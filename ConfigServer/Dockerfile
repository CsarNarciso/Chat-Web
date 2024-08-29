FROM openjdk:22-jdk-alpine
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} config.jar
RUN apk add --no-cache curl
ENTRYPOINT ["java", "-jar", "config.jar"]