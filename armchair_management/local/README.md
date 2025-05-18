# How all this Works ?

## _**prometheus.yml**_

```YAML
global: 
  scrape_interval: 15s   
  external_labels:
    monitor: 'codelab-monitor'

alerting:
  alertmanagers:
    - static_configs: 
        - targets: ['alertmanager:9093']
          
rule_files:
  - "/etc/prometheus/alerting-rules.yml"


scrape_configs:
  - job_name: 'postgres-exporter'
    static_configs: 
      - targets: ['postgres_exporter:9187']

  - job_name: 'prometheus'
    scrape_interval: 5s
    static_configs: 
      - targets: ['prometheus:9090']


  - job_name: 'spring-boot-app'
    scrape_interval: 5s
    metrics_path: /actuator/prometheus
    static_configs: 
      - targets: ['spring-app:8080']
        labels:
          application: "api-seatmanager"

```

<br>

### Syntax;


<br>

- **global:**

> Especifica os parâmetros válidos para todos os outros contextos de configuração. 

<br>

- **scrape_interval:**

> Define com que frequência faremos o "scrape" no 'target' por padrão.

<br>

- **external_labels:**

> Define as labels para adicionar a qualquer série temporal ou alerta quando 
> comunicando a sistemas externos (federation, remote storage, Alertmanager). 
> Referências a Variáveis de ambiente `${var}` ou `$var` são substituídas de 
> acordo com os valores atuais das variáveis do ambiente.
> Referências a variáveis não definidas são substituídas por uma string vazia.
> O caractere `$` pode ser escapado usando `$$`.

<br>

- **alerting:**

> Esse comando especifica as configurações relacionadas ao Alertmanager.

<br>

- **static_configs:**

> Define uma lista de rótulos, configurando estaticamente o Alertmanager. 
> O comando 'static_config' permite especificar uma lista de 'targets' e labels comuns settados para eles.
> Isso é uma maneira canônica de especificar 'targets' estáticos numa configuração de scrape.

<br>

- **rule_files:**

>Rule files specifies a list of globs. Rules and alerts are read from
all matching files.

<br>

- **scrape_configs:**

> Uma seção definida pelo comando 'scrape_config' especifica um conjunto de 'targets' e parâmetros, 
> descrevendo como fazer o 'scrape' deles. Geralmente uma configuração de 'scrape' define
> um 'single job'. Em configurações avançadas isso pode mudar. 

<br>

- **job_name:**

>A diretiva 'job_name' atribui um nome para as métricas obtidas por meio do scrape por padrão.

<br>

- **scrape_interval:**

> Essa diretiva define um "Per-scrape timeout" quando estivermos fazendo o scraping para esse job.
Isso não pode ser maior que o "scrape interval".

<br>

- **metrics_path:**

> Essa diretiva é um path é um recurso HTTP no qual faz a busca por métricas do 'target'.
> Com a diretiva definimos o 'socket' do qual iremos fazer o 'scrape', ou seja, o "host:port"
> ou serviço doceker/kubernets do exporter da aplicação, porem como o prometheus irá usar um 
> HTTP request para esse 'socket' buscando por métricas, entretanto sem o path HTTP de onde obterá  
> as métricas, para isso usamos a diretiva 'metrics_path' permitindo ao prometheus fazer o 
> 'scrape' dessa url: "http://spring-app:8080/actuator/prometheus". 

<br>

<br>


## _**alerting-rules.yml**_

```YAML
groups: 
  - name: ApplicationAlerts 
    rules: 
      - alert: InstanceDown
        expr: up == 0 
        for: 30s  
        labels:
          severity: critical
        annotations:
          summary: "Instância {{ $labels.instance }} está fora do ar"
          description: "{{ $labels.instance }} está inativa há mais de 30 segundos." 

      - alert: HighCPUUsage
        expr: rate(process_cpu_seconds_total[1m]) > 0.9
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Uso de CPU alto na instância {{ $labels.instance }}"
          description: "A CPU está sendo utilizada acima de 90% há mais de 1 minuto."

      - alert: HighMemoryUsage
        expr: (process_resident_memory_bytes / process_virtual_memory_bytes) > 0.8
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Uso de memória alto na instância {{ $labels.instance }}"
          description: "A memória está acima de 80% de uso há mais de 1 minuto."

      - alert: Http500Errors
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[1m]) > 0
        for: 30s
        labels:
          severity: critical
        annotations:
          summary: "Muitos erros 500 na instância {{ $labels.instance }}"
          description: "A aplicação está retornando erros 5xx."

      - alert: HighRequestLatency
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[1m])) > 1
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Alta latência nas requisições para {{ $labels.uri }}"
          description: "A latência das requisições está acima de 1 segundo no 95º percentil."


```

