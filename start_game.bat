@echo off
setlocal
echo ========================================================
echo   PET ME! - Запуск эмулятора и игры
echo ========================================================
cd /d "%~dp0"

set "PATH=%LOCALAPPDATA%\Android\Sdk\emulator;%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"

echo [*] Проверка запущенных эмуляторов...
adb get-state >nul 2>&1
if errorlevel 1 (
    echo [*] Запуск окна эмулятора...
    start "" "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" -avd medium_phone
    echo [*] Ожидание загрузки Android системы...
    adb wait-for-device
    :WAIT_BOOT
    for /f "tokens=*" %%A in ('adb shell getprop sys.boot_completed 2^>nul') do (
        if "%%A"=="1" goto BOOT_DONE
    )
    timeout /t 2 /nobreak >nul
    goto WAIT_BOOT
    :BOOT_DONE
    echo [OK] Эмулятор готов!
) else (
    echo [OK] Эмулятор уже подключен!
)

echo [*] Установка игры...
adb install -r "app\build\outputs\apk\debug\app-debug.apk"

echo [*] Запуск Pet Me!...
adb shell am start -n ltd.kyss.petme/.MainActivity

echo.
echo ========================================================
echo   Игра запущена! Приятного тестирования!
echo ========================================================
timeout /t 5
