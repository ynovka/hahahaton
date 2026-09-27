@echo off
set "PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools;%PATH%"
echo Streaming logs for Pet Me! (Press Ctrl+C to stop)...
adb logcat -v color *:W ltd.kyss.petme:V AndroidRuntime:E
