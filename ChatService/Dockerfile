FROM ubuntu:latest
RUN apt-get install -y curl
FROM openjdk:22
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} chat.jar
ENTRYPOINT ["java", "-jar", "chat.jar"]