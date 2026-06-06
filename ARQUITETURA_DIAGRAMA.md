# 🎨 DIAGRAMA DE ARQUITETURA - ANTES vs DEPOIS

---

## 🔴 ANTES (Não funcionava)

```
┌─────────────────────────────┐
│   React Frontend            │
│   (Vite localhost:5173)     │
└────────────┬────────────────┘
             │
             ├─ OPTIONS /auth/login
             │     ❌ BLOQUEADO (401)
             │
             └─ POST /auth/login
                  ❌ CORS ERROR
                  
                  Browser Console:
                  Access-Control-Allow-Origin: MISSING
                  Access-Control-Allow-Methods: MISSING


┌──────────────────────────────────────┐
│   Spring Boot Backend                │
│   (port 8080)                        │
├──────────────────────────────────────┤
│  CorsConfig.java                     │
│  ├─ Bean 1: corsConfigurationSource()│
│  │   settings: A                     │
│  └─ Bean 2: webMvcConfigurer()       │
│      settings: B (conflita!)         │
│                                      │
│  SecurityConfig.java                 │
│  ├─ ❌ OPTIONS não permitido         │
│  ├─ ❌ "/" sem resposta              │
│  ├─ ❌ /actuator bloqueado           │
│  └─ ❌ allowCredentials=true         │
│                                      │
│  application.yml                     │
│  ├─ ❌ JWT_SECRET hardcoded          │
│  ├─ ❌ Credenciais hardcoded         │
│  └─ ❌ CORS não configurada          │
│                                      │
│  pom.xml                             │
│  └─ ❌ spring-boot-starter-actuator  │
│     não incluído                     │
├──────────────────────────────────────┤
│                                      │
│  Dockerfile                          │
│  └─ ❌ healthcheck: wget (ausente)   │
│     Retorna: failure                 │
│                                      │
└──────────────────────────────────────┘

┌──────────────────────┐
│  Railway Platform    │
│  ❌ Healthcheck falha│
│  ❌ Pod não inicia   │
└──────────────────────┘
```

---

## 🟢 DEPOIS (Tudo funcionando)

```
┌─────────────────────────────────────┐
│   React Frontend                    │
│   (Vite localhost:5173)             │
└────────────┬────────────────────────┘
             │
             ├─ OPTIONS /auth/login
             │     ✅ 200 OK
             │     Access-Control-Allow-Origin: http://localhost:5173
             │     Access-Control-Allow-Methods: GET, POST, PUT, DELETE
             │
             └─ POST /auth/login
                  ✅ 200 OK {"token": "..."}


┌───────────────────────────────────────┐
│   Spring Boot Backend                 │
│   (port 8080)                         │
├───────────────────────────────────────┤
│  CorsConfig.java                      │
│  └─ ✅ Bean único: corsConfigurationSource()
│     settings: CORS completo           │
│     ├─ Origins: localhost:5173, ...   │
│     ├─ Methods: GET, POST, PUT, DELETE│
│     ├─ Headers: Authorization, ...    │
│     └─ allowCredentials: FALSE ✅     │
│                                       │
│  SecurityConfig.java                  │
│  ├─ ✅ OPTIONS permitido              │
│  ├─ ✅ "/" permitido (health check)   │
│  ├─ ✅ /actuator permitido            │
│  └─ ✅ allowCredentials: FALSE        │
│                                       │
│  HealthController.java ✅ NOVO        │
│  └─ GET "/" → {"status":"UP"}         │
│                                       │
│  application.yml                      │
│  ├─ ✅ JWT_SECRET: ${JWT_SECRET}      │
│  ├─ ✅ CORS: ${APP_ALLOWED_ORIGINS}   │
│  └─ ✅ app.cors.allowed-origins       │
│                                       │
│  application-prod.yml ✅ COMPLETO     │
│  ├─ ✅ DB com env vars                │
│  ├─ ✅ Redis configurado              │
│  ├─ ✅ Management endpoints           │
│  └─ ✅ Pool sizes otimizado           │
│                                       │
│  pom.xml                              │
│  └─ ✅ spring-boot-starter-actuator   │
│     GET /actuator/health ✅           │
├───────────────────────────────────────┤
│                                       │
│  Dockerfile                           │
│  ├─ ✅ RUN apk add curl               │
│  └─ ✅ healthcheck: curl --fail       │
│     Retorna: success                  │
│                                       │
└───────────────────────────────────────┘

┌──────────────────────┐
│  Railway Platform    │
│  ✅ Healthcheck OK   │
│  ✅ Pod inicia bem   │
│  ✅ Deploy sucesso   │
└──────────────────────┘
```

---

## 🔄 FLUXO DE REQUISIÇÃO (AGORA FUNCIONA)

### Local Development Flow
```
User Browser
  │
  ├─ Press Login Button
  │  │
  │  ├─ Browser: "Preciso comunicar com localhost:8080"
  │  │
  │  └─ Envia PREFLIGHT:
  │     OPTIONS http://localhost:8080/auth/login
  │     Host: localhost:5173
  │
  └─ Backend Cors Filter
     │
     ├─ Recebe OPTIONS
     │  │
     │  ├─ Verifica CorsConfigurationSource
     │  │  ├─ allowedOrigins = ["localhost:5173", ...]
     │  │  ├─ allowedMethods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
     │  │  └─ allowCredentials = false (JWT stateless!)
     │  │
     │  └─ Responde 200 OK com headers
     │     Access-Control-Allow-Origin: http://localhost:5173 ✅
     │     Access-Control-Allow-Methods: GET, POST, ...✅
     │
     └─ Browser recebe resposta 200
        │
        └─ "Tudo bem, posso fazer POST agora"
           │
           └─ Browser: "Enviando POST /auth/login com credenciais"
              │
              └─ Backend SecurityFilter
                 │
                 ├─ Valida credenciais ✅
                 └─ Responde: {"token": "eyJhbGci..."} ✅
                    │
                    └─ Frontend salva token em localStorage ✅
```

