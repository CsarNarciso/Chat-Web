# Real Time Messaging Application using Spring Boot WebSockets and SockJS.

I'm working in this chat project where the main point is to always prioritize project scalability, performance, and security practices. I'm going to apply Microservice Architecture using Spring Cloud technologies and everything will be managed by Docker Compose.

## Table of Contents
* [Technologies](#technologies)
* [Getting started](#getting-started)
  + [Prerequisites](#prerequisites)
  + [Running the application](#running-the-application)
  + [Using the application](#using-the-application)
* [How to build this project](#how-to-build-this-project)
* [Tests](#tests)
* [Contact me](#contact-me)


## Technologies
1. Java 22
2. Docker Compose
3. PostgreSQL, MySQL
4. Spring Boot:
   + WebSockets
   + JPA
   + Hibernate
6. Spring Security
7. SockJS
8. Maven 3.0+
   
## Getting Started

### Prerequisites
To use this project, you must have the following installed:
1. Git
2. Docker Compose

### Running the Application
Follow these steps to set up the project locally:

1. **Clone the repository**
    ```bash 
    git clone https://github.com/CsarNarciso/Chat-Web.git 
    ```
2. **Navigate to the project root directory**
   ```bash
   cd Chat-Web/
   ```
3. **Build and start the microservices**
   ```bash
   docker-compose up -d
   ```
   This command will create and run each microservice container using its image stored in Docker Hub. The **-d** flag runs the containers in the background, to view the logs use:
   ```bash
   docker logs microservice-container-name
   ```
4. Now, you should see the following output, wich indicates everything was built correct
   ![Project set up successfully](readme-images/project-set-up-successfully)

5. Also, you can check the created images with:
   ```bash
   docker images
   ```
6. To check the running containers:
   ```bash
   docker ps
   ```
7. For networks and volumes used by the project, use:
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


Application features.
-
- User login and registration.
- Authentication required to access chat pages/functionalities.
- User data (messages, conversations, and profile images) stored in database.
- User useful data (id and name) storaged in session, not in model, for application performance, by reduicng database calls when    client refresh page or reconnect.
- When users login, can see online users in real time.
- When a user discconnect, server remembers it until passed 1 minute. If user doesn't reconnect, it is forgot, and online users     list is updated to everyone in chat (this in real time).
- Users can update their profile image or name. Each change is showed to everyone else in real time.
- Text messages sending functionality.
- Delete or see actual conversations data (as how many messages without read user has).

## How to Build this Project

## Tests

## Why I migrated the project?
I found that the whole system had major problems due to lack of scalability, also, code was not easy to read/maintain following this way because I plan to add more features later.
Now you can see this application repository [here](https://github.com/CsarNarciso/Chat-Web)

## Contact Me
* [LinkedIn](https://www.linkedin.com/in/cesar-pozol-narciso-b48727180/)

