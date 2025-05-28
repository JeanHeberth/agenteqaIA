
# Etapa de build
FROM gradle:8.6.0-jdk21 AS build

# Instala dependências nativas do Tesseract
RUN apk add --no-cache \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd \
    tesseract-data \
    curl


WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Etapa de execução
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Porta padrão do backend
EXPOSE 8089

# Entrada da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
