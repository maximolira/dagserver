package main.infra.adapters.input.graphql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import main.application.ports.input.LoginUseCase;
import main.application.ports.input.SchedulerQueryUseCase;
import main.domain.entities.Log;
import main.domain.types.Available;
import main.domain.types.LogEntry;
import main.domain.types.Scheduled;


@Component
public class QueryResolver implements GraphQLQueryResolver {
	
	private final static Logger logger = Logger.getLogger(QueryResolver.class);
	
	@Autowired
	SchedulerQueryUseCase handler;
	
	@Autowired
	LoginUseCase login;
	
	public String login(String username,String pwdhash) throws Exception {
		String token = login.apply(Arrays.asList(username, pwdhash));
		return token;
	}
	
	public List<Scheduled> scheduledJobs() throws Exception {
        var rv = new ArrayList<Scheduled>();
		var list = handler.listScheduledJobs();
        for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			var scheduled = new Scheduled();
			scheduled.setGroupname(map.get("jobgroup").toString());
			scheduled.setDagname(map.get("jobname").toString());
			if(map.get("nextFireAt") != null) {
				Date nfat = (Date) map.get("nextFireAt");
				scheduled.setNextFireAt(nfat.getTime());	
			} else {
				scheduled.setNextFireAt(0L);
			}
			scheduled.setEventTrigger(map.get("eventTrigger").toString());
			rv.add(scheduled);
		}
        return rv;
    }	
	public List<Available> availableJobs() throws Exception{
		var operators = handler.availableJobs();
		var keys = operators.keySet();
		var rv = new ArrayList<Available>();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			var props = operators.get(string);
			for (Iterator<Map<String,String>> iterator2 = props.iterator(); iterator2.hasNext();) {
				var operatormap = iterator2.next();
				var operator = new Available();
				operator.setJarname(string);
				operator.setClassname(operatormap.get("classname"));
				operator.setCronExpr(operatormap.get("cronExpr"));
				operator.setGroupname(operatormap.get("groupname"));
				operator.setDagname(operatormap.get("dagname"));
				operator.setTriggerEvent("CRON STATEMENT");
				rv.add(operator);	
			}

		}
		rv.addAll(getDefaults());
		return rv;
	}
	private List<Available> getDefaults(){
		var rv = new ArrayList<Available>();
		var bsd = new Available();
		bsd.setJarname("SYSTEM");
		bsd.setClassname("main.domain.dags.BackgroundSystemDag");
		bsd.setCronExpr("0 0/1 * * * ?");
		bsd.setDagname("background_system_dag");
		bsd.setGroupname("system_dags");
		bsd.setTriggerEvent("CRON STATEMENT");
		rv.add(bsd);
		
		var events = new Available();
		events.setJarname("SYSTEM");
		events.setClassname("main.domain.dags.EventSystemDag");
		events.setDagname("event_system_dag");
		events.setGroupname("system_dags");
		events.setTriggerEvent("OnEnd");
		events.setTargetDagname("background_system_dag");
		rv.add(events);
		
		return rv;
	}
	public List<LogEntry> logs(String dagname){
		var arr = handler.getLogs(dagname);
		var rv = new ArrayList<LogEntry>();
		for (Iterator<Log> iterator = arr.iterator(); iterator.hasNext();) {
			Log log =  iterator.next();
			var entry = new LogEntry();
			entry.setDagname(log.getDagname());
			entry.setExecDt(log.getExecDt().getTime());
			entry.setId(log.getId());
			entry.setValue(log.getValue());
			rv.add(entry);
		}
		return rv;
	}
	
}
