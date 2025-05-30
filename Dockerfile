# Etapa de build com Gradle
FROM gradle:8.6.0-jdk21 AS build

WORKDIR /app
COPY . .

# Compila o projeto sem depender do Gradle wrapper
RUN gradle bootJar --no-daemon

# Etapa de execução com Tesseract funcional
FROM ubuntu:22.04

# Instala Java 21, Tesseract e libs nativas necessárias
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-osd \
    libleptonica-dev \
    libtesseract-dev \
    libjpeg-dev \
    libpng-dev \
    libtiff-dev \
    zlib1g-dev \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Define variáveis para uso no Java
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata
ENV TESS_LANG=por
ENV JNA_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu

# Cria diretório do app
WORKDIR /app

# Copia o .jar da etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Expõe a porta padrão da aplicação
EXPOSE 8089

# Executa o .jar
ENTRYPOINT ["java", "-jar", "app.jar"]
