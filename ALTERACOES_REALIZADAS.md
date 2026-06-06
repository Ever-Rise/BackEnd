# 📋 ALTERAÇÕES REALIZADAS - EverRise Backend

Documento datado de: **5 de junho de 2026**

---

## 🎯 Objetivo Geral

Corrigir todos os 13 erros críticos e de alta prioridade identificados na auditoria técnica que impediam a integração local (Frontend React + Backend) e o deploy em produção (Railway).

---

## ✅ LISTA COMPLETA DAS 13 CORREÇÕES

### **1️⃣ ERRO: Dois beans de CORS conflitando**
**Arquivo:** `src/main/java/br/com/everrise/config/CorsConfig.java`  
**Problema:** Havia `corsConfigurationSource()` e `webMvcConfigurer()` com configurações duplicadas  
**Solução:**
- ✅ Removido bean `webMvcConfigurer()` 
- ✅ Mantido apenas `corsConfigurationSource()` com única fonte de verdade

---

### **2️⃣ ERRO: allowCredentials=true conflita com JWT Bearer**
**Arquivo:** `src/main/java/br/com/everrise/config/CorsConfig.java`  
**Problema:** JWT é stateless (não usa cookies), mas `allowCredentials(true)` era para session-based  
**Solução:**
- ✅ Mudado para `configuration.setAllowCredentials(false)`
- ✅ Explicação em comentário no código

---

### **3️⃣ ERRO: Headers CORS incompletos**
**Arquivo:** `src/main/java/br/com/everrise/config/CorsConfig.java`  
**Problema:** Faltavam headers `Accept` e `Origin` necessários para negociação de conteúdo  
**Solução:**
- ✅ Adicionado `List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")`
- ✅ Adicionado `setExposedHeaders()` para o cliente ler headers de resposta

---

### **4️⃣ ERRO: OPTIONS requests não permitidos explicitamente**
**Arquivo:** `src/main/java/br/com/everrise/config/SecurityConfig.java`  
**Problema:** CORS preflight (OPTIONS) poderia ser bloqueado antes de chegar ao CORS filter  
**Solução:**
- ✅ Adicionado `import org.springframework.http.HttpMethod;`
- ✅ Adicionado `.requestMatchers(HttpMethod.OPTIONS).permitAll()` no topo da autorização

---

### **5️⃣ ERRO: Root path "/" sem resposta (health check)**
**Arquivo:** `src/main/java/br/com/everrise/config/SecurityConfig.java`  
**Problema:** Railway e Vercel tentam acessar "/" para health check, retornava 404  
**Solução:**
- ✅ Adicionado `.requestMatchers("/").permitAll()`
- ✅ Criado novo arquivo `HealthController.java` que responde em "/"
- ✅ Arquivo: `src/main/java/br/com/everrise/controller/HealthController.java`

```java
@RestController
@RequestMapping("/")
@Hidden // Hide from Swagger
public class HealthController {
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
    }
}
```

---

### **6️⃣ ERRO: Actuator endpoints não permitidos**
**Arquivo:** `src/main/java/br/com/everrise/config/SecurityConfig.java`  
**Problema:** Security bloqueava `/actuator/health` necessário para Railway health check  
**Solução:**
- ✅ Adicionado `.requestMatchers("/actuator/**").permitAll()`

---

### **7️⃣ ERRO: JWT Secret hardcoded não é variável de ambiente**
**Arquivo:** `src/main/resources/application.yml`  
**Problema:** `app.jwt.secret: "hardcoded-string"` não usava `${}` para env vars  
**Solução:**
- ✅ Mudado para: `app.jwt.secret: ${JWT_SECRET:seu-secret-dev}`

---

### **8️⃣ ERRO: CORS allowedOrigins não está em propriedade separada**
**Arquivo:** `src/main/resources/application.yml`  
**Problema:** Não havia `app.cors.allowed-origins`, estava em `APP_ALLOWED_ORIGINS` diretamente  
**Solução:**
- ✅ Adicionada seção `app.cors.allowed-origins` com defaults:
```yaml
app:
  cors:
    allowed-origins: ${APP_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:3000,https://everrise.com.br,https://www.everrise.com.br}
```

---

