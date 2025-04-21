# Microservices Based Real Time Messaging Application.

![Old Final Chat Web System Design](https://github.com/CsarNarciso/Assets/blob/main/ChatWeb/OLD%20Final%20System%20Design.png)

## What this project is intended to do, the propouse of build this, and the final vision.
Focused on follow real world practices, architectures, design and requiriments (user experience, asyncronous communication avoiding tigh-coupling, performance) as an excelent way to get real/deep hands-on experience.

## User-level application features
- Custom profile image upload and update, or default one if none is provided.
- User details updating.
- Search for a specific user.
- Send message to a user to start a conversation (first time interaction).
- Messages delivered in real time.
- Conversations storing.
- Delete a conversation locally (it remails for the other participant).
- Conversation recreation on new message after a delete.
- User deletion
- Conversation messages remove when all participants has delete it or they have been deleted

## Technical-level features
- Development and Production Spring Profiles.
- Whole project deployment Docker integration using a separate compose file for each service and profile.
- Configuration management by Spring Cloud Config Server.
- Single point of access through Spring Cloud Gateway.
- Services APIs exposure with Open API Swagger for easier client integration, exposing docs access through Gateway.
- A Kafka broker for event asyncronous communication (decoupling some services).
- User service: handle user details data and integrates with Media service to access profile images. 
- Media service: works as a media storing server, handling profile images access. 
- Chat service: handle Conversations and Messages REST resources, using websockets connections by Spring Stopm WebSockets and SockJs for client side.
- PostgreSQL Relational Database.
- Redis Caching instance.

## What's comming next?
- Security (JWT, OAuth2.0)
- Integration with Cloud (AWS S3 Bucket for Media service, Gateway, RDS, or EC2 instances).
- Three different DBs (NoSQL Cassandra for Chat, Relational MySQL for User service, and PostgreSQL for Social).
- A Redis Cache instance for each services that requires it (this is most for optimization handling learning propouses, because, even if the real architecture is intended to work in this way, there is no need of all these instances for a local project, how can we have 3 reddis instances, and 3 different DBs running at the same time in our machine, without getting overhead?).
- Client SPA (Frontend Angular/React)
- A Jenkins pipeline.

## Technologies

1. Java 21
   + Core
   + Functional Programming (Java 8 Streams API)
2. Docker Compose
3. PostgreSQL
4. Redis (Jedis extension)
5. Kafka (Kraft mode)
6. Open API Swagger
7. Spring:
   + Profiles
   + Test (Unit and Integration)
     + JUnit5
	 + Mockito
	 + JaCoCo (Coverage)
   + Web (Rest)
   + Actuator
   + WebSockets
   + Data JPA
   + Hibernate
   + Model Mapper
8. Spring Cloud:
   + Eureka Server
   + Config Server
   + Gateway
   + FeignClient
   + Resilience4j Circuit Braker
9. SockJS
10. Maven 3.0+

| Spring Framework | Tools & Other Languages | Architectures | 
|-|-|-|
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=Spring%20Boot&logoColor=black&style=for-the-badge) ![Eureka](https://img.shields.io/badge/Eureka-6DB33F?style=for-the-badge&logo=Spring&logoColor=black) ![RestTemplate, FeignClient](https://img.shields.io/badge/RestTemplate,%20FeignClient%20-white?style=for-the-badge&logo=Spring&logoColor=6DB33F) ![Spring Api Gateway](https://img.shields.io/badge/Api%20Gateway-6DB33F?style=for-the-badge&logo=Spring&logoColor=black) ![Spring Config Server](https://img.shields.io/badge/Config%20Server-6DB33F?style=for-the-badge&logo=Spring&logoColor=black) ![Spring Circuit Breaker](https://img.shields.io/badge/Circuit%20Breaker-6DB33F?style=for-the-badge&logo=Spring&logoColor=black) ![Spring Sockets](https://img.shields.io/badge/Sockets-black?logo=Spring%20Boot&logoColor=6DB33F&style=for-the-badge) | ![Redis](https://img.shields.io/badge/Redis-black?style=for-the-badge&logo=redis&logoColor=D5540F) ![Docker](https://img.shields.io/badge/Docker-white?style=for-the-badge&logo=docker&logoColor=4DB1E0) ![Kafka](https://img.shields.io/badge/Apache%20Kafka-white?style=for-the-badge&logo=Apache%20Kafka&logoColor=black) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-6284A7?style=for-the-badge&logo=postgresql&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) ![Lombok](https://img.shields.io/badge/Lombok-red?style=for-the-badge) ![Eclipse](https://img.shields.io/badge/Eclipse%20IDE-2C2255?logo=Eclipse-IDE&logoColor=orange&style=for-the-badge) | ![Microservices](https://img.shields.io/badge/Microservices-00B9FF?style=for-the-badge) ![REST](https://img.shields.io/badge/Rest%20Api-green?style=for-the-badge) | 

## Avaliable Services At The Moment

* Internal:
  + User
  + Chat  
* External:
  + Media
  + Gateway
* Dependencies:
  + Redis instance
  + Apache Kafka Server

![Current Chat Web System Design](https://github.com/CsarNarciso/Assets/blob/main/ChatWeb/Current%20System%20Design.png)

## Running The Application

#### Prerequsistes

1. **Clone the repository using Git (or download directly)**
    ```bash 
    git clone https://github.com/CsarNarciso/Chat-Web.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd Chat-Web/
   ```

The following have to be installed locally:

1. Apache Kafka.
2. Redis.

 Or you can use Docker Compose for this:

1. To setup the whole project directly:
```bash 
   docker compose -f prod-compose.yaml up -d 
```
Then, just jump directly to the [next section](#using-the-application) to see how to use the application.

2. Or if you just want to create a Kafka and a Redis container:
```bash 
   docker compose -f docker-compose.common.yaml up -d 
```

Note: either using a Docker environment or a local one, the application is configured to use default redis (6379) and kafka (9492) ports. In case you are using the provided compose files, the default ports are already configured, in case not, you need to make sure these two tools are using the specified ports.


#### Setup

First, make sure to have  already completed the [previous section](#prerequisites) and have all the software required up and running.
Then, follow the steps below:

1. **Run the services using Maven (they already have Maven Wraper integrated, so you don't need to install it on your machine)**

   ```bash
   cd Service-Name/
   ```
   ```bash
   ./mvnw spring-boot:run -Dmaven.test.skip
   ```
   

## Using The Application

Note: Gateway is not necesary for internal microservices to work.

### Use Swagger (only for User service at the moment)

##### With Gateway: 

http://localhost:8000/v3/swagger-ui.html

##### No Gateway:

###### User Service
http://localhost:8001/user/swagger-ui.html

### Or use Postman instead

Download the Postman collections in JSON format and import them in Postman Desktop.

##### No Gateway:

https://www.postman.com/csarnarciso/chat-web/overview
