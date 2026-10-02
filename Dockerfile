# Etapa 1: construir la aplicación
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos el proyecto Maven
COPY pom.xml .

# Descargamos dependencias
RUN mvn dependency:go-offline -B

# Copiamos el código fuente
COPY src ./src

# Generamos el JAR
RUN mvn clean package -DskipTests


# Etapa 2: ejecutar la aplicación
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el JAR generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]