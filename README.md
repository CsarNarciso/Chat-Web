Project description.
-
I'm working in a real time messaging application developed with Spring Boot, WebSockets and SockJS. The main point of this learning journey is to always priorize project scalability, performance, and security practices. 

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

How it was built?
-
- Developed server-side web socket functionality by Spring WebSockets, and get connection to client via SockJs.
- Set up server connection to MySQL database.
- Used Hibernate validator and custom anotations for user registration process.
- Implemented Js for profile image size verification and client-side functionalities.
- Configured login behavior and user session HTTP only through Spring Security.
- Used Bootstrap library, Html, Css for client-side design. 
- Built main chat entities (users, messages, conversations) through JPA.
- Migrated project to new one for clean architecture. 
- Built files REST API for users profile images uploading.
- Scaleted application by separate services to reach a microservices architecture. 

Complete technology stack.
-
- Java 22.
- Spring Boot.
- Spring Security.
- Spring WebSockets.
- Hibernate validation.
- SockJs.
- Js.
- Html.
- Css.
- Bootstrap.
- REST.
- JPA.
- MySQL database.
- H2 database.
- ModelMapper.
- Maven.
- Git.

Why I migrate the project?
-
  I found the main structure in trouble due to lack of scalability for microservices architecture.
