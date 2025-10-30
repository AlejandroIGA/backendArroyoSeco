# --- Etapa 1: Build (Construcción) ---
# Usamos una imagen que tenga Maven y el JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos todo el código fuente al contenedor
COPY . /app

# Ejecutamos el build de Maven para crear el .jar
# -DskipTests acelera el build al omitir las pruebas (opcional)
RUN mvn clean package -DskipTests

# --- Etapa 2: Runtime (Ejecución) ---
# Usamos tu imagen base original (ligera, solo con JRE)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos SOLAMENTE el .jar desde la etapa 'build'
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar /app/booking.jar

EXPOSE 8080

# Este es tu entrypoint original, que ahora funcionará
ENTRYPOINT ["java","-jar","booking.jar"]