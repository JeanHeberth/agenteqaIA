# Etapa de execução
FROM ubuntu:22.04

# Instala dependências, Tesseract e pacotes de idioma
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd \
    libleptonica-dev \
    libtesseract-dev \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Caminho correto onde os arquivos .traineddata são instalados
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
ENV JNA_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu
ENV TESS_LANG=por

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]


RUN ls -l /usr/share/tesseract-ocr/4.00/tessdata
