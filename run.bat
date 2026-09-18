@echo off
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo JDK 17 or newer is required. Please install a JDK and try again.
  pause
  exit /b 1
)
if not exist out mkdir out
javac -d out src\*.java
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)
java -cp out StayNestApp
if errorlevel 1 pause
