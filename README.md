# Impl Observability

Este repositório tem como objetivo demonstrar a implementação dos **três pilares da observabilidade de software**: **Métricas**, **Logs** e **Traces**. Esses pilares permitem o monitoramento abrangente do sistema, oferecendo visibilidade sobre o uso de recursos, comportamento da aplicação e fluxo de execução.

## 🚀 Getting Started

> **Importante**: Esta implementação é voltada para ambientes **locais** ou **controlados**. Não é recomendada para ambientes de produção, pois ainda **não inclui práticas de segurança ou autenticação** adequadas.

### Pré-requisitos

- Docker e Docker Compose instalados
- Git
- Java 21 (caso deseje executar localmente sem Docker)

<br>
<br>

### Clonando o Repositório

Você pode clonar este repositório via SSH ou HTTPS:

#### SSH

```bash
git clone git@gitlab.com:DDaanieloliv/Impl-Observability.git
# ou
git clone git@github.com:DDaanieloliv/Docker-.git
```

#### HTTPS

```bash
git clone https://gitlab.com/DDaanieloliv/Impl-Observability.git
# ou
git clone https://github.com/DDaanieloliv/Docker-.git
```



### Executando projeto

```bash
cd Impl-Observability/armchair_management/local/

docker compose up -d 
```

<br>
<br>

### Aviso

Este repositório tem fins educacionais e demonstrativos. Não é indicado o uso direto desta configuração em ambientes de produção sem as devidas adaptações de segurança, autenticação e escalabilidade.

<br>

- ### 🤝 Contribuições

Sinta-se à vontade para abrir issues ou merge requests. Feedbacks são bem-vindos!

<br>

- ### 📄 Licença

Este projeto está licenciado sob a MIT License.

<br>

- ### 👨‍💻 Autor

Daniel Oliveira – @DDaanieloliv

[Linkedin - Daniel Oliveira](https://www.linkedin.com/in/daniel-oliveira-aba552251/)


## Ferramentas de Observabilidade abordadas:

- [X] [OpenTelemetry](https://opentelemetry.io/docs/zero-code/java/agent/getting-started/)
- [X] [OpenTelemetry Collector](https://opentelemetry.io/docs/)
- [X] [Prometheus](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [X] [Prometheus AlertingManager](https://prometheus.io/docs/alerting/latest/configuration/)
- [X] [Grafana](https://grafana.com/docs/grafana/latest/introduction/)
- [X] [Grafana Promtail Agent](https://grafana.com/docs/loki/latest/send-data/promtail/)
- [X] [Grafana Alloy](https://grafana.com/docs/alloy/latest/)
- [X] [Grafana Loki](https://grafana.com/docs/alloy/latest/)
- [X] [Grafana Tempo](https://grafana.com/docs/alloy/latest/)
  