<br>

### Syntax;

<br>


- **groups:**

> Isso define uma lista de grupos de alertas. Agrupando regras, ajudando a organizar alertas 
> lógicamente, muitas vezes por serviço ou componente.


<br>


- **name:**

> Nome do grupo de alertas, indicando que ele contém regras relacionadas ao comportamento de 
> uma aplicação.

<br>


- **rules:**

> Lista de regras de alerta individuais que pertencem a este grupo. Cada regra define uma condição 
> específica que será monitorada.

<br>


- **for:**

> Duração mínima que a condição deve permanecer verdadeira antes que o alerta seja ativado (FIRING).

<br>


- **labels:**

> No mermite fazer Labels customization, que são ligadas ao alerta. Comummente usados para 
> categorizar alerts por (severity, team, environment...).


- **annotations:**

> Essa diretiva provê descrições textuais usadas em notificações. Suportando "templating" com 
> `{{ $labels.instance }}` para mensagens com contexto dinâmico. As variáveis `{{ $labels.instance }}`
> vem dos dados de séries temporais retornados pela expressão PromQL.

<br>


- **expr:**

> Expressão PromQL que define a condição do alerta. Caso ela permaneça verdadeira durante
> o tempo especificado em 'for', o alerta muda para o estado FIRING.


Alerting rules permite você definir condições de alertas baseados na 'Prometheus expression language' 
and send notifications about FIRING alerts para um dispositivo externo.

<br>

<br>


## Alertmanager - _**config.yml**_

```YAML
global:
  smtp_smarthost: 'mailpit:1025' 
  smtp_from: 'alertas@sistema.local'   
  smtp_require_tls: false

route:
  receiver: email-alerts
  group_wait: 10s
  group_interval: 30s
  repeat_interval: 1h

receivers:
  - name: email-alerts 
    email_configs:
      - to: 'admin@sistema.local'
        from: 'alertas@sistema.local' 
        send_resolved: true 
```

<br>

### Syntax;

<br>

- **global:**

> Essa configuração global especifica parâmetros validos em todas as outros 
> contextos de configuração. 

<br>

- **smtp_smarthost:** 

> Essa diretiva define o 'smarthost smtp' padrão, usado para mandar emails, incluindo port number.
> O número da porta costuma ser 25, ou 587 para sobre TLS (às vezes chamado STARTTLS).
> Exemplo: smtp.example.org:587

<br>


- **smtp_from:**

> The default SMTP From header field.


<br>


- **route:**

> O bloco de 'route' define um nó numa árvore de roteamento e a suas children. É uma 
> configuração opcional, e os parâmetros são herdados do nó pai se não forem definidos.

<br>

- **receivers:**

> Essa diretiva define a lista dos recebedores de notificações.

<br>


- **name:**

> The unique name of the receiver.

<br>


- **email_configs:**

> Configura o endereço de email para mandar notificações para...
> E Permite uma lista separada por vírgulas de endereços de e-mail compatíveis com rfc5322.

<br>


- **send_resolved:** 

> Espera um valor booleano para dizer se deve notificar sobre alertas resolvidos.


<br>

<br>


## _**promtail-config.yaml**_

```YAML
server:
  http_listen_port: 9080
  grpc_listen_port: 0

clients:
  - url: http://loki:3100/loki/api/v1/push
    
positions:
  filename: /tmp/positions.yaml


scrape_configs:
  - job_name: docker
    static_configs:
      - targets:
          - localhost
        labels:
          job: docker-logs
          __path__: /var/lib/docker/containers/*/*.log
          team: DevOps
          env: Prod
    pipeline_stages:
      - match:
          selector: '{job="docker-logs"} |= "service="'
          stages:
            - regex:
                expression: '.*service=(?P<service>[^\s]+).*'
            - labels:
                service:
```

<br>

### Syntax;

<br>


- **server:**

> Define as configurações e comportamentos do webserver do Promtail. 

<br>


- **http_listen_port:**

> Configuramos o servidor para escutar conexões com a respective porta que definimos.

<br>


- **clients:**

