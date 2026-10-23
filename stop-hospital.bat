@echo off
rem double click this file to stop the app

powershell -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='javaw.exe'\" | Where-Object { $_.CommandLine -like '*public-hospital-info-manager*' } | ForEach-Object { Stop-Process -Id $_.ProcessId }"
echo Public Hospital Info Manager has stopped.
timeout /t 3 >nul
