FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/currency-exchange-1.0.0.jar currency-exchange.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "currency-exchange.jar"]