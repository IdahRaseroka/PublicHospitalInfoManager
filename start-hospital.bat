@echo off
rem double click this file to start the app and open it in the browser

cd /d "%~dp0"
set JAR=target\public-hospital-info-manager-0.0.1-SNAPSHOT.jar

rem build the app the first time
if not exist "%JAR%" (
    echo Building the app for the first time, please wait...
    set MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT
    call mvnw.cmd -q -DskipTests package
)

rem if the app is already running just open the website again
powershell -NoProfile -Command "try { Invoke-WebRequest -UseBasicParsing http://localhost:8080 -TimeoutSec 2 | Out-Null; exit 0 } catch { exit 1 }"
if %errorlevel%==0 (
    start "" http://localhost:8080
    exit /b
)

rem javaw runs the app in the background without a black window
rem the app opens the website by itself when it is ready
start "" javaw -jar "%JAR%"
