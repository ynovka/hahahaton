@echo off
chcp 65001 >nul
setlocal
echo ========================================================
echo   Запуск Android Emulator (medium_phone)...
echo ========================================================
cd /d "%~dp0\.."

set "PATH=%LOCALAPPDATA%\Android\Sdk\emulator;%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"

start "" "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" -avd medium_phone -no-snapshot-save
echo [OK] Окно эмулятора запущено!
ping -n 3 127.0.0.1 >nul
