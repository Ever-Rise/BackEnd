# EverRise Backend

Backend do projeto **EverRise**, responsável pela base de autenticação, persistência, integrações e regras de negócio da plataforma do guincho hospitalar autônomo.

## Visão geral

Este backend foi construído com **Spring Boot** e organiza a aplicação em camadas para facilitar manutenção, evolução e integração com o ecossistema do produto.

Ele concentra as responsabilidades de:

- autenticação e autorização de usuários;
- gestão de usuários, pacientes e familiares;
- controle de equipamentos/guinchos;
- sessão de uso e acompanhamento operacional;
- processamento e histórico de telemetria;
- geração e gerenciamento de alertas;
- tokens de desconto e fluxo de pedidos;
- chat/SSE para suporte e interação;
- integração com banco de dados, Redis e MQTT.

## Arquitetura

A aplicação segue uma arquitetura em camadas, com separação clara entre entrada, negócio, domínio e persistência.

### 1. Camada de entrada

Responsável por expor a API e receber chamadas externas.

- `controller/`: endpoints REST dos módulos do sistema;
- `config/`: configurações globais da aplicação;
- `security/`: autenticação, filtros e regras de acesso;
- `messaging/`: integração com MQTT e eventos de comunicação.

### 2. Camada de aplicação

Centraliza as regras de negócio e a orquestração dos fluxos.

- `service/`: serviços que coordenam validações, persistência e integrações externas;
- `util/`: utilitários de apoio à aplicação.

### 3. Camada de domínio

Representa os conceitos centrais do negócio.

- `domain/`: entidades JPA e modelos da aplicação;
- `domain/enums/`: estados e tipos usados nas regras do sistema.

### 4. Camada de persistência

Responsável pelo acesso aos dados e pela evolução do schema.

- `repository/`: interfaces Spring Data JPA;
- `src/main/resources/db/migration/`: migrations SQL com Flyway.

### 5. Camada de contrato

Padroniza a entrada e saída dos dados da API.

- `dto/`: objetos de requisição e resposta;
- `exception/`: tratamento centralizado de erros e respostas padronizadas.

### Fluxo principal de requisição

```text
Cliente/Frontend -> Controller -> Service -> Repository -> Banco de Dados
```

Para integrações assíncronas, o backend também trabalha com canais de mensageria e cache:

```text
Dispositivo/IoT -> MQTT -> Messaging/Service -> Persistência -> Redis/Resposta para cliente
```

## Tecnologias utilizadas

### Plataforma e framework

- **Java 17**
- **Spring Boot 3.4.4**
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring WebSocket
- Spring Integration

### Persistência e migração

- **MySQL** como banco principal em dev/prod
- **H2** para testes
- **Flyway** para versionamento do schema

### Integração e infraestrutura

- **Redis** para cache e estado rápido
- **MQTT** com Eclipse Paho
- **JWT** para autenticação stateless
- **Docker** e `docker-compose` para ambiente local

### Documentação e produtividade

- **Springdoc OpenAPI / Swagger UI**
- **MapStruct** para mapeamento entre entidades e DTOs
- **Lombok** para redução de boilerplate
- **Testcontainers** para testes de integração

## Módulos principais

Os módulos do backend estão organizados por responsabilidade de negócio:

- **Auth**: login, registro, refresh e logout;
- **Usuários**: consulta e manutenção de usuários;
- **Equipamentos**: cadastro e gestão dos guinchos/equipamentos;
- **Sessão de uso**: rastreio da utilização operacional;
- **Telemetria**: histórico, leitura atual e eventos relacionados ao equipamento;
- **Alertas**: tratamento de ocorrências e estados críticos;
- **Token de desconto**: emissão e consumo de benefícios/promotions;
- **Chat**: histórico e mensagens do atendimento.

## Configuração e ambiente

Por padrão, a aplicação usa o perfil `dev` quando `SPRING_PROFILES_ACTIVE` não é informado.

### Variáveis de ambiente mais relevantes

- `PORT`: porta do servidor HTTP;
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`: conexão com o banco;
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`: conexão com Redis;
- `JWT_SECRET`: segredo para emissão e validação de tokens;
- `APP_ALLOWED_ORIGINS`: origens liberadas no CORS.

## Como executar

### Pré-requisitos

- Java 17;
- Maven 3.9+;
- MySQL 8;
- Redis;
- Docker e Docker Compose, se quiser subir tudo com containers.

### Opção 1: subir com Docker Compose

```bash
docker compose up --build
```

Isso sobe:

- MySQL;
- Redis;
- aplicação Spring Boot.

### Opção 2: executar localmente

1. Suba o MySQL e o Redis localmente ou via Docker.
2. Ajuste as variáveis de ambiente conforme seu ambiente.
3. Execute a aplicação:

```bash
mvn clean test
mvn spring-boot:run
```

## API

### Convenções gerais

- Base URL sugerida: `/api/v1`
- Formato de troca: JSON
- Autenticação: `Authorization: Bearer <token>`

### Documentação da API

Quando a aplicação estiver em execução, a documentação interativa fica disponível em:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Testes e validação

O projeto inclui um script de validação manual em PowerShell:

```powershell
.\test-backend.ps1
```

Ele ajuda a conferir cenários como:

- disponibilidade da aplicação;
- documentação Swagger;
- CORS;
- autenticação;
- acesso a endpoint protegido;
- status dos containers.

Para testes automatizados, use:

```bash
mvn test
```

## Estrutura resumida do código

```text
src/main/java/br/com/everrise
├── config
├── controller
├── domain
├── dto
├── exception
├── messaging
├── repository
├── security
├── service
└── util
```

## Documentação complementar

- `docs/manual-arquitetura.md` — visão arquitetural detalhada;
- `docs/manual-api.md` — contratos e endpoints da API;
- `docs/manual-funcionamento-fluxos.md` — fluxos de negócio e operação;
- `docs/manual-operacao.md` — orientações de operação e ambiente;
- `docs/manual-desenvolvimento-manutencao.md` — guia de evolução e manutenção.

## Resumo

O backend EverRise foi estruturado para ser uma base sólida de evolução do produto, combinando:

- arquitetura em camadas;
- integração com banco relacional, Redis e MQTT;
- segurança com JWT;
- documentação e contratos claros;
- preparo para escalabilidade e manutenção.

Se você quer entender o sistema rapidamente, comece por:

1. `docs/manual-arquitetura.md`
2. `docs/manual-api.md`
3. este `README.md`
