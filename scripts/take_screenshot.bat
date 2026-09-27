@echo off
setlocal
cd /d "%~dp0\.."
if not exist screenshots mkdir screenshots

for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set TIMESTAMP=%datetime:~0,8%_%datetime:~8,6%

echo Capturing device screenshot to screenshots/screen_%TIMESTAMP%.png...
call android screen capture -o "screenshots/screen_%TIMESTAMP%.png"

echo Done! Saved to screenshots/screen_%TIMESTAMP%.png