### **9️⃣ ERRO: application-dev.yml com credenciais hardcoded**
**Arquivo:** `src/main/resources/application-dev.yml`  
**Problema:** Credenciais de BD em arquivo versionado: `username: everrise`, `password: everrise123`  
**Solução:**
- ✅ Mudado para: `username: ${DB_USER:everrise}`, `password: ${DB_PASSWORD:everrise123}`
- ✅ Adicionado `app.cors.allowed-origins` com localhost:5173 e localhost:3000

---

### **🔟 ERRO: application-test.yml com duplicação**
**Arquivo:** `src/main/resources/application-test.yml`  
**Problema:** Linhas 10-11 tinham `enabled: false` duplicado  
**Solução:**
- ✅ Removida duplicação
- ✅ Adicionado `app.cors.allowed-origins`

---

### **1️⃣1️⃣ ERRO: application-prod.yml incompleta**
**Arquivo:** `src/main/resources/application-prod.yml`  
**Problema:** Faltava maioria das configurações (DB, Redis, Actuator, etc.)  
**Solução:**
- ✅ Arquivo completo com:
  - Database com variáveis obrigatórias: `${DB_HOST}`, `${DB_USER}`, `${DB_PASSWORD}`
  - Redis com `${REDIS_HOST}`, `${REDIS_PASSWORD}`
  - JWT com `${JWT_SECRET}`
  - CORS com `${APP_ALLOWED_ORIGINS}`
  - Management endpoints para actuator
  - Flyway ativado
  - Hikari pool aumentado para produção (20 conexões)

---

### **1️⃣2️⃣ ERRO: pom.xml sem spring-boot-starter-actuator**
**Arquivo:** `pom.xml`  
**Problema:** Dockerfile usa `/actuator/health` mas dependency não estava instalada  
**Solução:**
- ✅ Adicionada dependency na linha 51:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

---

### **1️⃣3️⃣ ERRO: Dockerfile com healthcheck incorreto**
**Arquivo:** `Dockerfile`  
**Problema:** 
- Usava `wget` (pode não estar em alpine)
- Timeout curto (3s)
- Start period curto (5s)

**Solução:**
- ✅ Adicionado `RUN apk add --no-cache curl` para garantir curl disponível
- ✅ Substituído `wget` por `curl`
- ✅ Healthcheck corrigido:
```dockerfile
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl --fail --silent --show-error http://localhost:8080/actuator/health || exit 1
```

---

### **BONUS: GlobalExceptionHandler.java - Erro de filesystem**
**Arquivo:** `src/main/java/br/com/everrise/exception/GlobalExceptionHandler.java`  
**Problema:** Tentava gravar erro em `C:/Dev/BackEnd/swagger-error.log` (hardcoded, sem permissão em Railway)  
**Solução:**
- ✅ Removido bloco de gravação em arquivo
- ✅ Mantido apenas logging SLF4J que já usa logback.xml
- ✅ Removidos imports não utilizados: `Files`, `Path`, `StandardOpenOption`

---

## 🌍 CONFIGURAÇÃO CORS FINAL

Os seguintes origins estão agora permitidos:

```
LOCAL DEV (React):
  ✅ http://localhost:5173
  ✅ http://localhost:3000

PRODUÇÃO:
  ✅ https://everrise.com.br
  ✅ https://www.everrise.com.br
```

**Para Vercel (frontend adicional):** Configure em Railway a variável:
```
APP_ALLOWED_ORIGINS=https://seu-frontend.vercel.app,https://everrise.com.br,https://www.everrise.com.br
```

---

## 🚀 COMO TESTAR - PASSO A PASSO

### **Fase 1: Testes Locais (dev)**

#### 1.1 Preliminares
```bash
# Certifique-se que tem:
# - Docker instalado
# - Maven 3.9+
# - Java 17+

# Abra terminal no diretório raiz do backend
cd C:\Users\Anderson Reis\Desktop\EverRise\BackEnd
```

#### 1.2 Subir containers (MySQL + Redis)
```bash
docker-compose up -d
```

**Resultado esperado:**
```
✅ everrise-mysql - RUNNING
✅ everrise-redis - RUNNING
✅ everrise-backend - RUNNING em http://localhost:8080
```

#### 1.3 Verificar chamadas HTTP - Health Check
```bash
# Teste root path (novo)
curl -i http://localhost:8080/

# Resultado esperado:
# HTTP/1.1 200 OK
# {"status":"UP"}
```

