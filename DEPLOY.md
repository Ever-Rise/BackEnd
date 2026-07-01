# Deploy — EverRise Backend (DigitalOcean Droplet)

## Pré-requisitos

- Droplet Ubuntu 22.04+, mínimo 2GB RAM / 2 vCPU (MySQL + Redis + Spring Boot juntos)
- Domínio (ex.: api.everrise.com.br) com registro A apontando para o IP do Droplet
- Acesso SSH ao Droplet com permissões de root ou sudo

## 1. Preparar o servidor

```bash
ssh root@SEU_IP_DROPLET
apt update && apt upgrade -y
curl -fsSL https://get.docker.com | sh
apt install docker-compose-plugin git -y
```

## 2. Clonar o repositório

```bash
git clone <URL_DO_SEU_REPO> everrise-backend
cd everrise-backend
```

## 3. Configurar variáveis de ambiente

```bash
cp .env.example .env
nano .env
```

**Importante:** Preencha TODAS as variáveis com valores fortes e únicos:

- `SPRING_PROFILES_ACTIVE`: mantém como `prod`
- `JWT_SECRET`: chave de pelo menos 64 caracteres (gere com `openssl rand -base64 32`)
- `MYSQL_ROOT_PASSWORD`: senha forte para root do MySQL
- `MYSQL_PASSWORD`: senha do usuário `everrise`
- `REDIS_PASSWORD`: senha do Redis
- `DB_PASSWORD`: deve ser idêntica a `MYSQL_PASSWORD`
- `APP_ALLOWED_ORIGINS`: domínios que podem acessar a API (ex.: `https://everrise.com.br,https://www.everrise.com.br`)

Gere senhas com:
```bash
openssl rand -base64 32
```

## 4. Ajustar domínio no Nginx

```bash
nano nginx/conf.d/everrise.conf
```

Troque **todas as 3 ocorrências** de `SEU_DOMINIO_AQUI` pelo seu domínio real (ex.: `api.everrise.com.br`).

## 5. Build e subida inicial (sem HTTPS ainda)

```bash
docker compose -f docker-compose.prod.yml up -d --build mysql redis app nginx
```

Aguarde ~30 segundos para que os serviços iniciem.

## 6. Validar que a app está saudável ANTES de seguir

```bash
docker compose -f docker-compose.prod.yml ps
```

Todos os serviços devem estar com status `Up`.

```bash
docker exec -it everrise-backend curl -f http://localhost:8080/actuator/health
```

Deve retornar:
```json
{"status":"UP", ...}
```

### Se falhar aqui:
- Não prossiga para emitir o certificado!
- Rode: `docker compose -f docker-compose.prod.yml logs app`
- Revise a configuração do `.env` e segurança (Security Filter Chain)

## 7. Emitir o certificado SSL

**Pré-condição:** O domínio já precisa estar resolvendo para o IP do Droplet (registrador DNS atualizado).

```bash
docker compose -f docker-compose.prod.yml run --rm certbot certonly \
  --webroot -w /var/www/certbot -d SEU_DOMINIO_AQUI
```

Substitua `SEU_DOMINIO_AQUI` pelo domínio real. Siga os prompts do Certbot (pode precisar aceitar os termos ou confirmar email).

## 8. Reiniciar o Nginx com HTTPS ativo

```bash
docker compose -f docker-compose.prod.yml restart nginx
```

## 9. Validar de fora do servidor

De sua máquina local:
```bash
curl -f https://SEU_DOMINIO_AQUI/actuator/health
```

Deve retornar o status da saúde da aplicação via HTTPS.

## 10. Renovação automática do certificado

O serviço `certbot` do compose já está configurado para renovar automaticamente a cada 12 horas. Confirme que está em pé:

```bash
docker compose -f docker-compose.prod.yml ps certbot
```

Deve estar `Up` ou `Running`.

---

## Comandos úteis de manutenção

### Ver logs em tempo real

```bash
docker compose -f docker-compose.prod.yml logs -f app
```

Ou sair com `Ctrl+C`.

### Ver logs de um serviço específico

```bash
docker compose -f docker-compose.prod.yml logs -f mysql
docker compose -f docker-compose.prod.yml logs -f nginx
docker compose -f docker-compose.prod.yml logs -f certbot
```

### Redeploy após git pull

Se houver atualizações no código:

```bash
git pull
docker compose -f docker-compose.prod.yml build --no-cache app
docker compose -f docker-compose.prod.yml up -d app
```

### Backup do banco

```bash
docker exec everrise-mysql mysqldump -u root -p"${MYSQL_ROOT_PASSWORD}" everrise_prod > backup_$(date +%F).sql
```

Copia o arquivo SQL para sua máquina:
```bash
scp root@SEU_IP_DROPLET:backup_*.sql .
```

### Restaurar um backup

```bash
docker exec -i everrise-mysql mysql -u root -p"${MYSQL_ROOT_PASSWORD}" everrise_prod < backup_2024-01-15.sql
```

### Parar tudo

```bash
docker compose -f docker-compose.prod.yml down
```

### Parar tudo e apagar volumes (CUIDADO: apaga dados do banco!)

```bash
docker compose -f docker-compose.prod.yml down -v
```

---

## Troubleshooting

### Aplicação demora muito para iniciar

MySQL ou Redis podem estar demorando. Verifique os logs:
```bash
docker compose -f docker-compose.prod.yml logs mysql
docker compose -f docker-compose.prod.yml logs redis
```

### Certbot não consegue emitir certificado

Confirme que:
1. O domínio está resolvendo para o IP do Droplet
2. O firewall permite tráfego na porta 80 (necessário para validação ACME)
3. Os logs do certbot não têm erros: `docker compose -f docker-compose.prod.yml logs certbot`

### Redis não conecta

Verifique senha:
```bash
docker exec -it everrise-redis redis-cli -a SEU_REDIS_PASSWORD ping
```

### Aplicação não consegue conectar ao MySQL

Verifique variáveis do `.env`:
- `DB_HOST` deve ser `mysql`
- `DB_USER`, `DB_PASSWORD`, `DB_NAME` devem coincidir com `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_DATABASE`

---

## Segurança

- Nunca commite o `.env` real (apenas `.env.example`)
- Mantenha as senhas em um gerenciador de senhas pessoal
- Backups devem ser armazenados em local seguro
- Considere usar um firewall UFW para restringir acesso ao Droplet a IPs conhecidos
- Atualize regularmente as imagens Docker: `docker pull mysql:8.0 && docker pull redis:7-alpine && docker pull nginx:alpine`

---

## Próximos passos

Após o deploy estar rodando:

1. Configure seu domínio no frontend para apontar para `https://SEU_DOMINIO_AQUI`
2. Rode testes de carga/stress para validar que o Droplet aguenta o volume esperado
3. Configure monitoramento e alertas (ex.: Datadog, NewRelic)
4. Estabeleça procedimento de backup automático
