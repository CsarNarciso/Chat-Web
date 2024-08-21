FROM openjdk:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE_PATH=./target/*.jar
ARG JAR_FILE_NAME=chat.jar
COPY ${JAR_FILE_PATH} ${JAR_FILE_NAME}
RUN apk add --no-cache curl
CMD ["java", "-jar", "${JAR_FILE_NAME}"] 