FROM openjdk:22-jdk-alpine
WORKDIR /app
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} eureka.jar
RUN apk add --no-cache curl
ENTRYPOINT ["java", "-jar", "eureka.jar"]