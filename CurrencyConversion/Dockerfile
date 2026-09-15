FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/currency-conversion-1.0.0.jar currency-conversion.jar

EXPOSE 8100

ENTRYPOINT ["java", "-jar", "currency-conversion.jar"]