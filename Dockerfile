# Etapa de build: compila o projeto e gera o JAR
FROM gradle:8.6.0-jdk21 AS build

# Instala dependências do sistema, incluindo Tesseract e o idioma português
RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Define o caminho real do tessdata
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
ENV TESS_LANG=por

WORKDIR /app
COPY . .

# Gera o JAR
RUN gradle bootJar --no-daemon

# Etapa de execução: imagem menor apenas para rodar o app
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Define novamente as variáveis de ambiente na imagem final
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
ENV TESS_LANG=por

# Copia o JAR gerado na etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8089

# Executa o JAR
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
