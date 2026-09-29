$ErrorActionPreference = "Stop"
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "  Сборка Android APK «Питомец Финни» (Capacitor)" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "1. Сборка веб-бандла..." -ForegroundColor Yellow
npm run build

Write-Host "2. Синхронизация ассетов в Android..." -ForegroundColor Yellow
npx cap sync android

Write-Host "3. Компиляция APK через Gradle..." -ForegroundColor Yellow
Push-Location "android"
try {
    .\gradlew.bat assembleDebug
} finally {
    Pop-Location
}

$apkSource = "android\app\build\outputs\apk\debug\app-debug.apk"
$apkDest = "FinnyPet-debug.apk"
if (Test-Path $apkSource) {
    Copy-Item $apkSource -Destination $apkDest -Force
    $sizeMb = [math]::Round((Get-Item $apkDest).Length / 1MB, 2)
    Write-Host ""
    Write-Host "====================================================" -ForegroundColor Green
    Write-Host "  УСПЕХ! APK собран: $apkDest ($sizeMb MB)" -ForegroundColor Green
    Write-Host "====================================================" -ForegroundColor Green
} else {
    Write-Error "Файл APK не найден после сборки!"
}
