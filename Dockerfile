# Build stage - Use Maven to build the app (only for first build)
FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app

# Copy required files for Maven build
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY .env ./

# Install dependencies (skip tests for now)
RUN ./mvnw clean install -DskipTests

# Runtime stage - Use the base JDK image for running the app
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Install Maven so we can run the Spring Boot application directly
RUN apk add --no-cache maven

# Copy everything from the build stage (including the jar, for first run)
COPY --from=build /app /app

# Expose port 8080
EXPOSE 8080

# Command to run the app (in development mode using spring-boot:run)
ENTRYPOINT ["./mvnw", "spring-boot:run"]
