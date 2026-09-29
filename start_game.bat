@echo off
chcp 65001 > nul
echo ========================================================
echo   🐾 Запуск игры «Питомец Финни» (Web App)
echo ========================================================
echo.
echo Проверяем установку зависимостей...
if not exist node_modules (
    echo Устанавливаем зависимости...
    call npm install
)

echo.
echo Запускаем локальный веб-сервер игры...
echo Важно: не открывайте index.html двойным кликом.
echo Игра откроется по адресу http://localhost:3000/
powershell -NoProfile -Command "try { Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:3000/' -TimeoutSec 1 ^| Out-Null; exit 0 } catch { exit 1 }"
if errorlevel 1 (
    start "Питомец Финни — сервер" /min cmd /k "npm run dev"
    timeout /t 2 /nobreak > nul
) else (
    echo Сервер уже запущен.
)
start "" http://localhost:3000/
echo Игра открыта в браузере. Сервер можно закрыть после игры.
pause
