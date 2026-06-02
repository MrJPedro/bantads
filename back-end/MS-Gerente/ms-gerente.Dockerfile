FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

COPY gerente-service/gradlew .
COPY gerente-service/gradle ./gradle
COPY gerente-service/build.gradle* gerente-service/settings.gradle* ./

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY gerente-service/src ./src

RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]