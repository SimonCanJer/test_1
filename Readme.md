# Inituit Challenge

### Given
   
- create microservices, which reads players  data from an .csv file  and retuns the readen data upon REST request.
- The common request method is GET and common sub uri is /api
- All players data are returned by means of get request for the url /api/players. 
- A particular player data is returned way get request from the url /apli/players/{playerId}
- Data are contained in an CSV file, which is applied.

### Assumptions:
-  We provide not fully industrial Big Data model.
-  We suppose that  number of Players are not more than 5 000 000 and number of instance are not more than 10
-  In the case of huge amount of data and a hight request density, model of a single (or not)microservice, which reads data and serves clients IS NOT APPLICABLE and another architecture should be used, when a reader microservice(scheduler) reads data from a huge file, inserts them in a document search engine (as Solr,NOT A RANDOM ACCESS DATABASE!!) and then notifies retrievers.
-  In the mentioned case of big data, application should run in number of instances and database update should be out of its business competence.It should be alerted to change of ready and HANDS-OFF (!) data source,as URL
-  In the last case requirement that "api/players" returns all players is simply not applicable and should be replaced by a query with paginated results
-  The microservice runs in an environment, which supports volumes, which are shared for volume to internal docker directory mapping

### Implementation in accordance to requirements.
-  Data are readen from the CSV file and in current implementation are kept in in memory map, because files are not large at this moment and  the data can be kept in the JVM memory. Database+ cache will be needed for large volume of data, that can be achieved by replacing implementatiion of the IDao interface.
-  Data are returned by a controller class com.intuit.test.rest.spring.RestEndPoint. 
-  The /api/players GET request should have the 200 response code. Alternativly the INTERNAL_SERVER_ERROR returned.
-  The /api/player/{playerId} returns a player only when it is found and in thes case response code is 200. Otherwise the ERROR_NOT_FOUND  response code returned .
-  The port by default is 10101. It can be changed by means of the server.port property and ENV_SERVER_PORT environhment variable from outside
-  The field playerID in the CSV is considered as identifier for retrieve operation


####  Structure
- The application has the main package is com.intuit.test
- the application has the following layers:
- - model (data model of the application)
- - rest.spring   (rest layer, includes end point and Api exposure)
- - source_read (source reader, CSV reader in this case)
-   spring (configuration and bean provider packages)
-   model has the sublayers
-  - dao (replacable data access)
-  - dao.api (exposes common dao interface)
-  - dao.in_mem (represents in memeory implementation of DAO)
-  - dao_solr staff for configuring application to be publisher of data are inserted in Solr
-  - dao.observer.file (observer of files and mounted volume)
-    rest layer currently has the only sublayer:
-  - spring (Spring controller,  and security configuration.
-  spring : all spring inherent configurations to created reader, dao,
-  observer beans. This separated pure POJO and concrete data porvider logics 
   from Spring staff, which is reponsible only for configuration.
## defaut mode
#### in memory map which is built on content of players,csv (or another) data files, which can be configured using environment variable.
    The file is observed and map substituted in backgound safely, without affection of incoming queries
## Solr mode.
#### The solution can be switched to Solr mode way of changing configuration by environment variables.
-    In this mode the appplication is pure publisher and interacts with Solr engine. Solar's url is managed both ways of environment variables values
     and values in files, which are placed in  volumes. It is a normal practice of config map mapping in K8S.
-    The files location is onserved, and implementor of Solr dao changes connection when files changes.
-    The work in froni of a real Solr engine (the solr docker) image is tested (com.intuit.test.model.dao.dao_solr.package com.intuit.test.model.dao.dao_solr)
    
### Service management
-  The system configuration is managed by Spring and properties/environment variables, which point to @Configuration classes of Spring. The structure provides to configure application in run time, depending current needs.
-  Location of file is defined by properties and environment variable ENV_DATA_SOURCE_FILE, which really shoud refer to a file location in a volume (of Kubernetes, for instance). In real case, we'll need monitor in side the application and file reload.
-  properties of the application are defined in the application.properties file. Key properties  are controlled by an environment variables.

## Server workflow
-  Initialized by Spring application.
-  It exposes automatically documented REST API (com.intuit.test.rest.spring.RestEndPoint.java)
-  It creates, activates and injects reader and dao beans into top level, rest, layer upon data initially loaded.
-  starts monitoring data volume, where source is readen from.
-  It detects  changes in source data file and notifies Dao objects (com.intuit.test.model.dao.in_mem.*) for chanages
-  It reloads data upon changes detected in source file using the Double Buffer principle, no simultanrous read and write

##Comments.
#### Why Solr proposed?
     It is one of search engines, which are dedicated for search.
#### Why Kafka/RabbitMQ is not used for notifications?
     It is not a volume of work for Kafka and another message Q.There are another cloud provier inherent mechanisms
### Why not Spring repositories:
- Spring inherented.
- It has number of layers of reflection driven staff, from hibernate, to EntityManager.

## configurable features and config variables:
- server port managed by the ENV_SERVER_PORT, by default it is 10101 
- document API url is managed by the ENV_API_DOCS variable, default is /api-docs
- specific public access URI list is managed by ENV_OPEN_URIS variable, default is /api-docs
- is api protected is managed by the variable ENV_PROTECT_API: default value is false
- Title of docs API is managed by the ENV_API_DOCS_TITLE: default is Player API test.
- API  docs version is managed by the external variable ENV_API_DECS_VERSION, default is 1.0-SNAPSHOT
- Api doc description is managed by the ENV_API_DOCS_DESCRIPTION variable, default is The Api exposes get methods to query and retrieve information}
- CSV file location is managed by ENV_DATA_SOURCE_FILE, default is sources/player.csv.
- Notice to the previous: the default location is oriented for testing mainly, but it MUST BE ULTIMATIVELLY defined for containers way of mapping -v [MY_CSV_SOURCE]:/etc/intuit/source (in the docker run command line and the environment variable is setted properly - its directory/volume must be mapped on the container directory) 
- File observation for updates is managed by the ENV_CHRON_OBSERVER vatiable, default value is "0 0 0-23 * * *, that means every one clock's houer.
  
##    Undefined important topics.
- Security. Currently, api is configured to be a free for access. However, secuity can be set to up by changing security porvider.
- Data size.  We need another architecture for really scalable application Big Data (if it applicable for a players).
- In the case of big amount of data, the /api/players GET request must contain pagination parameters, because all data could have a huge size.
- SLI/PKI metrics must be defined. I have added a custom metrics for all players and concrete player retrieve counters, but we should define others (as timing)




##     Project reorganization 
- really project must be divided by libraries as a set of micro-projects with mounted build, where POJO classes can be reused  by frameworks as Spring, Micronaut/Quarcus.





