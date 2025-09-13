@echo off
echo 正在檢查 Java 版本...
java -version

echo.
echo 正在檢查專案結構...
dir src\main\java\com\esun\socialmedia /s /b | findstr "\.java$" | wc -l
echo Java 檔案數量: 

echo.
echo 正在檢查測試檔案...
dir src\test\java\com\esun\socialmedia /s /b | findstr "\.java$" | wc -l
echo 測試檔案數量:

echo.
echo 專案編譯需要 Maven 或 Gradle。
echo 請安裝 Maven 後執行: mvn clean compile test
echo 或使用 IDE (如 IntelliJ IDEA 或 Eclipse) 來編譯和執行測試。

pause
