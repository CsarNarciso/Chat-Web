# Microservices Based Real Time Messaging Application using Spring Boot and WebSockets.

![Final Architecture Design](https://github.com/CsarNarciso/Assets/blob/main/Final%20Chat%20Web%20System%20Design.png)

## Application features
- User registration and login.
- Custom profile image upload and update, or default one if none is provided.
- User details updating.
- Search for a specific user.
- Send message to a user to start a conversation (first time interaction).
- Messages delivered in real time.
- Conversations storing.
- Delete a conversation locally (it remails for the other participant).
- Conversation recreation on new message after a delete.
- Users presence status tracking.
- User data and presence updates in real time for user relationships (based on conversations)
- User delete (along with their conversations and messages)

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
  + Configuration
  + Discovery
* Dependencies:
  + Global PostgreSQL DB
  + Global Redis Instance
  + Apache Kafka Server

![Actual Architecture Design](https://github.com/CsarNarciso/Assets/blob/main/Actual%20Chat%20Web%20System%20Design.png)

## Running The Application

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

In case your are going to use 'prod' profile, then you will need Docker Compose installed on your machine. Then, just jump directly to the [next section](#setup) to start with the setup. 

If you don't, either install locally the following:

1. A Kafka Server
2. A Redis Server
3. (Only for 'dev-1' profile) A PostgreSQL DB

Or use docker compose to execute the compose file for non production profiles located in the project root directory (wich will create Kafka, Postgres and Redis containers for you):
```bash 
   docker compose -f local-compose.yaml up -d 
```

Note: either using a docker environment or a local one, the application is configured to use default redis (6379), postgres (5432) and kafka (9492) ports. In case you are using the provided compose file, the default ports are already configured, in case not, you need to make sure the three technologies are using the specified ports.
Both Kafka and Redis works with the default configurations.
If you are going to use Postgres, then configure it with a new user called 'postgres' and 'letmein' as password

#### Setup

For 'prod' profile, simply run the following command to setup the whole project:
```bash 
   docker compose up -d 
```
Once everything is setup successfully, jump directly to the [next section](#using-the-application) to see how to use the application

If you are not using 'prod' profiles, make sure to have  already completed the [previous section](#prerequisites) and have all the software required up and running.
Then, follow the steps below:

1. **Clone the repository using Git (or download directly)**
    ```bash 
    git clone https://github.com/CsarNarciso/Chat-Web.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd Chat-Web/
   ```
3. **Run [avaliable](#avaliable-services-at-the-moment) and required (based on [profiles](#about-profiles)) services using Maven (project folders already have Maven Wraper integrated, so you don't need to install it)**
       
   ```bash
   cd Service-Name/
   ```
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=profile-name
   ```

## Using The Application

Note: even Gateway is not necesary for internal microservices to work, if you want to use it, then replace specific service port for 8000 (default Gateway port) in endpoints urls.

#### User Service

Base url: http://localhost:8001/users

| Name | Path | Method | Body | Media Type | Parameters | Path variables
|-|-|-|-|-|-|-|
| create | none | POST | String username, String email, String password, MultipartFile imageMetadata | form-data | none | none |
| getById | /id | GET | none | application-json | none | Long id |
| getByIds | none | GET | List< Long > ids | application-json | none | none |
| updateDetails | /id | PATCH | String username, String email | application-json | none | Long id |
| updateProfileImage | /id | PUT | MultipartFile imageMetadata | form-data | none | Long id |
| delete | /id | DELETE | none | application-json | none | Long id |

#### Chat Service

Base urls: 

http://localhost:8002/conversations
   
| Name | Path | Method | Body | Media Type | Parameters | Path variables
|-|-|-|-|-|-|-|
| createOnFirstInteractionMessage | none | POST | Long senderId, Long recipientId, String content | application-json | none | none |
| loadUserConversations | /userId | GET | none | application-json | none | Long userId |
| delete | /conversationId/userId | DELETE | none | application-json | none | UUID conversationId, Long userId |
   
http://localhost:8002/messages

| Name | Path | Method | Body | Media Type | Parameters | Path variables
|-|-|-|-|-|-|-|
| send | none | POST | UUID conversationId, Long senderId, String content, boolean recreateForSomeone | application-json | none | none |
| loadMessages | /conversationId | GET | none | application-json | none | UUID conversationId |
| markAsRead | /conversationId/readerId | PUT | none | application-json | none | UUID conversationId, Long readerId |

