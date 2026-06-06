#!/usr/bin/env pwsh
# Script de Testes - EverRise Backend
# Data: 5 de junho de 2026
# Uso: .\test-backend.ps1

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  TESTES - EverRise Backend" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$BackendUrl = "http://localhost:8080"
$FrontendOrigin = "http://localhost:5173"

# Cores
$Success = "Green"
$Error = "Red"
$Info = "Yellow"

function Test-Health {
    Write-Host "`n[1/7] Testando Health Check - GET /" -ForegroundColor $Info
    try {
        $response = Invoke-WebRequest -Uri "$BackendUrl/" -Method GET -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS - Status 200" -ForegroundColor $Success
            Write-Host "Response: $($response.Content)" -ForegroundColor $Success
        }
    } catch {
        Write-Host "❌ FAILED" -ForegroundColor $Error
        Write-Host "Error: $_" -ForegroundColor $Error
    }
}

function Test-Actuator {
    Write-Host "`n[2/7] Testando Actuator - GET /actuator/health" -ForegroundColor $Info
    try {
        $response = Invoke-WebRequest -Uri "$BackendUrl/actuator/health" -Method GET -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS - Status 200" -ForegroundColor $Success
            $content = $response.Content | ConvertFrom-Json
            Write-Host "Response: $($content | ConvertTo-Json)" -ForegroundColor $Success
        }
    } catch {
        Write-Host "❌ FAILED" -ForegroundColor $Error
        Write-Host "Error: $_" -ForegroundColor $Error
    }
}

function Test-Swagger {
    Write-Host "`n[3/7] Testando Swagger - GET /swagger-ui.html" -ForegroundColor $Info
    try {
        $response = Invoke-WebRequest -Uri "$BackendUrl/swagger-ui.html" -Method GET -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS - Status 200" -ForegroundColor $Success
            Write-Host "Acesse: $BackendUrl/swagger-ui.html" -ForegroundColor $Success
        }
    } catch {
        Write-Host "❌ FAILED" -ForegroundColor $Error
        Write-Host "Error: $_" -ForegroundColor $Error
    }
}

function Test-CORSPreflight {
    Write-Host "`n[4/7] Testando CORS Preflight - OPTIONS /auth/login" -ForegroundColor $Info
    try {
        $headers = @{
            "Origin" = $FrontendOrigin
            "Access-Control-Request-Method" = "POST"
            "Access-Control-Request-Headers" = "Content-Type"
        }

        $response = Invoke-WebRequest -Uri "$BackendUrl/auth/login" `
            -Method OPTIONS `
            -Headers $headers `
            -TimeoutSec 5 `
            -SkipHttpErrorCheck

        if ($response.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS - Status 200" -ForegroundColor $Success
            Write-Host "CORS Headers:" -ForegroundColor $Success
            Write-Host "  Allow-Origin: $($response.Headers['Access-Control-Allow-Origin'])" -ForegroundColor $Success
            Write-Host "  Allow-Methods: $($response.Headers['Access-Control-Allow-Methods'])" -ForegroundColor $Success
            Write-Host "  Allow-Headers: $($response.Headers['Access-Control-Allow-Headers'])" -ForegroundColor $Success
        } else {
            Write-Host "⚠️  Status $($response.StatusCode)" -ForegroundColor $Info
        }
    } catch {
        Write-Host "❌ FAILED" -ForegroundColor $Error
        Write-Host "Error: $_" -ForegroundColor $Error
    }
}

