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
RUN apt-get update && \
    apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
ENV JNA_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu
ENV TESS_LANG=por

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
