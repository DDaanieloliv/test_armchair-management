# Para construir uma Imagem do nosso projeto Java spring-boot.

# Definimos uma Imagem base que nos ajudara a fazer o build de nossa aplicação.
# Tambem definimos como nos referênciaremos a essa etapa do build multi-stage.
FROM maven:3.9.9-eclipse-temurin-21-alpine  AS builder

# Alguns metadados relevantes para documentação.
LABEL authors="nielo"

# Definimos o WORKDIR, diretório onde manipularemos os arquivos necessários para o build.
WORKDIR /app
#COPY .mvn ./.mvn
#COPY mvnw ./
#COPY mvnw.cmd ./

# Como queremos usar o cacheamento do Docker, copiamos para o WORKDIR apenas o arquivo pom.xml
# para não o termos que a todo momento que alterarmos algo em 'src' recarregarmos todas as dependêcias
# do pom.xml. Melhorando a perforamance de nosso build devido já possuirmos esse camada em nosso cache.
COPY pom.xml ./

# O comando `mvn dependency:resolve` é utilizado para baixar e armazenar localmente todas as dependências do projeto,
# garantindo que elas estejam disponíveis antes do processo de build.
# Isso melhora o cacheamento do Docker, pois evita que dependências sejam baixadas novamente
# a menos que o `pom.xml` seja alterado.
#
# Ele busca todas as dependências definidas no `pom.xml` e as armazena no repositório local do Maven,
# que geralmente fica no diretório `~/.m2/repository/`.
#
# Dessa forma, quando o código-fonte do projeto for copiado e compilado posteriormente,
# as dependências já estarão resolvidas, tornando o build mais eficiente e rápido.
RUN mvn dependency:resolve

# Após todas as dependências serem resolvidas, copiamos a pasta `src/`, que contém o código-fonte da aplicação.
# Como todas as dependências já foram baixadas previamente, agora podemos compilar o código sem precisar baixá-las novamente.
# Isso garante que apenas as mudanças no código-fonte invalidem o cache do Docker, tornando o build mais rápido e eficiente.
# O código-fonte será então compilado e empacotado junto com as dependências para gerar o arquivo `.jar`,
# que será executado posteriormente na imagem final.
COPY src ./src

# Agora que o código-fonte já foi copiado e todas as dependências foram previamente resolvidas,
# usamos o comando `mvn clean package` para compilar e empacotar a aplicação.
#
# O `clean` remove qualquer artefato de builds anteriores, garantindo que a compilação seja feita do zero.
# O `package` compila o código-fonte, executa os testes e gera um arquivo `.jar` dentro do diretório `target/`.
# Esse `.jar` contém o código compilado da aplicação junto com as dependências necessárias para sua execução.
#
# Esse processo garante que a aplicação esteja pronta para ser copiada e executada na imagem final do Docker.
RUN mvn clean package

# Definimos a imagem base que será utilizada na etapa final do build multi-stage.
# Diferente da imagem usada na etapa de construção, essa imagem contém apenas o **runtime do Java (JRE),
# e não o JDK completo, tornando-a mais leve e adequada para execução da aplicação.
#
# A escolha de `eclipse-temurin:21-jre-jammy` significa:
# - `eclipse-temurin`: Utilizamos a versão do OpenJDK mantida pela Eclipse Adoptium, uma implementação
#   confiável e otimizada do Java.
# - `21-jre`: Especificamos que queremos **apenas o JRE (Java Runtime Environment)**, já que não
#   precisamos do JDK na execução.
# - `jammy`: Indica que a imagem é baseada na distribuição Ubuntu 22.04 (apelidada de "Jammy Jellyfish"),
#   garantindo estabilidade e compatibilidade.
#
# Essa abordagem ajuda a **reduzir o tamanho da imagem final**, melhorando a performance e diminuindo o
# consumo de recursos.
FROM eclipse-temurin:22-jre-jammy

# Definimos o diretório onde os seguintes comandos serão executados.
# Como cada estágio( Cada FROM inicia um novo estágio ) do Dockerfile
# é independente, definir o mesmo nome para o WORKDIR neste estágio
# não interfere em nada. Apenas garantimos que os arquivos relacionados
# ao Java serão manipulados dentro desse diretório, mantendo a estrutura
# organizada.
WORKDIR /app

# Rodamos esse comando para Instalar o `curl` para o healthcheck funcionar.
# healthcheck é uma checagem para largada que fazemos no Docker compose.
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Utilizamos '--from=builder' para referenciar o estágio de build,
# de onde copiamos o arquivo gerado no caminho '/app/target/*.jar'.
# Esse arquivo é copiado para 'app.jar' na imagem final, garantindo
# que apenas o artefato compilado esteja presente, tornando a imagem mais leve.
COPY --from=builder /app/target/*.jar app.jar

# Copiamos o .jar, responsavel pela Telemetria para dentro de um arquivo .jar
# dentro do WORDIR que definimos.
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar

# JAVA_TOOL_OPTIONS é uma variável de ambiente reconhecida pela JVM (Java Virtual Machine).
# A JVM aplica automaticamente os argumentos definidos em JAVA_TOOL_OPTIONS, sem que seja
# necessário incluí-los manualmente no comando 'java'.
#
# Isso elimina a necessidade de especificar a flag '-javaagent' diretamente no nosso comando
# 'CMD ["java", "-jar", "app.jar"]'.
#
# O uso da variável JAVA_TOOL_OPTIONS equivale a executar a aplicação com o seguinte comando:
# java -javaagent:/app/opentelemetry-javaagent.jar -jar app.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

# Variáveis de ambiente do OpenTelemetry.
ENV OTEL_SERVICE_NAME=seatManager-API
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://collector:4318
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf

# Por reazões de documentação definimos a porta em que a execuçõa dessa imagem irá rodar.
EXPOSE 8080

# Utilizamos so comandos referentes a Imagem final que definimos, para executarmos
# a imagem de nossa aplicação.
#CMD ["java", "-jar", "app.jar"]

CMD ["java", "-jar", "app.jar"]

