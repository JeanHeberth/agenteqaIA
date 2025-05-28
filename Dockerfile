# Etapa de build
FROM gradle:8.6.0-jdk21 AS build

RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
ENV TESS_LANG=por

WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Etapa de execução
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o jar final
COPY --from=build /app/build/libs/*.jar app.jar

# Expõe a porta padrão
EXPOSE 8089

# Permite que variáveis de ambiente sejam utilizadas no entrypoint
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
