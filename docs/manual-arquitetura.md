# Manual de Arquitetura - EVERRISE Backend

## 1. Objetivo
Este documento descreve a arquitetura atual do backend EVERRISE para orientar evolucao tecnica, manutencao e onboarding.

## 2. Visao Geral
O sistema e um backend Spring Boot orientado a dominio para:
- autenticacao de usuarios
- controle de guinchos hospitalares
- processamento de telemetria em tempo real
- pagamentos e pedidos de planos
- chatbot com streaming SSE

## 3. Stack Tecnica
- Java 17
- Spring Boot 3.4.x
- Spring Web, Validation, Security
- Spring Data JPA
- WebSocket (STOMP)
- MQTT (Eclipse Paho + Spring Integration)
- Redis (cache e blacklist de token)
- Flyway (migrations SQL)
- MySQL (dev/prod)
- H2 (test)

## 4. Arquitetura em Camadas
### 4.1 Camada de Entrada
- `controller`: endpoints REST e streaming SSE
- `websocket`: handshake JWT e comandos STOMP
- `messaging`: recebimento/publicacao MQTT

### 4.2 Camada de Aplicacao
- `service`: regras de negocio, orquestracao e transacoes
- `util`: funcoes de apoio como contexto de seguranca e builder de topicos MQTT

### 4.3 Camada de Dominio
- `domain/entity`: entidades JPA
- `domain/enums`: estados e tipos do negocio

### 4.4 Camada de Persistencia
- `repository`: interfaces Spring Data para consultas e escrita
- `db/migration`: versionamento do schema com Flyway

### 4.5 Camada de Contrato
- `dto/request`: payloads de entrada
- `dto/response`: payloads de saida
- `mapper`: conversao entre entidades e DTOs (MapStruct)

## 5. Fluxos Estruturais
- REST -> Controller -> Service -> Repository -> Banco
- Telemetria MQTT -> MqttMessageHandler -> Atualizacao de estado + persistencia -> Redis/WebSocket
- Comando REST/WebSocket -> GuinchoService -> MqttPublisher -> Dispositivo

## 6. Componentes de Configuracao
- `SecurityConfig`: filtro JWT, autorizacao de rotas e sessao stateless
- `CorsConfig`: origem permitida por variavel de ambiente
- `MqttConfig`: conexao broker e assinatura de topicos
- `WebSocketConfig`: broker interno STOMP e endpoint `/ws`
- `RedisConfig`: template Redis para cache/estado
- `OpenApiConfig`: documentacao OpenAPI/Swagger

## 7. Dependencias Criticas de Runtime
Para operacao completa, o ambiente deve prover:
- Banco SQL (MySQL em dev/prod)
- Redis
- Broker MQTT
- Secrets de JWT e webhook de pagamento

## 8. Decisoes Arquiteturais
- Sessao HTTP desabilitada: autenticacao baseada em JWT
- Estado rapido em Redis com TTL curto para telemetria/status
- Mensageria assicrona (MQTT) para acoplamento com hardware
- API versionada por prefixo `/api/v1`

## 9. Riscos Arquiteturais Atuais
- Dependencias usadas no codigo mas ausentes no `pom.xml`
- Divergencias entre entidades e migrations SQL
- Cobertura de testes insuficiente para modulos criticos

## 10. Proximos Passos de Consolidacao
1. Normalizar dependencias Maven e garantir build verde.
2. Alinhar schema Flyway com modelo JPA.
3. Fortalecer autorizacao em endpoints e canais de tempo real.
4. Cobrir fluxos criticos com testes de servico e integracao.
