# Documentacao EVERRISE Backend

Este diretorio concentra o manual tecnico e operacional do backend EVERRISE.

## Como usar esta documentacao

1. Comece por `docs/manual-arquitetura.md` para entender a estrutura do projeto.
2. Leia `docs/manual-funcionamento-fluxos.md` para compreender os fluxos REST, MQTT e WebSocket.
3. Use `docs/manual-api.md` para evoluir endpoints e integrar com frontend.
4. Consulte `docs/manual-desenvolvimento-manutencao.md` para padroes de manutencao e extensao.
5. Consulte `docs/manual-operacao.md` para ambientes, deploy e troubleshooting.

## Arquivos

- `docs/manual-arquitetura.md`: visao completa da arquitetura em camadas e modulos.
- `docs/manual-funcionamento-fluxos.md`: fluxo de autenticacao, controle, telemetria e pagamentos.
- `docs/manual-api.md`: mapa de controllers, contratos, status esperados e exemplos de uso.
- `docs/manual-desenvolvimento-manutencao.md`: como criar funcionalidades novas com seguranca.
- `docs/manual-operacao.md`: configuracao de ambiente, execucao, monitoramento e correcoes comuns.

## Roteiro de consolidacao tecnica
Use os manuais em conjunto para organizar as proximas entregas:

1. Arquitetura: validar responsabilidades e limites por camada.
2. Fluxos: revisar regras de negocio ponta a ponta.
3. API: estabilizar contratos e tratar compatibilidade.
4. Manutencao: aplicar checklist de PR e definicao de pronto.
5. Operacao: fechar setup de ambientes e runbook de incidentes.

