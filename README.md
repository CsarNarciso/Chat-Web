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
  + Media  
* External Services
  + Gateway
  + Configuration
  + Discovery
* Dependencies
  + Global PostgreSQL DB
  + Global Redis Instance
  + Apache Kafka Server

![Actual Architecture Design](https://github.com/CsarNarciso/Assets/blob/main/Actual%20Chat%20Web%20System%20Design.png)


#### Prerequsistes

To use this project, you can either install the following thecnologies locally on your machine:

1. A kafka Server
2. And a Redis Server
   
Or use docker compose to run the compose file located in the project root directory (wich will create Kafka and Redis containers for you)

Note: either using a docker environment or a local one, the application is configured to use default redis (6379) and kafka (9492) ports. In case you are using the provided compose file, the default ports are already configured, in case not, you need to make sure the two servers are using the specified ports.

#### Set up the project:

1. **Clone the repository (or download directly)**
    ```bash 
    git clone https://github.com/CsarNarciso/Chat-Web.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd Chat-Web/
   ```
3. **Run [avaliable](#avaliable-services-at-the-moment) services using Maven (project folders already have Maven Wraper integrated, so you don't need to install it)**
       
   ```bash
   cd Service-Name/
   ```
   ```bash
   ./mvnw spring-boot:run
   ```
