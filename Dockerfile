# --- Fase 1: Build ---
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# --- Fase 2: Runtime ---
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /build/target/AlquilaTusVehiculos-0.0.1-SNAPSHOT.jar app.jar

COPY --from=build /build/src/main/resources/templates ./resources/templates
COPY --from=build /build/src/main/resources/static ./resources/static

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", \
  "--spring.thymeleaf.prefix=file:/app/resources/templates/", \
  "--spring.web.resources.static-locations=file:/app/resources/static/"]
