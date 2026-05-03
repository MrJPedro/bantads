FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV PORT=8082
EXPOSE 8082
COPY gerente-service/build/libs/*.jar /app/
RUN rm -f /app/*-plain.jar && mv /app/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]