FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar /app/booking.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","booking.jar"]