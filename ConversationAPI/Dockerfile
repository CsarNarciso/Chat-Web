FROM ubuntu:latest
RUN apt-get install -y curl
FROM openjdk:22
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} conversation.jar
ENTRYPOINT ["java", "-jar", "conversation.jar"]