FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/naming-server-1.0.0.jar naming-server.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "naming-server.jar"]