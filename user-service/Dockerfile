FROM eclipse-temurin:11-jre AS builder
WORKDIR app
ARG DEPENDENCY=target/dependency
COPY target/dependency/* /usr/local/lib/
COPY target/app.jar /app/app.jar

FROM eclipse-temurin:11-jre
WORKDIR app
COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
