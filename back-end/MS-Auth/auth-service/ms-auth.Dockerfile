FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV PORT=8081
EXPOSE 8081
COPY build/libs/*.jar /app/
RUN rm -f /app/*-plain.jar && mv /app/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