---

## 📊 HEALTH CHECK FLOW (Railway)

### ANTES ❌
```
Railway Platform
  │
  └─ Health Check (a cada 30s)
     │
     ├─ GET http://localhost:8080/
     │  └─ ❌ 404 Not Found
     │     Nenhum controller da resposta
     │
     └─ GET http://localhost:8080/actuator/health
        └─ ❌ 403 Forbidden
           spring-boot-starter-actuator não está no pom.xml
           
Result: ❌ Pod marked as unhealthy
        ❌ Container restarted 3x
        ❌ Deploy FAILS
```

### DEPOIS ✅
```
Railway Platform
  │
  └─ Health Check (a cada 30s)
     │
     ├─ GET http://localhost:8080/
     │  │
     │  └─ ✅ 200 OK {"status":"UP"}
     │     HealthController responde
     │
     ├─ GET http://localhost:8080/actuator/health
     │  │
     │  └─ ✅ 200 OK {"status":"UP","components":{...}}
     │     Actuator está no pom.xml
     │
     └─ GET http://localhost:8080/docker healthcheck
        │
        └─ ✅ curl --fail retorna 0 (sucesso)
           Dockerfile tem curl instalado
           
Result: ✅ Pod marked as healthy
        ✅ Can receive traffic
        ✅ Deploy SUCCESS
```

---

## 🔐 SEGURANÇA AGORA

### JWT Token Flow (Correto para Stateless)

```
Frontend                     Backend
   │                             │
   ├─ POST /auth/login ─────────►│
   │  {email, senha}             │
   │                             │
   │  ◄─────── {token}  ────────┤
   │         200 OK              │
   │                             │
   ├─ GET /equipamentos ────────►│
   │  Header:                    │
   │  Authorization:             │
   │  Bearer {token}             │
   │                             │
   │  ◄────── [...]  ───────────┤
   │        200 OK               │
   │                             │
   └─ (Sem cookies, sem session)
      (JavaScript control)       │
                                 │
     JwtAuthFilter checks:       │
     ├─ Token is valid? ✅       │
     ├─ Token not expired? ✅    │
     └─ Signature OK? ✅         │
```

---

## 🎁 VARIÁVEIS DE AMBIENTE

### Development (LocalHost)
```
app.properties (application-dev.yml):
├─ DB_HOST=localhost
├─ DB_PORT=3306
├─ REDIS_HOST=localhost
├─ JWT_SECRET=dev-secret-key-123
└─ APP_ALLOWED_ORIGINS=localhost:5173,localhost:3000
```

### Production (Railway)
```
Railway Dashboard (Environment Variables):
├─ DB_HOST=seu-mysql.railway.app
├─ DB_PORT=3306 (Railway injeta)
├─ DB_NAME=everrise_prod
├─ DB_USER=seu_usuario
├─ DB_PASSWORD=sua_senha_segura
├─ REDIS_HOST=seu-redis.railway.app
├─ REDIS_PASSWORD=sua_senha_redis
├─ JWT_SECRET=chave-aleatoria-32-caracteres
├─ APP_ALLOWED_ORIGINS=seu-frontend.vercel.app,everrise.com.br
├─ SPRING_PROFILES_ACTIVE=prod
└─ PORT=8080 (Railway injeta automaticamente)
```

---

## 🧪 TESTE FLOW (Local)

```
┌─ docker-compose up -d
│  ├─ MySQL starts ✅
│  ├─ Redis starts ✅
│  └─ Spring Boot starts ✅
│
├─ curl http://localhost:8080/
│  └─ ✅ {"status":"UP"}
│
├─ curl http://localhost:8080/actuator/health
│  └─ ✅ {"status":"UP"}
│
├─ OPTIONS http://localhost:8080/auth/login
│  └─ ✅ 200 OK (CORS preflight)
│
├─ POST http://localhost:8080/auth/login
│  └─ ✅ {"token":"..."}
│
├─ GET http://localhost:8080/equipamentos
│  with: Authorization: Bearer {token}
│  └─ ✅ [...]
│
└─ npm run dev (Frontend)
   ├─ React starts on :5173 ✅
   ├─ Login button works ✅
   ├─ DevTools Console clean ✅
   └─ No CORS errors ✅
```

---

## 📈 ARQUITETURA FINAL

```
                    ┌─────────────────────┐
                    │   User's Browser    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  React (Vite)       │
                    │ :5173 / Vercel      │
                    └──────────┬──────────┘
                               │ HTTP/CORS
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
    LocalHost              Railway              Vercel
    :8080                  SBB App              Frontend
        │                      │                      │
    ┌───▼────┐            ┌────▼────┐          ┌─────▼──┐
    │ Spring  │            │ Spring  │          │ React  │
    │ Boot    │            │ Boot    │          │ Prod   │
    │ DEV     │            │ PROD    │          │        │
    └─┬─┬─┬─┬─┘            └─┬─┬─┬─┬─┘          └────────┘
      │ │ │ │               │ │ │ │
      │ │ │ └─ Actuator ←──┘ │ │ │
      │ │ └─ MySQL  ←────────┘ │ │
      │ └─ Redis   ←───────────┘ │
      └─ Application ←────────────┘

All communication: Secured by JWT ✅
All CORS: Validated in CorsConfig ✅
All Health: Checked by /actuator/health ✅
```

---

**Diagrama atualizado:** 5 de junho de 2026  
**Status:** ✅ Arquitetura pronta para produção

