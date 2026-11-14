# ---------- Etapa 1: compilar ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1) Solo el pom: así Docker cachea las dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 2) Ahora el código fuente y compilamos el jar
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

# Usuario sin privilegios (no correr como root)
RUN groupadd --system spring && useradd --system --gid spring spring

# Copiamos SOLO el jar desde la etapa "build"
COPY --from=build /app/target/*.jar app.jar

USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]