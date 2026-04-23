# EVERRISE Backend

Backend do projeto EVERRISE para autenticacao, controle de guinchos, telemetria em tempo real, pedidos/pagamentos e chatbot.

## Objetivo deste repositorio
Consolidar uma base tecnica segura e evolutiva para os proximos ciclos de desenvolvimento.

## Stack
- Java 17
- Spring Boot 3.4.x
- Spring Security (JWT)
- Spring Data JPA
- WebSocket (STOMP)
- MQTT
- Redis
- Flyway

## Estrutura
- `src/main/java`: codigo fonte principal
- `src/main/resources`: configuracoes e migrations
- `src/test/java`: testes
- `docs`: documentacao tecnica e operacional

## Documentacao
Comece por `docs/README.md` para navegar pelos manuais.

## Como rodar (visao geral)
1. Configure banco, redis e broker MQTT.
2. Defina variaveis de ambiente e secrets necessarios.
3. Inicie o backend com perfil `dev`.
4. Valide healthcheck e rotas principais.

## Proximos passos recomendados
1. Garantir build verde com dependencias completas no `pom.xml`.
2. Alinhar migrations com entidades JPA.
3. Aumentar cobertura de testes nos fluxos criticos.
4. Fortalecer regras de autenticacao e autorizacao.
