FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/users-service-1.0.0.jar users-service.jar

EXPOSE 8770

ENTRYPOINT ["java", "-jar", "users-service.jar"]