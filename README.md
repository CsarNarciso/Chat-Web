# Microservices Based Real Time Messaging Application using Spring Boot and WebSockets.

![Final Architecture Design](https://github.com/CsarNarciso/Assets/blob/main/Final%20Chat%20Web%20System%20Design.png)

## Technologies
1. Java 21
2. Docker Compose
3. PostgreSQL, MySQL, CassandraDB
4. Redis (Jedis extension)
5. Kafka (Kraft mode)
6. Spring:
   + REST
   + Actuator
   + WebSockets
   + Data JPA
   + Hibernate
8. Spring Cloud:
   + Eureka Server
   + Config Server
   + FeignClient
   + Resilience4j Circuit Braker
10. SockJS
11. Maven 3.0+
   
## Running the Application

#### Avaliable services at the moment


* Internal microservices:
  + User  
* External Services
  + Media
  + Gateway
  + Configuration
  + Discovery
* Dependencies
  + Global PostgreSQL DB
  + Global Redis Instance
  + Apache Kafka Server

![Actual Architecture Design](https://github.com/CsarNarciso/Assets/blob/main/Actual%20Chat%20Web%20System%20Design.png)

#### About Profiles
This application right now can work with the following Spring Profiles:

1. Dev (Local development):
   + No Docker (compose) deployment integration
   + No Eureka (Discovery service) requirment
   + No Config Server (configuration service) requirment
   + H2 Database in memory instead PostgreSQL
3. Dev-2
   + No Docker deployment
   + Eureka and Config Server requirment
   + PostgreSQL integration
5. Prod (Production)
   + Whole project deployment using docker compose
   + Eureka and Config Server requirment
   + PostgreSQL integration

Choose 'dev' profile for fast and light internal services testing environment. Dev profile will disable Discovery and Configuration services dependencies, so you don't have to run them to work.

Choose 'dev-2' profile for fast and light internal as external services testing environment. Chosse this if you need to work with a production like environment without depend on docker compose for the full application setup (wich will lead on slow down your machine performance).

Choose 'prod' profile for full project production environment setup. This will lead in a total of 11 docker containers running on your machine. 

#### Prerequsistes

To use this project, you must have the following installed:

1. A kafka Server
2. A Redis Server
3. (Depending on profile) A PostgreSQL DB
   
You can either install them locally, or use docker compose to run the compose file located in the project root directory (wich will create Kafka, Postgres and Redis containers for you)

Note: either using a docker environment or a local one, the application is configured to use default redis (6379), postgres (5432) and kafka (9492) ports. In case you are using the provided compose file, the default ports are already configured, in case not, you need to make sure the three technologies are using the specified ports.

#### Set up the project:

1. **Clone the repository (or download directly)**
    ```bash 
    git clone https://github.com/CsarNarciso/Chat-Web.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd Chat-Web/
   ```
3. **Run [avaliable services](#avaliable-services-at-the-moment) using Maven (project folders already have Maven Wraper integrated, so you don't need to install it)**
       
   ```bash
   cd Service-Name/
   ```
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=profile-name
   ```
