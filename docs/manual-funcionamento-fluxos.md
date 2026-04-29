# Manual de Funcionamento e Fluxos

## 1. Objetivo
Detalhar como os principais fluxos do backend funcionam de ponta a ponta.

## 2. Fluxo de Autenticacao
### 2.1 Registro
1. Cliente envia `POST /api/v1/auth/register`.
2. Backend valida payload e cria usuario com senha criptografada.
3. Backend retorna access token + refresh token.

### 2.2 Login
1. Cliente envia `POST /api/v1/auth/login`.
2. Credenciais sao validadas via `AuthenticationManager`.
3. Backend retorna novos tokens e dados do usuario.

### 2.3 Refresh
1. Cliente envia `POST /api/v1/auth/refresh` com refresh token.
2. Backend valida token e blacklist.
3. Backend emite novo par de tokens.

### 2.4 Logout
1. Cliente envia `POST /api/v1/auth/logout`.
2. Backend invalida token em blacklist no Redis.

## 3. Fluxo de Vinculacao de Dispositivo
1. Usuario autenticado chama `POST /api/v1/auth/device/bind`.
2. Sistema cria/atualiza sessao ativa para guincho + device fingerprint.
3. Comando para guincho exige sessao valida.

## 4. Fluxo de Comando do Guincho
Entrada possivel por:
- REST: `POST /api/v1/controle/{id}/comando`
- WebSocket STOMP: `/app/guincho/{id}/comando`

Processamento:
1. Validar acesso ao guincho e sessao ativa.
2. Validar regras de estado (emergencia, bloqueio por obstaculo/anomalia).
3. Publicar comando no MQTT.
4. Atualizar estado do guincho e cache Redis.
5. Notificar clientes no topico WebSocket do guincho.

## 5. Fluxo de Telemetria
1. Dispositivo publica em `guincho/{id}/telemetry`.
2. `MqttMessageHandler` interpreta payload.
3. Estado do guincho e atualizado.
4. Eventos relevantes sao persistidos no banco.
5. Cache da telemetria mais recente e atualizado no Redis.
6. Alertas sao enviados via WebSocket.

## 6. Fluxo de Pagamento e Pedido
1. Cliente cria pedido com `POST /api/v1/pedidos/checkout`.
2. Webhook de pagamento chega em `POST /api/v1/pagamentos/webhook`.
3. Backend valida assinatura e atualiza status do pedido.
4. Em caso de pagamento aprovado, plano do usuario e atualizado.

## 7. Fluxo de Chatbot
1. Cliente chama `POST /api/v1/chatbot/message`.
2. Backend abre stream SSE e envia chunks de resposta.
3. Conversa e persistida por sessao.
4. Historico e consultado em `GET /api/v1/chatbot/history/{sessionId}`.

## 8. Eventos em Tempo Real
- Topico geral por guincho: `/topic/guincho/{id}`
- Topico de telemetria: `/topic/telemetry/{id}`

## 9. Falhas Comuns de Fluxo
- Falha de credenciais/secrets em ambiente.
- Sessao de dispositivo ausente para comando.
- Inconsistencia entre schema e entidades JPA.
- Incompatibilidade de payload de webhook.
