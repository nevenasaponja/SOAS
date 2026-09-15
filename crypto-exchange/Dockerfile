FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/crypto-exchange-1.0.0.jar crypto-exchange.jar

EXPOSE 8400

ENTRYPOINT ["java", "-jar", "crypto-exchange.jar"]