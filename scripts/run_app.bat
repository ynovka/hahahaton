@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================================
echo   PET ME! - Сборка и запуск игры
echo ========================================================
cd /d "%~dp0\.."

set "PATH=%LOCALAPPDATA%\Android\Sdk\emulator;%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"

echo [*] Проверка Android устройства...
adb get-state >nul 2>&1
if errorlevel 1 (
    echo [*] Эмулятор не запущен. Автоматически запускаем medium_phone...
    start "" "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" -avd medium_phone -no-snapshot-save
    echo [*] Ожидание подключения устройства...
    adb wait-for-device
    echo [*] Ожидание загрузки Android системы...
:WAIT_BOOT
    ping -n 2 127.0.0.1 >nul
    for /f "tokens=*" %%A in ('adb shell getprop sys.boot_completed 2^>nul') do (
        set "BOOT_STATUS=%%A"
    )
    if not "!BOOT_STATUS!"=="1" (
        goto WAIT_BOOT
    )
    echo [OK] Эмулятор готов!
) else (
    echo [OK] Устройство готово!
)

echo.
echo [*] Сборка debug APK...
call .\gradlew.bat assembleDebug
if errorlevel 1 (
    echo [ОШИБКА] Сборка завершилась с ошибкой!
    pause
    exit /b 1
)

echo [*] Установка APK...
adb install -r "app\build\outputs\apk\debug\app-debug.apk"

echo [*] Разблокировка экрана...
adb shell input keyevent 82 >nul 2>&1
adb shell input keyevent 224 >nul 2>&1

echo [*] Запуск Pet Me!...
adb shell am start -n ltd.kyss.petme/.MainActivity

echo.
echo ========================================================
echo   [OK] Приложение успешно запущено на эмуляторе!
echo ========================================================
ping -n 4 127.0.0.1 >nul
