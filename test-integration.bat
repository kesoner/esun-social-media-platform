@echo off
echo ========================================
echo   整合測試腳本
echo ========================================
echo.

REM 設定測試變數
set BACKEND_URL=http://localhost:8080
set FRONTEND_URL=http://localhost:3000
set TEST_USERNAME=testuser_%RANDOM%
set TEST_EMAIL=test%RANDOM%@example.com
set TEST_PASSWORD=Test123456

echo 開始整合測試...
echo.

REM 測試後端健康檢查
echo [1/6] 測試後端健康檢查...
powershell -Command "try { $response = Invoke-WebRequest -Uri '%BACKEND_URL%/actuator/health' -Method GET -TimeoutSec 10; if ($response.StatusCode -eq 200) { Write-Host '[OK] 後端服務正常運行' -ForegroundColor Green } else { Write-Host '[FAIL] 後端健康檢查失敗' -ForegroundColor Red } } catch { Write-Host '[FAIL] 無法連接到後端服務' -ForegroundColor Red }"

REM 測試前端可訪問性
echo [2/6] 測試前端可訪問性...
powershell -Command "try { $response = Invoke-WebRequest -Uri '%FRONTEND_URL%' -Method GET -TimeoutSec 10; if ($response.StatusCode -eq 200) { Write-Host '[OK] 前端服務正常運行' -ForegroundColor Green } else { Write-Host '[FAIL] 前端服務異常' -ForegroundColor Red } } catch { Write-Host '[FAIL] 無法連接到前端服務' -ForegroundColor Red }"

REM 測試 API 端點
echo [3/6] 測試 API 端點...
powershell -Command "try { $response = Invoke-WebRequest -Uri '%BACKEND_URL%/api/auth/check-username?username=nonexistentuser' -Method GET -TimeoutSec 10; if ($response.StatusCode -eq 200) { Write-Host '[OK] API 端點正常' -ForegroundColor Green } else { Write-Host '[FAIL] API 端點異常' -ForegroundColor Red } } catch { Write-Host '[FAIL] API 端點無法訪問' -ForegroundColor Red }"

REM 測試使用者註冊
echo [4/6] 測試使用者註冊...
powershell -Command "$body = @{ username='%TEST_USERNAME%'; email='%TEST_EMAIL%'; password='%TEST_PASSWORD%'; biography='Test user' } | ConvertTo-Json; try { $response = Invoke-WebRequest -Uri '%BACKEND_URL%/api/auth/register' -Method POST -Body $body -ContentType 'application/json' -TimeoutSec 10; if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 201) { Write-Host '[OK] 使用者註冊成功' -ForegroundColor Green } else { Write-Host '[FAIL] 使用者註冊失敗' -ForegroundColor Red } } catch { Write-Host '[FAIL] 註冊請求失敗' -ForegroundColor Red }"

REM 測試使用者登入
echo [5/6] 測試使用者登入...
powershell -Command "$body = @{ usernameOrEmail='%TEST_USERNAME%'; password='%TEST_PASSWORD%' } | ConvertTo-Json; try { $response = Invoke-WebRequest -Uri '%BACKEND_URL%/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -TimeoutSec 10; if ($response.StatusCode -eq 200) { Write-Host '[OK] 使用者登入成功' -ForegroundColor Green; $global:token = ($response.Content | ConvertFrom-Json).accessToken } else { Write-Host '[FAIL] 使用者登入失敗' -ForegroundColor Red } } catch { Write-Host '[FAIL] 登入請求失敗' -ForegroundColor Red }"

REM 測試 Swagger 文件
echo [6/6] 測試 API 文件...
powershell -Command "try { $response = Invoke-WebRequest -Uri '%BACKEND_URL%/swagger-ui.html' -Method GET -TimeoutSec 10; if ($response.StatusCode -eq 200) { Write-Host '[OK] API 文件可訪問' -ForegroundColor Green } else { Write-Host '[FAIL] API 文件無法訪問' -ForegroundColor Red } } catch { Write-Host '[FAIL] API 文件請求失敗' -ForegroundColor Red }"

echo.
echo ========================================
echo   測試完成
echo ========================================
echo.
echo 如果所有測試都通過，表示系統整合正常。
echo 如果有測試失敗，請檢查相應的服務是否正在運行。
echo.
echo 訪問地址：
echo - 前端應用：%FRONTEND_URL%
echo - 後端 API：%BACKEND_URL%
echo - API 文件：%BACKEND_URL%/swagger-ui.html
echo.
pause
