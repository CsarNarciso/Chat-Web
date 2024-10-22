FROM ubuntu:latest
RUN apt-get install -y curl
FROM openjdk:22
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} media.jar
ENTRYPOINT ["java", "-jar", "media.jar"]