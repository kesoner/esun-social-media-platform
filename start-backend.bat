@echo off
echo Starting Social Media Backend...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or later and add it to your PATH
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    echo.
    echo Alternative: You can download Maven from https://maven.apache.org/download.cgi
    echo Or use your IDE to run the Spring Boot application
    pause
    exit /b 1
)

echo Java and Maven are available
echo.

REM Compile and run the application
echo Compiling the application...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo Error: Compilation failed
    pause
    exit /b 1
)

echo Starting Spring Boot application...
echo Backend will be available at: http://localhost:8080
echo API documentation: http://localhost:8080/swagger-ui.html
echo.
echo Press Ctrl+C to stop the server
echo.

mvn spring-boot:run
