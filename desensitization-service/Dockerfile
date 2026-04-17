FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/desensitization-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
