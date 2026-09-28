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

Write-Host "Открываем браузер и поднимаем сервер..." -ForegroundColor Green
Start-Process "http://localhost:5173/"
npx vite --port 5173 --host
