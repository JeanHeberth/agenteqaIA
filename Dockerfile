FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia todos os arquivos do projeto
COPY . .

# Dá permissão de execução para o gradlew (evita erro de permissão no Render)
RUN chmod +x ./gradlew

# Executa o build usando Gradle
RUN ./gradlew clean build --no-daemon

# Copia o JAR gerado para o ponto de entrada
COPY build/libs/agenteqaIA-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta usada pela aplicação
EXPOSE 8089

# Comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]
