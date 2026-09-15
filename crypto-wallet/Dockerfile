FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/crypto-wallet-1.0.0.jar crypto-wallet.jar

EXPOSE 8300

ENTRYPOINT ["java", "-jar", "crypto-wallet.jar"]