function Test-Login {
    Write-Host "`n[5/7] Testando Login - POST /auth/login" -ForegroundColor $Info
    Write-Host "(Digite email e senha ou deixe vazio para pular)" -ForegroundColor $Info

    $email = Read-Host "Email"
    if (-not $email) {
        Write-Host "⏭️  Teste pulado" -ForegroundColor $Info
        return
    }

    $senha = Read-Host "Senha" -AsSecureString
    $senhaPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToCoTaskMemUnicode($senha))

    try {
        $body = @{
            email = $email
            senha = $senhaPlain
        } | ConvertTo-Json

        $headers = @{
            "Content-Type" = "application/json"
            "Origin" = $FrontendOrigin
        }

        $response = Invoke-WebRequest -Uri "$BackendUrl/auth/login" `
            -Method POST `
            -Headers $headers `
            -Body $body `
            -TimeoutSec 5 `
            -SkipHttpErrorCheck

        if ($response.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS - Status 200" -ForegroundColor $Success
            $content = $response.Content | ConvertFrom-Json
            $token = $content.token
            Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor $Success

            # Salvar token para próximo teste
            $global:jwt_token = $token
        } else {
            Write-Host "❌ FAILED - Status $($response.StatusCode)" -ForegroundColor $Error
            Write-Host "Response: $($response.Content)" -ForegroundColor $Error
        }
    } catch {
        Write-Host "❌ FAILED" -ForegroundColor $Error
        Write-Host "Error: $_" -ForegroundColor $Error
    }
}

function Test-ProtectedEndpoint {
    Write-Host "`n[6/7] Testando Endpoint Protegido - GET /equipamentos" -ForegroundColor $Info

    if (-not $global:jwt_token) {
        Write-Host "⏭️  Teste pulado (nenhum token disponível)" -ForegroundColor $Info
        return
    }

    try {
        $headers = @{
            "Authorization" = "Bearer $($global:jwt_token)"
            "Content-Type" = "application/json"
            "Origin" = $FrontendOrigin
        }

        $response = Invoke-WebRequest -Uri "$BackendUrl/equipamentos" `
            -Method GET `
            -Headers $headers `
            -TimeoutSec 5 `
            -SkipHttpErrorCheck

        if ($response.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS - Status 200" -ForegroundColor $Success
            $content = $response.Content | ConvertFrom-Json
            Write-Host "Equipamentos encontrados: $($content.Count)" -ForegroundColor $Success
        } elseif ($response.StatusCode -eq 403) {
            Write-Host "❌ FORBIDDEN (403) - Sem permissão para este endpoint" -ForegroundColor $Error
        } else {
            Write-Host "⚠️  Status $($response.StatusCode)" -ForegroundColor $Info
        }
    } catch {
        Write-Host "❌ FAILED" -ForegroundColor $Error
        Write-Host "Error: $_" -ForegroundColor $Error
    }
}

function Test-Docker {
    Write-Host "`n[7/7] Verificando Docker Containers" -ForegroundColor $Info
    try {
        $containers = docker ps --filter "name=everrise" --format "{{.Names}}: {{.Status}}"
        if ($containers) {
            Write-Host "✅ Containers em execução:" -ForegroundColor $Success
            $containers | ForEach-Object { Write-Host "  • $_" -ForegroundColor $Success }
        } else {
            Write-Host "❌ Nenhum container em execução" -ForegroundColor $Error
            Write-Host "Execute: docker-compose up -d" -ForegroundColor $Info
        }
    } catch {
        Write-Host "❌ Docker não disponível" -ForegroundColor $Error
        Write-Host "Instale Docker Desktop primeiro" -ForegroundColor $Error
    }
}

# Menu Principal
Write-Host "`nEscolha uma opção:" -ForegroundColor Cyan
Write-Host "  1 - Executar todos os testes"
Write-Host "  2 - Health check"
Write-Host "  3 - Actuator"
Write-Host "  4 - Swagger"
Write-Host "  5 - CORS Preflight"
Write-Host "  6 - Login"
Write-Host "  7 - Endpoint Protegido"
Write-Host "  8 - Docker Status"
Write-Host "  0 - Sair"
Write-Host ""

$choice = Read-Host "Digite sua escolha"

switch ($choice) {
    "1" {
        Write-Host "`n🧪 Executando todos os testes..." -ForegroundColor Cyan
        Test-Docker
        Test-Health
        Test-Actuator
        Test-Swagger
        Test-CORSPreflight
        Test-Login
        Test-ProtectedEndpoint
        Write-Host "`n✅ Testes completos!" -ForegroundColor $Success
    }
    "2" { Test-Health }
    "3" { Test-Actuator }
    "4" { Test-Swagger }
    "5" { Test-CORSPreflight }
    "6" { Test-Login }
    "7" { Test-ProtectedEndpoint }
    "8" { Test-Docker }
    "0" { return }
    default { Write-Host "Opção inválida" -ForegroundColor $Error }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Fim dos testes" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

