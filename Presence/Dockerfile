FROM ubuntu:latest
RUN apt-get install -y curl
FROM openjdk:21
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} presence.jar
ENTRYPOINT ["java", "-jar", "presence.jar"]