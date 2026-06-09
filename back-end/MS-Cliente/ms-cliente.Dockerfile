FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build

COPY cliente/gradlew .
COPY cliente/gradle ./gradle
COPY cliente/build.gradle* cliente/settings.gradle* ./

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY cliente/src ./src

RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]