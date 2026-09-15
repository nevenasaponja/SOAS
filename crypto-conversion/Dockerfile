FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/crypto-conversion-1.0.0.jar crypto-conversion.jar

EXPOSE 8500

ENTRYPOINT ["java", "-jar", "crypto-conversion.jar"]