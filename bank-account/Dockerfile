FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/bank-account-1.0.0.jar bank-account.jar

EXPOSE 8200

ENTRYPOINT ["java", "-jar", "bank-account.jar"]