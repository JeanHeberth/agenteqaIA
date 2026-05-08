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

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
ENV TESS_LANG=por

WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Etapa de execução
FROM eclipse-temurin:21-jdk
WORKDIR /app

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
ENV TESS_LANG=por

# Copia as bibliotecas e arquivos necessários
COPY --from=build /usr/lib/x86_64-linux-gnu/libtesseract.so* /usr/lib/x86_64-linux-gnu/
COPY --from=build /app/build/libs/*.jar app.jar

# Porta do app
EXPOSE 8089

# Corrigido: adicionando jna.library.path
ENTRYPOINT ["java", "-Djna.library.path=/usr/lib/x86_64-linux-gnu/", "-jar", "app.jar"]
