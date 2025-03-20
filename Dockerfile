# Usar uma imagem base com JDK 21
FROM eclipse-temurin:21-jdk-jammy

# Diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar o arquivo JAR da aplicação para o contêiner
COPY target/transactions-0.0.1-SNAPSHOT.jar transactions.jar

# Expor a porta que a aplicação Spring Boot usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "transactions.jar"]