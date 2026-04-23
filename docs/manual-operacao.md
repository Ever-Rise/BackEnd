# Manual de Operacao

## 1. Objetivo
Padronizar setup, execucao, monitoramento e troubleshooting do backend.

## 2. Perfis de Ambiente
- `dev`: MySQL local, Redis local, logs detalhados
- `test`: H2 em memoria, Flyway desabilitado
- `prod`: variaveis de ambiente para banco e Redis

## 3. Variaveis e Configuracoes Minimas
Exemplos de configuracao obrigatoria:
- Banco: `spring.datasource.*` ou `DATABASE_URL`
- Redis: host/porta ou `REDIS_URL`
- JWT: `app.jwt.secret`, `app.jwt.expiration`, `app.jwt.refresh-expiration`
- MQTT: `app.mqtt.broker-url`, `app.mqtt.username`, `app.mqtt.password`
- Pagamento: `app.mercadopago.webhook-secret`
- CORS: `APP_ALLOWED_ORIGINS`

## 4. Execucao Local
Passos recomendados:
1. Subir dependencias (MySQL, Redis, broker MQTT).
2. Selecionar profile `dev`.
3. Rodar aplicacao Spring Boot.
4. Validar healthcheck e Swagger.

## 5. Banco e Migration
- Migrations em `src/main/resources/db/migration`.
- Toda alteracao de schema deve gerar nova migration versionada.
- Nunca editar migration ja aplicada em ambiente compartilhado.

## 6. Observabilidade
Minimo recomendado:
- Log estruturado por modulo
- Correlacao por request id
- Monitorar erros 4xx/5xx
- Monitorar taxa de comandos e eventos MQTT

## 7. Troubleshooting
### 7.1 Aplicacao nao sobe
Possiveis causas:
- Dependencia Maven ausente
- Variavel obrigatoria nao configurada
- Conexao com banco/redis/mqtt indisponivel

### 7.2 Erro de schema
Possiveis causas:
- Divergencia entre entidade JPA e migration
- Colunas obrigatorias ausentes no banco

### 7.3 Token invalido em rotas autenticadas
Possiveis causas:
- Secret JWT incorreto
- Token expirado
- Header Authorization ausente

### 7.4 WebSocket sem eventos
Possiveis causas:
- Origem bloqueada por CORS
- Handshake sem token valido
- Topico incorreto no cliente

## 8. Checklist de Go Live
1. Validar secrets e origem CORS de producao.
2. Revisar regras de autorizacao em endpoints e websocket.
3. Confirmar migrations aplicadas em ordem.
4. Validar fluxo de pagamento em ambiente de homologacao.
5. Executar testes de regressao basicos.
