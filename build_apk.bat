@echo off
setlocal
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
set "Path=%JAVA_HOME%\bin;%Path%"

echo [1/3] Building web bundle...
call npm run build
if %errorlevel% neq 0 exit /b %errorlevel%

echo [2/3] Syncing Capacitor Android assets...
call npx cap sync android
if %errorlevel% neq 0 exit /b %errorlevel%

echo [3/3] Compiling APK with Gradle...
cd android
call .\gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    cd ..
    exit /b %errorlevel%
)
cd ..

copy /y "android\app\build\outputs\apk\debug\app-debug.apk" "FinnyPet-debug.apk" > nul
echo.
echo SUCCESS! APK built: FinnyPet-debug.apk
