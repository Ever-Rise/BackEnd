# Manual de Desenvolvimento e Manutencao

## 1. Objetivo
Definir um processo consistente para evoluir o backend com seguranca e previsibilidade.

## 2. Principios de Codigo
- Separar responsabilidades por camada.
- Centralizar regra de negocio em `service`.
- Evitar logica pesada em `controller`.
- Usar DTOs para entrada e saida.
- Tratar erros por excecoes de dominio + handler global.

## 3. Fluxo para Nova Funcionalidade
1. Criar/ajustar entidades e enums necessarios.
2. Criar migration Flyway para schema.
3. Criar repository e consultas.
4. Implementar servico com regras de negocio e transacao.
5. Expor endpoint/controller.
6. Criar/atualizar DTOs e mappers.
7. Criar testes unitarios e de integracao.
8. Atualizar documentacao em `docs/`.

## 4. Convencoes de Naming
- Controller: `*Controller`
- Service interface: `*Service`
- Service impl: `*ServiceImpl`
- Repository: `*Repository`
- Request DTO: `*Request`
- Response DTO: `*Response`

## 5. Seguranca no Desenvolvimento
- Toda nova rota deve ser revisada em `SecurityConfig`.
- Evitar `permitAll` sem justificativa.
- Validar contexto do usuario (`SecurityUtils`) antes de acessar dados sensiveis.
- Nunca commitar secrets em arquivo versionado.

## 6. Qualidade e Testes
Cobertura minima recomendada por modulo critico:
- Auth: login, refresh, logout e invalidacao de token
- Controle de guincho: regras de estado e permissao
- Pagamentos: webhook valido/invalido e transicao de pedido
- Telemetria: parsing de payload e persistencia

Tipos de teste recomendados:
- Unitarios para regras de negocio
- Integracao para repositorios e fluxo com banco
- Contrato para endpoints REST principais

## 7. Checklist de PR
- Build local executado com sucesso
- Testes relevantes executados
- Migration validada
- Contrato de API revisado
- Documentacao atualizada
- Sem segredo exposto

## 8. Gestao de Divida Tecnica
Registrar e priorizar:
- gaps de seguranca
- inconsistencias de schema
- cobertura de teste ausente
- dependencia faltante no build

## 9. Definicao de Pronto (DoD)
Uma entrega e considerada pronta quando:
1. Funcionalidade atende criterio de aceite.
2. Testes passam no pipeline.
3. Documentacao foi atualizada.
4. Nao adiciona regressao de seguranca.
