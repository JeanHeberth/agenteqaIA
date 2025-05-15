# Usa a imagem oficial com JDK 21
FROM eclipse-temurin:21-jdk

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR gerado pelo Gradle (ajuste o nome do JAR se necessário)
COPY build/libs/*.jar app.jar

# Expõe a porta 8089
EXPOSE 8089

# Comando para iniciar sua aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
