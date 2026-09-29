$ErrorActionPreference = "Stop"
Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "  Сборка релизного Android APK «Питомец Финни»" -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Write-Host "1. Сборка веб-бандла..." -ForegroundColor Yellow
npm run build

Write-Host "2. Синхронизация ассетов в Android..." -ForegroundColor Yellow
npx cap sync android

Write-Host "3. Компиляция релизного APK через Gradle..." -ForegroundColor Yellow
Push-Location "android"
try {
    .\gradlew.bat assembleRelease
} finally {
    Pop-Location
}

$apkSource = "android\app\build\outputs\apk\release\app-release.apk"
$apkDest = "app.apk"
if (Test-Path $apkSource) {
    Copy-Item $apkSource -Destination $apkDest -Force
    $sizeMb = [math]::Round((Get-Item $apkDest).Length / 1MB, 2)
    Write-Host ""
    Write-Host "====================================================" -ForegroundColor Green
    Write-Host "  УСПЕХ! Релизный APK собран: $apkDest ($sizeMb MB)" -ForegroundColor Green
    Write-Host "====================================================" -ForegroundColor Green
} else {
    Write-Error "Файл APK не найден после сборки!"
}
