# EverRise Backend

Backend do projeto EverRise Medical Solutions.

## Requisitos

- Java 21 LTS
- Maven 3.9+
- MySQL para o perfil `prod`

## Perfis

- `dev`: usa H2 em memória com console habilitado
- `test`: usa H2 em memória com configurações voltadas para testes automatizados
- `prod`: espera variáveis de ambiente para MySQL

## Como executar

```powershell
mvn clean test
```

```powershell
mvn spring-boot:run
```

Para escolher um perfil explicitamente:

```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Variáveis de ambiente do perfil `prod`

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
