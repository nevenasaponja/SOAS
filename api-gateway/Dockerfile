FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/api-gateway-1.0.0.jar api-gateway.jar

EXPOSE 8765

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]