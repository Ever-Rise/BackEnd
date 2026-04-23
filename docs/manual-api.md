# Manual de API

## 1. Convencoes Gerais
- Prefixo base: `/api/v1`
- Formato JSON para requests/responses
- Validacao por annotations Jakarta Validation
- Erros padronizados por `GlobalExceptionHandler`

## 2. Modulo Auth
- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/refresh`
- `POST /auth/logout`
- `POST /auth/device/bind`
- `DELETE /auth/device/unbind/{guinchoId}`

## 3. Modulo Guinchos
- `GET /guinchos`
- `GET /guinchos/{id}`
- `GET /guinchos/{id}/status`
- `GET /guinchos/{id}/telemetry?limit=&from=&to=`
- `POST /guinchos`
- `PATCH /guinchos/{id}`
- `DELETE /guinchos/{id}`

## 4. Modulo Controle
- `POST /controle/{id}/comando`
- `GET /controle/{id}/status`
- `POST /controle/{id}/emergencia`

## 5. Modulo Telemetria
- `GET /telemetry/guincho/{id}/history?limit=&from=&to=`
- `GET /telemetry/guincho/{id}/latest`
- `GET /telemetry/guincho/{id}/alerts?limit=`

## 6. Modulo Planos e Pedidos
- `GET /planos`
- `POST /pedidos/checkout`
- `GET /pedidos/me`

## 7. Modulo Pagamentos
- `POST /pagamentos/webhook`

## 8. Modulo Chatbot
- `POST /chatbot/message` (SSE)
- `GET /chatbot/history/{sessionId}`

## 9. Modulo Usuarios
- `GET /users`
- `GET /users/{id}`

## 10. Autenticacao
- Header esperado: `Authorization: Bearer <token>`
- Rotas publicas devem estar explicitamente permitidas em `SecurityConfig`

## 11. Status Codes Esperados
- `200 OK`: consulta/processamento comum
- `201 Created`: criacao de recurso
- `202 Accepted`: publicacao de comando assincrono
- `400 Bad Request`: validacao/regra de negocio
- `401 Unauthorized`: autenticacao invalida
- `404 Not Found`: recurso inexistente
- `409 Conflict`: conflito de sessao/estado
- `500 Internal Server Error`: erro nao tratado

## 12. Boas Praticas para Evoluir API
1. Preservar versionamento (`/api/v1`).
2. Nao quebrar contrato de DTO sem estrategia de migracao.
3. Incluir casos de erro no Swagger/OpenAPI.
4. Adicionar testes de controller para novos endpoints.
