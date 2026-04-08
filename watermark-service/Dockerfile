FROM eclipse-temurin:11-jre AS builder

WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:11-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