> Descreve como o Promtail se conecta a multiplas instancias do Grafana Loki
> mandando logs para cada um.
> 
> WARNING: If one of the remote Loki servers fails to respond or responds
with any error which is retryable, this will impact sending logs to any
other configured remote Loki servers.  Sending is done on a single thread!
It is generally recommended to run multiple Promtail clients in parallel
if you want to send to multiple remote Loki instances.

<br>

- **url:**

> Essa diretiva define de onde o Loki é escutado pelo Promtail webserver denotado
> no Loki com http_listen_address e http_listen_port.
> Se o Loki estiver a rodar em modo microserviços, isso é a HTTP URL para o Distributor.
> Path para a 'push API' precisa ser includa. Example: http://example.com:3100/loki/api/v1/push

<br>

- **position:** 

> Descreve como salvar um Offset de arquivos lidos para o disk.
> Esse bloco onde o promtail irá salvar o arquivo indicado até quão longe ele
> leu o arquivo. Isso é preciso para quando o Promtail é reiniciado para permitir
> que ele continue de onde parou.

<br>

- **scrape_configs:**

> Esse bloco configura como o Promtail pode fazer o 'scrape' de logs de uma série
> de 'targers' usando um dicovery method especificado. O Promtail usa o mesmo 
> Prometheus SCRAPE_CONFIGS. Isso significa que, se você já possui uma instância 
> de Prometheus, a configuração será muito semelhante.

<br>

- **job_name:**

> Define o nome que identifica a respectiva configuração de 'scrape' no Promtail UI.

<br>


- **labels:**

> Nos permite atribuirmos rótulos estáticos ao read file em questão.

<br>


- **pipiline_stages:**

> São usadas para transformar as entradas de logs e a suas labels.
> A pipeline é executada depois do discovery process terminar.
> Essa diretiva consiste numa lista de estágios que correspondem a itens como
> regex e json para extrair data from logs geralmente. Esses dados extraídos
> são transformados num mapa de objetos temporários. Esses dados podem ser usados 
> pelo Promtail, por exemplo, como valores para labels e outputs.   

<br>


- **match:**

> O estágio 'match' condicionalmente executam um conjunto de estágios quando a entrada de logs 
> der match com a LogQL 'stream selector'.

<br>


- **selector:**

> Diretiva que avalia se a entrada de log match com a string LogQL definida nela.
> E caso true o entry log irá passar pelos seguintes estágios pipeline.
> Estágios esses que podem ser `- [ <docker> | <cri> | <regex> | <json> | <template> |
> <match> | <timestamp> | <output> | <labels> | <metrics> ]` .

<br>


- **stages:**

> Define um consjunto aninhado de pipeline stages apenas se o selector matches
> com as labels dos logs entries.

<br>


- **regex:**

> Esse estágio 'regex' leva uma expressão regular para matches com grupos presentes nos logs entries.
> E captura nomes de grupos extraídos conforme a diretiva 'expression', para serem usados em outros estágios.

<br>


- **expression:**

> Define a RE2 regular expression que cada grupo de captura deve ser nomeado.

<br>


- **labels:**

> O estágios 'labels' apanha data do mapa de objetos estraídos e configura 
> 'labels' adicionais no log entry que irá ser enviado para o Loki.
> Podendo ser definido assim `[ <string>: [<string>] ... ]`.
> Chave e o nome é requerido para a 'label' que irá ser criada.
> O valor é opcional e será o nome do dado extraído cujo valor será usado para
> o valor da 'label'. Caso vazio o valor que será inferido será o mesmo que a Chave.

<br>

<br>


## _**loki-config.yaml**_


```YAML
auth_enabled: false

server:
  http_listen_port: 3100

ingester:
  lifecycler:
    address: 127.0.0.1
    ring:
      kvstore:
        store: inmemory
      replication_factor: 1
    final_sleep: 0s
  chunk_idle_period: 5m
  chunk_retain_period: 30s
  max_transfer_retries: 0

schema_config:
  configs:
    - from: 2020-10-24
      store: boltdb-shipper
      object_store: filesystem
      schema: v11
      index:
        prefix: index_
        period: 24h

storage_config:
  boltdb_shipper:
    active_index_directory: /loki/index
    cache_location: /loki/boltdb-cache
    cache_ttl: 24h
  filesystem:
    directory: /loki/chunks

limits_config:
  enforce_metric_name: false
  reject_old_samples: true
  reject_old_samples_max_age: 168h

chunk_store_config:
  max_look_back_period: 0s

table_manager:
  retention_deletes_enabled: false
  retention_period: 0s
```

<br>

### Syntax;

<br>


- **auth_enabled:**

