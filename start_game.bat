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
start "" http://localhost:5173/
call npx vite --port 5173 --host
pause