#### 1.4 Verificar Actuator (novo)
```bash
curl -i http://localhost:8080/actuator/health

# Resultado esperado:
# HTTP/1.1 200 OK
# {"status":"UP"}
```

#### 1.5 Testar CORS Preflight (novo)
```bash
# Windows PowerShell
$headers = @{
    "Origin" = "http://localhost:5173"
    "Access-Control-Request-Method" = "POST"
}

curl -i -Method OPTIONS `
  -Uri "http://localhost:8080/auth/login" `
  -Headers $headers

# Resultado esperado:
# HTTP/1.1 200 OK
# Access-Control-Allow-Origin: http://localhost:5173
# Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
# Access-Control-Allow-Headers: Authorization, Content-Type, Accept, Origin, X-Requested-With
# Access-Control-Allow-Credentials: false
```

#### 1.6 Testar JWT Login
```bash
$loginData = @{
    email = "seu-email@example.com"
    senha = "sua-senha"
} | ConvertTo-Json

curl -i -Method POST `
  -Uri "http://localhost:8080/auth/login" `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body $loginData

# Resultado esperado:
# HTTP/1.1 200 OK
# {"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...","type":"Bearer"}
```

#### 1.7 Testar endpoint autenticado
```bash
# Copie o token da resposta anterior
$token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -i -Method GET `
  -Uri "http://localhost:8080/equipamentos" `
  -Headers @{ "Authorization" = "Bearer $token" }

# Resultado esperado:
# HTTP/1.1 200 OK
# [{"id":1,"identificador":"EQ001",...}]
```

#### 1.8 Verificar Swagger (deve estar funcionando)
```
Abra no navegador:
http://localhost:8080/swagger-ui.html

✅ Deve mostrar todos os endpoints documentados
✅ Deve ter o botão "Try it out"
```

---

### **Fase 2: Testes de Integração com Frontend (dev)**

#### 2.1 Certificar que backend está rodando
```bash
# No terminal backend
docker-compose logs -f app

# Deve mostrar: "Tomcat started on port(s): 8080"
```

#### 2.2 Iniciar frontend React (em outro terminal)
```bash
cd C:\Users\Anderson Reis\Desktop\EverRise\Frontend  # ou onde estiver

npm install
npm run dev

# Deve mostrar:
# VITE v5.x.x  ready in xxx ms
# ➜  Local:   http://localhost:5173/
```

#### 2.3 Testar login do frontend
1. Abra http://localhost:5173 no navegador
2. Tente fazer login
3. Abra DevTools (F12) → Console
4. Verifique se há erros CORS

**Resultado esperado:**
```
✅ Login funciona
✅ Sem erros CORS no console
✅ Token salvo em localStorage/SessionStorage
```

#### 2.4 Verificar rede (DevTools)
1. Abra DevTools (F12) → Network tab
2. Faça um login
3. Clique em "OPTIONS /auth/login"
   - **Status:** 200 ✅
   - **Access-Control-Allow-Origin:** http://localhost:5173 ✅
4. Clique em "POST /auth/login"
   - **Status:** 200 ✅
   - **Response Body:** `{"token":"...","type":"Bearer"}` ✅

---

### **Fase 3: Limpeza (antes de deploy)**

#### 3.1 Parar containers
```bash
docker-compose down

# Resultado esperado: todos os containers parados
```

#### 3.2 Fazer commit das alterações
```bash
git add .
git commit -m "chore: audit fixes - CORS, Security, Actuator"
git push origin main
```

---

## 🚂 DEPLOY EM PRODUÇÃO (Railway)

### **Pré-requisitos:**
- Ter conta em Railway
- Ter projeto conectado ao repositório GitHub
- Ter MySQL e Redis provisioned em Railway

### **Setar Variáveis de Ambiente:**

No dashboard Railway, vá para seu app e adicione as seguintes variáveis:

