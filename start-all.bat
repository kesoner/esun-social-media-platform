@echo off
echo ========================================
echo   玉山銀行社群媒體平台啟動腳本
echo ========================================
echo.

REM Check prerequisites
echo Checking prerequisites...

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 17 or later
    goto :error
)
echo [OK] Java is available

REM Check Node.js
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed or not in PATH
    echo Please install Node.js from https://nodejs.org/
    goto :error
)
echo [OK] Node.js is available

REM Check Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] Maven is not installed or not in PATH
    echo You can install Maven or use your IDE to run the backend
    echo.
    echo For now, only starting the frontend...
    goto :frontend_only
)
echo [OK] Maven is available

echo.
echo All prerequisites are met!
echo.

REM Start backend in a new window
echo Starting backend server...
start "Social Media Backend" cmd /k "start-backend.bat"

REM Wait a moment for backend to start
echo Waiting for backend to initialize...
timeout /t 5 /nobreak >nul

:frontend_only
REM Start frontend in a new window
echo Starting frontend server...
start "Social Media Frontend" cmd /k "start-frontend.bat"

echo.
echo ========================================
echo   Servers are starting...
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo API Docs: http://localhost:8080/swagger-ui.html
echo.
echo Both servers will open in separate windows.
echo Close those windows to stop the servers.
echo.
pause
exit /b 0

:error
echo.
echo ========================================
echo   Setup incomplete
echo ========================================
echo.
echo Please install the missing prerequisites and try again.
echo.
echo Installation guides:
echo - Java: https://adoptium.net/
echo - Node.js: https://nodejs.org/
echo - Maven: https://maven.apache.org/install.html
echo.
pause
exit /b 1
