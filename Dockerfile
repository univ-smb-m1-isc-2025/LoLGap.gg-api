# Utilisez une image de base avec Java 17
FROM eclipse-temurin:21-jdk-alpine

# Définissez le répertoire de travail dans le conteneur
WORKDIR /app

# Copiez le fichier JAR de l'application dans le conteneur
COPY target/project-0.0.1-SNAPSHOT.jar app.jar

# Exposez le port sur lequel l'application va écouter
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]