> Ativa autenticação através do X-Scope-OrgID header, que deve está presente caso 
> essa diretiva seja definida como true. Se falso, o OrgID será sempre definido como 'fake'.
> CLI flag: -auth.enabled

<br>


- **server:**

> Configura o servidor do módulo lançado.

<br>


- **ingester:**

> O bloco 'ingester' configura o 'ingester' e como ele irpa registrar a si para um armazenamento
> chave, valor. O "ingester" no Loki é o componente crucial que recebe e processa os dados de 
> log enviados pelos agentes de coleta, como Promtail ou Fluentd. Ele atua como um intermediário, 
> compactando os dados de log e preparando-os para o armazenamento.
> Essa secção configura como Loki recebe buffers e armazena logs temporariamente
> antes deles serem flushed to armazenamento permanente.

<br>


- **schema_config:**

> Configura o esquema de índice da chunk e onde é armazenado.
> Configura como os logs são indexados e armazenados, over time.

<br>


- **storage_config:**

> O bloco 'storage_config' configura um de muitos stores possíveis para both index e chunks.
> Com a configuração a ser escolhida devendo ser definida no bloco de schema_config.

<br>


- **limits_config:**

> Esse bloco configura um limite global e por inquilino no Loki. 
> Os valores aqui podem ser substituidos nas 'overrides' section of the runtime_config file. 

<br>


- **table_manager:**

