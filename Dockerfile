FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build --no-daemon

# Ajuste: NÃO copie o .jar, apenas aponte diretamente
EXPOSE 8089

ENTRYPOINT ["java", "-jar", "build/libs/agenteqaIA-0.0.1-SNAPSHOT.jar"]
