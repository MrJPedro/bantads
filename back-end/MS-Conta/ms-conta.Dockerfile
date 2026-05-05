# Template de Dockerfile para o microsserviço MS-Conta
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV PORT=8083
EXPOSE 8083
COPY conta-service/build/libs/*.jar /app/
RUN rm -f /app/*-plain.jar && mv /app/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
