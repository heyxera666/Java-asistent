@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"

set JAVA_PATH=%CD%\.java
set MAVEN_PATH=%CD%\.maven

echo Checking for Java...
if not exist "%JAVA_PATH%\bin\java.exe" (
    echo Java not found. Downloading JDK 17...
    PowerShell -NoProfile -ExecutionPolicy Bypass -Command ^
        "[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; ^
         $url = 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.10%2B13/OpenJDK17U-jdk_x64_windows_hotspot_17.0.10_13.zip'; ^
         $file = '%CD%\.java.zip'; ^
         (New-Object System.Net.WebClient).DownloadFile($url, $file); ^
         Expand-Archive $file -DestinationPath '%CD%' -Force; ^
         Remove-Item $file -Force; ^
         Get-ChildItem '%CD%\jdk*' -Directory | Select-Object -First 1 | Rename-Item -NewName '.java' -Force"
    if errorlevel 1 (
        echo Failed to download Java. Please install JDK 17 manually.
        pause
        exit /b 1
    )
) else (
    echo Java found at %JAVA_PATH%
)

echo Checking for Maven...
if not exist "%MAVEN_PATH%\bin\mvn.cmd" (
    echo Maven not found. Downloading Maven 3.9.5...
    PowerShell -NoProfile -ExecutionPolicy Bypass -Command ^
        "[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; ^
         $url = 'https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip'; ^
         $file = '%CD%\.maven.zip'; ^
         (New-Object System.Net.WebClient).DownloadFile($url, $file); ^
         Expand-Archive $file -DestinationPath '%CD%' -Force; ^
         Remove-Item $file -Force; ^
         Get-ChildItem '%CD%\apache-maven*' -Directory | Select-Object -First 1 | Rename-Item -NewName '.maven' -Force"
    if errorlevel 1 (
        echo Failed to download Maven. Please install Maven manually.
        pause
        exit /b 1
    )
) else (
    echo Maven found at %MAVEN_PATH%
)

echo.
echo Setting up environment variables...
set PATH=%JAVA_PATH%\bin;%MAVEN_PATH%\bin;%PATH%
set JAVA_HOME=%JAVA_PATH%
set MAVEN_HOME=%MAVEN_PATH%

echo.
echo Verifying installations...
"%JAVA_PATH%\bin\java.exe" -version
"%MAVEN_PATH%\bin\mvn.cmd" -version

echo.
echo Starting Spring Boot application...
echo Application will be available at http://localhost:8080
echo.

"%MAVEN_PATH%\bin\mvn.cmd" spring-boot:run

pause
