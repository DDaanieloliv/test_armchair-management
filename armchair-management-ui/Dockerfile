# Como pretendemos construir uma imagem Docker do nosso projeto Angular.
# Imagem -> (pacote leve, standalone e executável que contém tudo o que é
# necessário para rodar uma aplicação: código, runtime, bibliotecas, dependências e configurações.)

# Definimos a imagem base / dependência necessária para executar nosso projeto Angular.
# Utilizamos 'AS' builder para marcar o início de uma etapa de build multi-stage, permitindo que
# posteriormente possamos referenciar essa etapa e copiar apenas os arquivos relevantes usando
# '--from=builder ....' Isso ajuda a criar uma imagem final mais leve e otimizada.
FROM node:22-alpine AS builder

# Definimos alguns metadados para a documentação
LABEL authors="nielo"

# Definimos a pasta que os arquivos copiados seram manipulados.
WORKDIR /app

# Copiamos os arquivos de configuração e dependências para o WORKDIR.
# Essa abordagem aproveita o cache do Docker, evitando a reconstrução desnecessária de partes
# do build sempre que o código-fonte for alterado. Isso melhora a performance, pois somente as
# dependências serão reinstaladas caso os arquivos de configuração sejam modificados.
COPY package.json package-lock.json ./
COPY angular.json tsconfig.json tsconfig.app.json tsconfig.spec.json ./

# Executamos o comando npm ci para instalar as dependências do projeto de forma limpa e determinística.
# Diferente de npm install, ele garante que as versões instaladas correspondam exatamente às do
# package-lock.json, evitando variações inesperadas. Além disso, é mais rápido e eficiente, pois remove
# a node_modules existente antes da instalação, garantindo um ambiente limpo e reprodutível.
RUN npm ci
#COPY . .
#RUN npm install

# Após a instalação das dependências, copiamos os arquivos do projeto Angular que contêm o código-fonte.
# No caso deste projeto, os diretórios src e public armazenam o código-fonte e os ativos que serão
# processados e renderizados pelo Angular e suas dependências. Assim, copiamos src/ e public/ para as
# respectivas pastas dentro do WORKDIR, garantindo que todo o código necessário esteja disponível para a
# etapa de build.
COPY src/ src/
COPY public/ public/

# Após copiar o código-fonte, executamos o comando de build do Angular.
# O comando npm run build -- --configuration=production instrui o Angular CLI a compilar a aplicação
# utilizando as configurações específicas do ambiente de produção. Isso significa que o código será
# otimizado, removendo trechos desnecessários, minificando arquivos e ativando estratégias que melhoram
# a performance da aplicação. Dessa forma, o resultado final será uma versão leve e eficiente do projeto,
# pronta para ser servida por um servidor web.
RUN npm run build -- --configuration=production


# Por fim definimos a outra Imagem Docker, que fará parte desse build mult-stage.
# Como um programa Angular, exige que ele esteja rodando em um servidor, como servidor de aplicação
# escolhemos o nginx.
FROM nginx:alpine

# O --from=builder ( especifica a origem dos arquivos ) nos permite acessar os arquivos gerados na etapa
# de build multi-stage (builder). Nesse caso, copiamos os arquivos da pasta /app/dist/armchair-management-ui/browser,
# que foram gerados pelo comando npm run build -- --configuration=production, e os movemos para
# usr/share/nginx/html. Esse é o diretório padrão do Nginx, onde ele busca os arquivos estáticos para servir
# a aplicação Angular como um site.
#
# A referência --from=builder permite copiar arquivos de uma etapa anterior do build multi-stage
# sem manter toda a imagem daquele estágio, tornando a imagem final mais leve.
# No build multi-stage, o Docker constrói diferentes estágios (ou camadas), mas apenas a última
# imagem é mantida. Dessa forma, conseguimos incluir somente os arquivos necessários
# para a execução da aplicação, descartando dependências e arquivos intermediários usados na compilação.
COPY --from=builder /app/dist/armchair-management-ui/browser usr/share/nginx/html


# Copiamos o arquivo `nginx.conf` para `/etc/nginx/nginx.conf`, substituindo a configuração padrão do Nginx.
# Esse arquivo define regras como portas, gzip, cache, roteamento de requisições e comportamento geral do servidor.
#
# Também copiamos o arquivo `mime.types` para `/etc/nginx/mime.types`,
# que contém associações de tipos MIME para diferentes extensões de arquivos.
# Isso permite que o Nginx informe corretamente o tipo de conteúdo de cada arquivo servido,
# garantindo que navegadores e clientes interpretem os arquivos corretamente.
COPY nginx.conf /etc/nginx/nginx.conf
COPY mime.types /etc/nginx/mime.types

# Definimos apenas a nível de documetação a porta em que essa imagem irá rodar.
EXPOSE 80

# Comando para rodar a Ultima Imagem do build mult-stage. (Nginx)
CMD ["nginx", "-g", "daemon off;"]
