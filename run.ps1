#!/usr/bin/env pwsh
# Setup and run Personal Assistant

$ErrorActionPreference = "Stop"

$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptPath

$javaPath = Join-Path $scriptPath ".java"
$mavenPath = Join-Path $scriptPath ".maven"
$javaExe = Join-Path $javaPath "bin\java.exe"
$mvnExe = Join-Path $mavenPath "bin\mvn.cmd"

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Personal Assistant - Setup & Run" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

# Setup TLS
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072

# Check and install Java
if (-not (Test-Path $javaExe)) {
    Write-Host "`n[1/3] Downloading Java 17..." -ForegroundColor Yellow
    try {
        $javaZip = Join-Path $scriptPath ".java.zip"
        $url = "https://cdn.azul.com/zulu/bin/zulu17.50.19-ca-jdk17.0.11-win_x64.zip"
        
        Write-Host "Downloading from: Azul Zulu" -ForegroundColor Gray
        $webClient = New-Object System.Net.WebClient
        $webClient.DownloadFile($url, $javaZip)
        
        Write-Host "Extracting..." -ForegroundColor Gray
        Expand-Archive $javaZip -DestinationPath $scriptPath -Force
        Remove-Item $javaZip -Force
        
        # Rename the extracted folder - try multiple patterns
        $folders = @("zulu*", "jdk*", "*jdk*", "*java*")
        foreach ($pattern in $folders) {
            $extracted = Get-ChildItem -Path $scriptPath -Directory -Filter $pattern -ErrorAction SilentlyContinue | Where-Object { $_.Name -ne ".java" } | Select-Object -First 1
            if ($extracted) {
                Write-Host "Found extracted folder: $($extracted.Name)" -ForegroundColor Gray
                Rename-Item -Path $extracted.FullName -NewName ".java" -Force
                break
            }
        }
        
        Write-Host "Java installed successfully" -ForegroundColor Green
    } catch {
        Write-Host "Error downloading Java: $_" -ForegroundColor Red
        Write-Host "Please install JDK 17 manually from: https://adoptium.net/" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "`n[1/3] Java found" -ForegroundColor Green
}

# Check and install Maven
if (-not (Test-Path $mvnExe)) {
    Write-Host "`n[2/3] Downloading Maven 3.9.5..." -ForegroundColor Yellow
    try {
        $mavenZip = Join-Path $scriptPath ".maven.zip"
        $url = "https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip"
        
        Write-Host "Downloading from Apache Archive..." -ForegroundColor Gray
        $webClient = New-Object System.Net.WebClient
        $webClient.DownloadFile($url, $mavenZip)
        
        Write-Host "Extracting..." -ForegroundColor Gray
        Expand-Archive $mavenZip -DestinationPath $scriptPath -Force
        Remove-Item $mavenZip -Force
        
        # Rename the extracted folder
        $extracted = Get-ChildItem -Path $scriptPath -Directory -Filter "apache-maven*" -ErrorAction SilentlyContinue | Where-Object { $_.Name -ne ".maven" } | Select-Object -First 1
        if ($extracted) {
            Write-Host "Found extracted folder: $($extracted.Name)" -ForegroundColor Gray
            Rename-Item -Path $extracted.FullName -NewName ".maven" -Force
        }
        
        Write-Host "Maven installed successfully" -ForegroundColor Green
    } catch {
        Write-Host "Error downloading Maven: $_" -ForegroundColor Red
        Write-Host "Please install Maven manually from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "`n[2/3] Maven found" -ForegroundColor Green
}

# Verify installations
Write-Host "`n[3/3] Verifying installations..." -ForegroundColor Yellow
try {
    & $javaExe -version 2>&1 | Select-Object -First 1
    Write-Host "Java: OK" -ForegroundColor Green
    
    & $mvnExe -version 2>&1 | Select-Object -First 1
    Write-Host "Maven: OK" -ForegroundColor Green
} catch {
    Write-Host "Verification failed: $_" -ForegroundColor Red
    exit 1
}

# Setup environment
$env:JAVA_HOME = $javaPath
$env:MAVEN_HOME = $mavenPath
$env:Path = "$javaPath\bin;$mavenPath\bin;$env:Path"

Write-Host "`n" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Starting Personal Assistant" -ForegroundColor Cyan
Write-Host "Open: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "`n"

# Run Spring Boot
& $mvnExe spring-boot:run
