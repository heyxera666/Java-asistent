@echo off
set "JAVA_HOME=%CD%\.java"
set "PATH=%CD%\.java\bin;%CD%\apache-maven-3.9.5\bin;%PATH%"
taskkill /F /IM java.exe >nul 2>&1
echo Starting Personal Assistant...
start /b mvn spring-boot:run
:wait
timeout /t 2 /nobreak >nul
curl -s http://localhost:8081/api/assistant/health >nul 2>&1
if errorlevel 1 goto wait
start http://localhost:8081
mvn spring-boot:run