> O bloco Table_Manager configura o gerenciador de tabela para retenção.
> No Grafana Loki, o Table Manager é um componente responsável por gerir as tabelas onde 
> os logs são armazenados. Ele cria tabelas periódicas antes do início do seu período de tempo e as 
> apaga quando os seus dados excedem o período de retenção. Essencialmente, o Table Manager 
> implementa a retenção de dados, apagando tabelas inteiras que contenham dados mais antigos 
> que o período de retenção especificado, conforme a documentação da Grafana. [https://grafana.com/docs/loki/latest/operations/storage/table-manager/]

<br>


- **chunk_store_config:**

> Esse bloco configura como as chunks irão ser cacheadas e how long esperar antes de salvar elas
> to the backing store.

<br>

<br>


## _**tempo.yaml**_

```YAML
auth_enabled: false

server:
  http_listen_port: 3200

distributor:
  receivers:
    otlp:
      protocols:
        grpc:
        http:

ingester:
  trace_idle_period: 10s
  max_block_duration: 5m

compactor:
  compaction:
    compaction_window: 1h

storage:
  trace:
    backend: local
    local:
      path: /tmp/tempo
    wal:
      path: /tmp/tempo/wal
```

<br>

### Syntax;

<br>

- **http_listen_port:**

> HTTP server listen port.

<br>


- **distributor:**

> Define as configurações do bloco distributor.
> O 'distributor' recebe os spans e dispachaos ao ingester apropriado.
> Ou seja, definimos como o tempo recebe as traces.
> E a diretiva 'oltp' especifica o suporte para 'OpenTelemetry Protocol'
> sob so protocolos 'grpc' e 'http'. 

<br>


- **receivers:**

> Define as configurações para diferentes protocolos.
> Essas configurações são passadas para os receptores definidos.

<br>


- **ingester:**

> O ingester é responsavel por lidar com traces e pushing them para TempoDB.
> Esse bloco controla como as traces são grouped e flushed to storage.

<br>


- **trace_idle_period:**

> Define a quantidade de tempo um rastreamento deve estar ocioso antes de libertá-lo para o Wal.

<br>


- **max_block_duration:**

> Define o máximo de tempo antes de cortar um bloco

<br>


- **compactor:**

> O compactador no Grafana Tempo tem a função de otimizar o armazenamento e o acesso a dados de rastreamento distribuído, reduzindo o número de blocos e, consequentemente, o espaço utilizado. Ele faz isso transmitindo blocos de e para o armazenamento de backend, agindo como um mecanismo de compactação para garantir que os dados sejam armazenados de forma eficiente. 
> O compactor stream blocos do backend de armazenamento, combin-os e os escreve de volta.
> Controla a otimização de dados de trace ao longo do tempo.

<br>


- **storage:**

> Esse bloco configura como serão armazenamento para as traces.
> Ou seja, configuramos como e onde o Grafana Tempo armazena as traces.

<br>

<br>

## _**otel-collector.yaml**_

```YAML
receivers:
  otlp:
    protocols:
      grpc:
      http:

exporters:
  otlp:
    endpoint: tempo:4317
    tls:
      insecure: true

service:
  pipelines:
    traces:
      receivers: [otlp]
      exporters: [otlp]
```

<br>

### Syntax;

<br>


- **receivers:**

> Essa secção define como o Colletor recebe telemetry data.

<br>


- **exporters:**

> Esse bloco define para onde o Collector envia os dados após recebe-los.
> Nele explicitamos que estamos a usar o 'oltp' exporter, fundamental para forward data para 
> outro sistema compatível com OLTP.
exporters:
<br>


- **service:**

> Nessa secção definimos como telemetry flows através do Collector.
> Nos permitindo definir a pipeline para trace data (tambem podendo ter usado para métricas e/ou logs).
> Seu pipeline acabo por conectar OLTP receiver ao OLTP exporter, no nosso caso (receive trace data → send to Tempo).

<br>

<br>


___


### _**De adordo com Grafana Labs:**_

> Promtail has been deprecated and is in Long-Term Support (LTS) through February 28, 2026. Promtail will reach an End-of-Life (EOL) on March 2, 2026.


Para isso temos como recurso de migração ou alternativo o Grafana alloy.

<br>

<br>


## Grafana Alloy - _**config.alloy**_

```alloy
// ###############################################
// #### Logging Configuration ( Using files ) ####
// ###############################################

livedebugging {
  enabled = true
}

logging {
  level  = "debug"
  format = "logfmt"
}

local.file_match "local_files" {
    path_targets = [{"__path__" = "/var/lib/docker/containers/*/*.log", "job" = "java-api", "hostname" = constants.hostname}]
    sync_period  = "5s"
}

loki.source.file "log_scrape" {
    targets    = local.file_match.local_files.targets
    forward_to    = [loki.process.add_labels.receiver]
    tail_from_end = true
}

loki.process "add_labels" {
  stage.json {
    expressions = {
      log   = "",
      time  = "",
      stream = "",
    }
  }

  stage.regex {
    expression = ".*service=(?P<service>[^ ]+).*level=(?P<level>[^ ]+).*traceId=(?P<traceId>[^ ]+).*"
  }

  stage.labels {
    values = {
      service = "",
      level   = "",
      traceId = "",
    }
  }

  stage.static_labels {
    values = {
      team = "backend",
      env  = "production",
    }
  }

  stage.timestamp {
    source = "time"
    format = "RFC3339Nano"
  }

  stage.output {
    source = "log"
  }

  forward_to = [loki.write.local.receiver]
}


loki.write "local" {
  endpoint {
    url = "http://loki:3100/loki/api/v1/push"
  }
}


// #######################################################
// #### Logging Configuration ( Using OpenTelemetry ) ####
// #######################################################


logging {
  level  = "debug"
  format = "logfmt"
}

livedebugging {
  enabled = true
}

otelcol.receiver.otlp "default_logs" {
  http {
    endpoint="0.0.0.0:4444"
  }

  output {
    logs = [otelcol.exporter.loki.default.input]
  }
}


otelcol.exporter.loki "default" {
  forward_to = [loki.write.local.receiver]
}

loki.write "local" {
  endpoint {
    url = "http://loki:3100/loki/api/v1/push"
  }
}





// ###############################
// #### Traces Configuration #####
// ###############################


otelcol.receiver.otlp "default" {
  http {
    endpoint="0.0.0.0:4320"
  }

  output {
    traces = [otelcol.processor.batch.default.input]
  }
}

otelcol.processor.batch "default" {
  output {
    traces  = [otelcol.exporter.otlphttp.tempo.input]
  }
}

otelcol.exporter.otlphttp "tempo" {
    client {
        endpoint = "http://tempo:4318"
        tls {
            insecure             = true
            insecure_skip_verify = true
        }
    }
}
```

<br>

- O Grafana alloy dinâmicamente configura e conecta COMPONENTES usando o Alloy configuration syntax.
  <br>
  <br>
- O alloy nos permite coletar, transformar e distribuir dados de telemetria.
  <br>
  <br>
- Cada COMPONENTE de configuração realiza tasks especificas ou define o fluxo de dados e conexão de componentes.

<br>

### Syntax;

A sintaxe do alloy na sua essência utiliza COMPONENTES para configurar o comportamento e o fluxo de dados.
<br>
<br>
Essa sintaxe do alloy visa tornar os arquivos mais fáceis de ler e escrever. Fazendo o uso de blocos, atributos e expressões.
<br>
<br>
Como a sua sintaxe é declarativa, a ordem dos componentes, blocos e atributos não importa. A relação entre 
componentes que determina a sequência de operações do pipeline. 

- Documentação: https://grafana.com/docs/alloy/latest/get-started/configuration-syntax/

<br>
<br>

- Grupos de blocos relacionados típicamente representam a criação de components.
- Blocos possuem um nome que consistem no identificador separado por um `.`, uma label de utilizador opcionalmente, e um body contendo atributos e Nested Blocks.  
- Atributos aparecem dentro de blocos e atribuem valor a nomes.
- Expressões representam valores, literalmente ou referenciando e combinando outros valores.

<br>
<br>


### $$Abordagem \;\; Utilizando \;\;LogFiles:$$


<br>

- Collection: Monta um diretório local com um certo path especificado.
```
local.file_match "local_files" {
    path_targets = [{"__path__" = "/var/lib/docker/containers/*/*.log", "job" = "java-api", "hostname" = constants.hostname}]
    sync_period  = "5s"
}
```

<br>
<br>

- Collection: Pega a correspondência do arquivo como entrada, e faz o scrape nos arquivos de log montados.
- E por meio do `forward_to    = [loki.process.add_labels.receiver]` especificamos que componente deverá processar os logs a seguir, fazendo um link entre componentes.
```
loki.source.file "log_scrape" {
    targets    = local.file_match.local_files.targets
    forward_to    = [loki.process.add_labels.receiver]
    tail_from_end = true
}
```

<br>
<br>


- Transformation: Puxa alguns dados para fora da log message e transforma em labels.
```
loki.process "add_labels" {
  stage.json {
    expressions = {
      log   = "",
      time  = "",
      stream = "",
    }
  }

  stage.regex {
    expression = ".*service=(?P<service>[^ ]+).*level=(?P<level>[^ ]+).*traceId=(?P<traceId>[^ ]+).*"
  }

  stage.labels {
    values = {
      service = "",
      level   = "",
      traceId = "",
    }
  }

  stage.static_labels {
    values = {
      team = "backend",
      env  = "production",
    }
  }

  stage.timestamp {
    source = "time"
    format = "RFC3339Nano"
  }

  stage.output {
    source = "log"
  } 

  forward_to = [loki.write.local.receiver]
}
```

<br>
<br>


- Tudo isso que vem desse componente anterior é recebido no 'loki.write.local.receiver' component e é escrito na API remota do Loki.
```
loki.write "local" {
  endpoint {
    url = "http://loki:3100/loki/api/v1/push"
  }
}
```
<br>
<br>

### $$Abordagem \;\; Utilizando \;\;OpenTelemetry Logs:$$


- `otelcol.receiver.otlp` is a wrapper over the upstream OpenTelemetry Collector otlp receiver.
```
otelcol.receiver.otlp "default_logs" {
  http {
    endpoint="0.0.0.0:4444"
  }

  output {
    logs = [otelcol.exporter.loki.default.input]
  }
}
```

- `otelcol.exporter.loki` aceita logs com "OTLP-formatted" vindo de outro componente "otelcol", converte as entradas de logs para "Loki-formatted", e os exporta para o componente Loki.
```
otelcol.exporter.loki "default" {
  forward_to = [loki.write.local.receiver]
}

loki.write "local" {
  endpoint {
    url = "http://loki:3100/loki/api/v1/push"
  }
}
```

<br>
<br>


### $$Abordagem \;\; Utilizando \;\;OpenTelemetry Traces:$$


- `otelcol.receiver.otlp` is a wrapper over the upstream OpenTelemetry Collector otlp receiver. 
```
otelcol.receiver.otlp "default" {
  http {
    endpoint="0.0.0.0:4320"
  }

  output {
    traces = [otelcol.processor.batch.default.input]
  }
}
```

- `otelcol.processor.batch` aceita telemetry data de outro componente otelcol e os empurra para "batches". Batching melhora a compressão of data e reduz o número de solicitações de saída a rede necessárias para transmitir dados. Este processador suporta processamento em lote(batching) baseado em tamanho e tempo.
```
otelcol.processor.batch "default" {
  output {
    traces  = [otelcol.exporter.otlphttp.tempo.input]
  }
}
```


- `otelcol.exporter.otlphttp` aceita telemetry data de outros componentes otelcol e escreve eles pela rede usando o OTLP HTTP protocol. 
```
otelcol.exporter.otlphttp "tempo" {
    client {
        endpoint = "http://tempo:4318"
        tls {
            insecure             = true
            insecure_skip_verify = true
        }
    }
}
```