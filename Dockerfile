# Build stage
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY .env ./
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/project-0.0.1-SNAPSHOT.jar app.jar
COPY .env ./

# Exposez le port sur lequel l'application va écouter
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]