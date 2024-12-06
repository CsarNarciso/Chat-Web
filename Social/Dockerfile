FROM ubuntu:latest
RUN apt-get install -y curl
FROM openjdk:21
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} social.jar
ENTRYPOINT ["java", "-jar", "social.jar"]