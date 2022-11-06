# dagserver
Dag server based on quartz, allows to execute batch processes modeled as DAG (Direct Acyclic graph). Inspired by Apache Airflow

## Requirements

You must have a Quartz-compatible database installation. Currently only tested using Mysql type (MariaDB).

## Installation

Once this war is compiled, it can be run on the latest version of TOMCAT 9

## Basic configuration

The following are the basic configurations that are needed to run the application:

application.properties:
	param.jwt_secret: Secret of token JWT for authentication
	param.jwt_signer: Signer of token
	param.jwt_subject: Subject of the token
	param.jwt_ttl: Time to live
	param.folderpath: Path from where the JAR files will be loaded, which contain the implementation of the DAGs to be executed
	
log4j.properties:
	log4j.appender.file.File: Path from where the execution log file will be saved
	
	
quartz.properties:	
	org.quartz.dataSource.quartzDS.URL: Quartz engine database host
	org.quartz.dataSource.quartzDS.user: Quartz engine database user
	org.quartz.dataSource.quartzDS.password: Quartz engine database password
	
## Basic Usage

An example of a basic Dag is included in the repository , in the form of a maven project. The DAG implementation must be compiled into a JAR file and sent to the path configured in folderpath param.
The application does not yet have a front end to visualize the executions and the data in a friendly way, but it does have a GraphQL api, with which you can know the scheduled DAGs, the available DAGs, and the execution logs.

## Credentials:

GraphQL Endpoint:

URL: http://<serverhost>:<serverport>/server/query
Username: dagserver
Password: dagserver

The current graphql schema can be checked in the schema.graphql file located at the root of the classpath.
The application does not have the integrated graphql client (graphqli), but that does not prevent you from using an online version like <a href="https://lucasconstantino.github.io/graphiql-online/">this</a>