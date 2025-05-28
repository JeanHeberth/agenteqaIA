# Etapa de build
FROM gradle:8.6.0-jdk21 AS build

# Instala dependências do sistema (OCR)
RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Variável de ambiente do Tesseract
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata

WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# Etapa de execução
FROM eclipse-temurin:21-jdk

# Copia binários do Tesseract da etapa anterior
COPY --from=build /usr/share/tesseract-ocr /usr/share/tesseract-ocr
COPY --from=build /usr/bin/tesseract /usr/bin/tesseract
COPY --from=build /usr/lib /usr/lib
COPY --from=build /lib /lib

# Define variáveis de ambiente para a execução
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
ENV TESS_LANG=por

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
