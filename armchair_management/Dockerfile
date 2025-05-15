# Etapa de build: utiliza Maven com JDK para compilar a aplicação.
FROM maven:3.9.9-eclipse-temurin-21-alpine  AS builder

# Alguns metadados relevantes para documentação.
LABEL authors="nielo"

# Define o diretório de trabalho do build.
WORKDIR /app
#COPY .mvn ./.mvn
#COPY mvnw ./
#COPY mvnw.cmd ./

# Copia apenas o pom.xml para aproveitar cache das dependências no Docker.
COPY pom.xml ./

# Resolve as dependências do Maven antes de copiar o código.
RUN mvn dependency:resolve

# Copia o código-fonte da aplicação para o container.
COPY src ./src

# Compila e empacota a aplicação em um arquivo .jar.
RUN mvn clean package

# Etapa final: imagem com apenas o JRE para executar a aplicação.
FROM eclipse-temurin:22-jre-jammy

# Define o diretório de trabalho do runtime.
WORKDIR /app

# Instala o curl para suportar healthchecks via Docker Compose.
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copia o .jar gerado na etapa de build.
COPY --from=builder /app/target/*.jar app.jar

# Copia o agente do OpenTelemetry para rastreamento.
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar

# Configura a execução automática do agente de observabilidade.
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

# Variáveis de ambiente do OpenTelemetry.
ENV OTEL_SERVICE_NAME=seatManager-API
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://alloy:4320
#ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://collector:4318
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_EXPORTER_OTLP_LOGS_ENDPOINT=http://alloy:4444/v1/logs
ENV OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf

# Expõe a porta padrão da aplicação.
EXPOSE 8080

# Comando para iniciar a aplicação.
CMD ["java", "-jar", "app.jar"]

