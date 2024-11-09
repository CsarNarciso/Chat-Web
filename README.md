# Microservices Based Real Time Messaging Application using Spring Boot and WebSockets.

![Architecture Design](https://github.com/CsarNarciso/Assets/blob/main/chat-architecture-desing.png)
https://drive.google.com/file/d/1V3u7YTs40QCPeiNQBHlbjsy3eRtgeP_G/view?usp=sharing
## Application features.

- Docker compose integration for production environment.
- User useful data (id and name) storaged in session, not in model, for application performance, by reduicng database calls when    client refresh page or reconnect.
- When users login, can see online users in real time.
- When a user discconnect, server remembers it until passed 1 minute. If user doesn't reconnect, it is forgot, and online users     list is updated to everyone in chat (this in real time).
- Users can update their profile image or name. Each change is showed to everyone else in real time.
- Text messages sending functionality.
- Delete or see actual conversations data (as how many messages without read user has).

![Architecture Diagram](https://github.com/CsarNarciso/Assets/blob/main/Chat%20Web%20Architecture%20Diagram.png)

## Table of Contents
* [Technologies](#technologies)
* [Getting started](#getting-started)
  + [Prerequisites](#prerequisites)
  + [Running the application](#running-the-application)
  + [Using the application](#using-the-application)
* [Contact me](#contact-me)

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
   
## Getting Started

### Prerequisites
Optionally, to use this project, you need the following thecnologies installed on your machine:
1. Git
2. Docker Compose

### Running the Application
Follow these steps to set up the project locally:

1. **Clone the repository (or download directly)**
    ```bash 
    git clone https://github.com/CsarNarciso/Chat-Web.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd Chat-Web/
   ```
3. **Build and start the microservices**

   Note! Take in mind that with docker integration during start up, project will be build in production mode (prod profile), wich means the following:
     - Each one of these microservices (User, Chat, Social) will use a different docker container DB instance.
     - Each one of the internal microservices will use a Redis docker container instance.
     - A Kafka (in Kraft mode) container will be created for all internal microservices.
     - It will result in a total of 16 containers running on your machine, so, performance will slow down in a huge significantly way.
       
   ```bash
   docker-compose up -d
   ```
   This command will create and run each microservice container using its image stored in Docker Hub. The **-d** flag runs the containers in the background, to view the logs use:
   ```bash
   docker logs microservice-container-name
   ```
5. Now, you should see the following output, wich indicates everything was built correct
   ![Project set up successfully](readme-images/project-set-up-successfully)

6. Also, you can check the created images with:
   ```bash
   docker images
   ```
7. To check the running containers:
   ```bash
   docker ps
   ```
8. For networks and volumes used by the project, use:
   ```bash
   docker network ls
   docker volume ls
   ``` 

### Using the Application

1. With all microservices running, you can access to the chat by the following endpoint:

```bash
http://localhost:8006
```

or

```bash
http://localhost:8006/login
```

This because if you are not authenticated, the application will automatically redirect you to login page.

2. Finally, to stop and remove from your system all the docker components created by the project, use:
 ```bash
 docker-compose down
 ```

## Contact Me
* [LinkedIn](https://www.linkedin.com/in/cesar-pozol-narciso-b48727180/)

