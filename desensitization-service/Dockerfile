FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/desensitization-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