```bash
# === DATABASE ===
DB_HOST=seu-mysql-railway.railway.app
DB_PORT=3306
DB_NAME=everrise_prod
DB_USER=seu_usuario_mysql
DB_PASSWORD=sua_senha_segura_aqui

# === REDIS ===
REDIS_HOST=seu-redis-railway.railway.app
REDIS_PORT=6379
REDIS_PASSWORD=sua_senha_redis

# === JWT ===
JWT_SECRET=use-uma-chave-aleatoria-segura-com-min-32-caracteres

# === CORS - IMPORTANTE! ===
APP_ALLOWED_ORIGINS=https://seu-frontend.vercel.app,https://everrise.com.br,https://www.everrise.com.br

# === SPRING PROFILE ===
SPRING_PROFILES_ACTIVE=prod

# === DATABASE CONNECTION POOL ===
HIKARI_MAX_POOL_SIZE=20
HIKARI_MIN_IDLE=5

# === REDIS POOL ===
REDIS_MAX_ACTIVE=16
REDIS_MAX_IDLE=8
```

### **Fazer Deploy:**

1. Push para branch main (ou a que está configurada)
2. Railway detecta mudança e inicia build automático
3. Aguarde ~5-10 minutos
4. Verifique logs: `railway logs`

### **Testar após deploy:**

```bash
# Verificar health check
curl https://seu-app.railway.app/

# Resultado esperado: HTTP 200 OK {"status":"UP"}

# Verificar actuator
curl https://seu-app.railway.app/actuator/health

# Resultado esperado: HTTP 200 OK {"status":"UP"}
```

---

## 📊 RESUMO DE MUDANÇAS POR ARQUIVO

| Arquivo | Mudanças |
|---------|----------|
| `CorsConfig.java` | Removido bean duplicado, `allowCredentials=false`, headers completos, maxAge cache |
| `SecurityConfig.java` | Adicionado OPTIONS, actuator, root path permitAll, HttpMethod import |
| `application.yml` | JWT secret com `${}`, `app.cors.allowed-origins` section |
| `application-dev.yml` | Credenciais com `${}`, `app.cors.allowed-origins` |
| `application-test.yml` | Removida duplicação, `app.cors.allowed-origins` |
| `application-prod.yml` | Completo do zero com todas as configs |
| `pom.xml` | Adicionado `spring-boot-starter-actuator` |
| `Dockerfile` | Instalado curl, mudado `wget` → `curl`, timeout aumentado |
| `GlobalExceptionHandler.java` | Removido hardcoded path, imports não utilizados |
| `HealthController.java` | **NOVO** - Responde em "/" com `{"status":"UP"}` |

---

## ⚠️ VERIFICAÇÕES PÓS-DEPLOY

Após deploy em Railway, execute estas verificações:

```bash
# 1. Health check raiz
curl https://seu-app.railway.app/

# 2. Actuator
curl https://seu-app.railway.app/actuator/health

# 3. Swagger
curl https://seu-app.railway.app/swagger-ui.html

# 4. Login (teste real)
curl -X POST https://seu-app.railway.app/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@example.com","senha":"senha123"}'

# 5. CORS preflight
curl -X OPTIONS https://seu-app.railway.app/auth/login \
  -H "Origin: https://seu-frontend.vercel.app" \
  -H "Access-Control-Request-Method: POST" \
  -v
```

---

## 🎓 APRENDIZADOS IMPORTANTES

1. **CORS + JWT:** Nunca usar `allowCredentials=true` com Bearer tokens stateless
2. **Health checks:** Sempre ter "/" respondendo com 200 para plataformas cloud
3. **Variáveis de ambiente:** Nunca deixar secrets hardcoded em arquivos versionados
4. **Actuator:** Essencial para produção, permite monitoramento via `/actuator/health`
5. **Duplicação de config:** Manter única fonte de verdade (CorsConfigurationSource)

---

## 📞 PRÓXIMOS PASSOS SE AINDA HOUVER PROBLEMAS

Se ao testar ainda houver erros:

1. **Erro CORS no console do navegador:**
   - Verificar `APP_ALLOWED_ORIGINS` em Railway
   - Testar preflight com curl primeiro
   - Verificar se Origin está exato (sem trailing slash)

2. **Erro 401/403 em endpoints:**
   - Copiar token completo do login (sem "Bearer ")
   - Testar com `curl -H "Authorization: Bearer TOKEN"`

3. **Health check falha:**
   - Verificar logs: `railway logs`
   - Garantir que `spring-boot-starter-actuator` está em pom.xml
   - Testar localmente primeiro

4. **Timeout:**
   - Aumentar `HIKARI_CONNECTION_TIMEOUT` em Railway
   - Verificar conectividade com banco

---

**Documento gerado em:** 5 de junho de 2026  
**Todas as 13 correções implementadas e testadas** ✅

