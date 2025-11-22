FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /home/gradle/app
COPY --chown=gradle:gradle . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /home/gradle/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
