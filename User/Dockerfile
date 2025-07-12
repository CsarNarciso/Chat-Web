FROM openjdk:22-jdk-slim-buster AS build
WORKDIR /app

#Build jar
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests


FROM openjdk:22-jdk-slim-buster
WORKDIR /app

#Install curl
RUN apt-get update && apt-get install -y curl

#Run the application
COPY --from=build /app/target/*.jar program.jar
ENTRYPOINT ["java", "-jar", "program.jar"]
