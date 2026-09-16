FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/electricity-billing-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]