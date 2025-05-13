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

