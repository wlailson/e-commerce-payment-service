# E-commerce Payment Service

Serviço responsável por registrar e consultar pagamentos associados aos pedidos. Possui um processador simulado e publica eventos de resultado de pagamento via Kafka para que o serviço de pedidos atualize o estado do pedido.

## Tecnologias

- Java 25, Maven e Spring Boot 4.1.1.
- Spring MVC, validação e OAuth2 Resource Server para validação JWT.
- Spring Data JPA, PostgreSQL e Flyway.
- Spring for Apache Kafka para publicação de eventos.
- Spring Boot Actuator e Micrometer Prometheus Registry.
- springdoc-openapi / Swagger UI.
- Testes com JUnit Jupiter, Mockito e Testcontainers para PostgreSQL e Kafka.

## Swagger

[📚 Acessar Swagger](https://wlailson.github.io/e-commerce-payment-service/)

## Executar localmente

Pré-requisitos: JDK 25, PostgreSQL e Kafka acessíveis. O perfil de desenvolvimento é ativado por padrão; confira `src/main/resources/application-dev.yaml` para os parâmetros locais do banco.

```bash
./mvnw spring-boot:run
```

Variáveis de configuração principais:

| Variável | Uso |
|---|---|
| `SERVER_PORT` | Porta HTTP (padrão `8080`). |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Conexão com PostgreSQL. |
| `KAFKA_BOOTSTRAP_SERVERS` | Endereço do cluster Kafka (padrão local `localhost:9092`). |
| `KAFKA_TOPIC_CREATED`, `KAFKA_TOPIC_UPDATED` | Tópicos de pagamento configurados pelo serviço. |
| `JWT_PUBLIC_KEY` | Chave pública para validação dos tokens. |
| `PAYMENT_SIMULATOR_DECLINE_ABOVE_AMOUNT` | Limite opcional acima do qual o simulador recusa pagamentos. |

Flyway gerencia as migrações do banco. Use chaves próprias e configuração segura em ambientes compartilhados ou de produção.

## Testes

```bash
./mvnw test
```

Docker deve estar disponível para os testes de integração que usam Testcontainers.

## API e observabilidade

Os endpoints de pagamento estão sob `/payments`. Consulte a documentação HTTP gerada pela aplicação:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Métricas Prometheus: `http://localhost:8080/actuator/prometheus`
- Saúde: `http://localhost:8080/actuator/health`

O serviço publica o evento `payment-created-event` por padrão para consumo pelos serviços de pedidos e analytics.

## Projeto

Veja a arquitetura e os demais serviços no [README central do BFF](https://github.com/wlailson/e-commerce-BFF-service#readme).
