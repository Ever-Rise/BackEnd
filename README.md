# EVER RISE Backend

Backend do projeto EVERRISE para autenticacao, controle de guinchos, telemetria em tempo real, pedidos/pagamentos e chatbot.

## Objetivo deste repositorio
Consolidar uma base tecnica segura e evolutiva para os proximos ciclos de desenvolvimento.

## Versoes e Ferramentas em Uso (estado atual)

As versoes abaixo foram confirmadas no repositorio neste momento:

- Java: 25 (`pom.xml` -> `java.version`)
- Spring Boot: 3.4.4 (`spring-boot-starter-parent`)
- Build tool: Maven (projeto com `pom.xml`)
- Banco no profile dev: MySQL (driver `com.mysql.cj.jdbc.Driver`)
- Banco no profile test: H2 em memoria
- Cache/sessoes: Redis
- Mensageria: MQTT (broker externo)
- Realtime: WebSocket (STOMP)
- Migrations: Flyway (`src/main/resources/db/migration`)

Ferramentas recomendadas para desenvolvimento local:

- JDK 25
- Maven 3.9+
- MySQL 8+
- Redis 7+
- Broker MQTT (ex.: Eclipse Mosquitto 2.x)

Validacao rapida do ambiente Java local:

```bash
java -version
mvn -version
```

Esperado: ambos os comandos exibindo Java 25.

## Estrutura de Pastas

```text
BackEnd/
|- docs/
|  |- README.md
|  |- manual-api.md
|  |- manual-arquitetura.md
|  |- manual-desenvolvimento-manutencao.md
|  |- manual-funcionamento-fluxos.md
|  `- manual-operacao.md
|- src/
|  |- main/
|  |  |- java/br/com/everrise/
|  |  |  |- config/
|  |  |  |- controller/
|  |  |  |- domain/
|  |  |  |- dto/
|  |  |  |- exception/
|  |  |  |- mapper/
|  |  |  |- messaging/
|  |  |  |- repository/
|  |  |  |- security/
|  |  |  |- service/
|  |  |  |- util/
|  |  |  |- websocket/
|  |  |  `- EverriseBackendApplication.java
|  |  `- resources/
|  |     |- application.yml
|  |     |- application-dev.yml
|  |     |- application-test.yml
|  |     |- application-prod.yml
|  |     `- db/migration/
|  `- test/java/br/com/everrise/
|- pom.xml
|- README.md
`- .gitignore
```

Resumo rapido:

- `src/main/java`: codigo fonte principal
- `src/main/resources`: configuracoes e migrations
- `src/test/java`: testes
- `docs`: documentacao tecnica e operacional

## Perfis de Execucao

- `dev`: usa MySQL local + Redis local + Swagger habilitado
- `test`: usa H2 em memoria, Flyway desabilitado
- `prod`: usa variaveis de ambiente para banco e redis, Swagger desabilitado

## Configuracao Minima para Rodar Local (`dev`)

### 1) Subir dependencias locais

- MySQL em `localhost:3306` com database `everrise_dev`
- Redis em `localhost:6379`
- MQTT broker em `localhost:1883`

### 2) Definir variaveis/propriedades obrigatorias

O codigo usa propriedades para JWT, MQTT e webhook. Se elas nao estiverem definidas, a aplicacao pode falhar na inicializacao.

Exemplo em PowerShell (sessao atual):

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"

$env:APP_JWT_SECRET="troque-por-uma-chave-longa-e-segura"
$env:APP_JWT_EXPIRATION="3600000"
$env:APP_JWT_REFRESH_EXPIRATION="604800000"

$env:APP_MQTT_BROKER_URL="tcp://localhost:1883"
$env:APP_MQTT_USERNAME="admin"
$env:APP_MQTT_PASSWORD="admin"
$env:APP_MQTT_CLIENT_ID="everrise-backend"
$env:APP_MQTT_TOPIC_COMMAND="guincho/%s/command"
$env:APP_MQTT_TOPIC_TELEMETRY="guincho/%s/telemetry"

$env:APP_MERCADOPAGO_WEBHOOK_SECRET="troque-por-segredo-local"
```

Observacao importante sobre nomes de variaveis:

- Propriedade `app.jwt.secret` -> variavel `APP_JWT_SECRET`
- Propriedade `app.mqtt.broker-url` -> variavel `APP_MQTT_BROKER_URL`

## Como Rodar o Backend

Na raiz do projeto:

```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

Ou empacotando e executando o jar:

```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## Build e Testes

- Build sem testes:

```bash
mvn clean package -DskipTests
```

- Executar testes:

```bash
mvn test
```

## Validacao Rapida apos subir

- Aplicacao responde na porta `8080`
- Swagger UI (profile `dev`): `http://localhost:8080/swagger-ui/index.html`
- Endpoints de autenticacao e fluxos principais respondendo sem erro 5xx

## Documentacao

Comece por `docs/README.md` para navegar pelos manuais.

## Proximos Passos Recomendados

1. Garantir build verde com dependencias completas no `pom.xml`.
2. Alinhar migrations com entidades JPA.
3. Aumentar cobertura de testes nos fluxos criticos.
4. Fortalecer regras de autenticacao e autorizacao.
