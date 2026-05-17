# Estágio 1: Build da aplicação (sua máquina não precisa ter o Maven ou Java instalados)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copia os arquivos do projeto Saga
COPY pom.xml .
COPY src ./src

# Executa o comando do Maven para gerar o arquivo .jar (ignorando os testes para ser mais rápido)
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final para rodar a aplicação (mais leve)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no Estágio 1
COPY --from=builder /build/target/*.jar app.jar

# Define a porta que será exposta pelo contêiner
EXPOSE 8085

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]