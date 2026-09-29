# ========================================================
#   🐾 Запуск игры «Питомец Финни» (PowerShell)
# ========================================================

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  🐾 Запуск игры «Питомец Финни» (Web App)" -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "node_modules")) {
    Write-Host "Устанавливаем зависимости npm..." -ForegroundColor Green
    npm install
}

Write-Host "Запускаем сервер игры и открываем браузер..." -ForegroundColor Green
Write-Host "Не открывайте index.html напрямую: используйте http://localhost:3000/" -ForegroundColor Yellow
try {
    Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:3000/" -TimeoutSec 1 | Out-Null
    $serverReady = $true
} catch {
    $serverReady = $false
}

if (-not $serverReady) {
    Start-Process cmd.exe -ArgumentList '/k', 'npm run dev' -WindowStyle Minimized
    Start-Sleep -Seconds 2
}
Start-Process "http://localhost:3000/"
