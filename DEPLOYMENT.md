# 部署指南

## 📋 目錄

- [本地開發環境](#本地開發環境)
- [Docker 部署](#docker-部署)
- [生產環境部署](#生產環境部署)
- [環境變數配置](#環境變數配置)
- [監控與維護](#監控與維護)

## 🚀 本地開發環境

### 前置需求

- Java 17 或更高版本
- Node.js 16 或更高版本
- Maven 3.6 或更高版本

### 快速啟動

1. **使用啟動腳本（推薦）**
   ```bash
   # Windows
   start-all.bat
   
   # 或分別啟動
   start-backend.bat
   start-frontend.bat
   ```

2. **手動啟動**
   ```bash
   # 啟動後端
   mvn spring-boot:run
   
   # 啟動前端（新終端）
   cd frontend
   npm install
   npm run dev
   ```

### 訪問地址

- 前端應用：http://localhost:3000
- 後端 API：http://localhost:8080
- API 文件：http://localhost:8080/swagger-ui.html

## 🐳 Docker 部署

### 前置需求

- Docker 20.10 或更高版本
- Docker Compose 2.0 或更高版本

### 部署步驟

1. **建置並啟動所有服務**
   ```bash
   docker-compose up -d --build
   ```

2. **查看服務狀態**
   ```bash
   docker-compose ps
   ```

3. **查看日誌**
   ```bash
   # 查看所有服務日誌
   docker-compose logs -f
   
   # 查看特定服務日誌
   docker-compose logs -f backend
   docker-compose logs -f frontend
   ```

4. **停止服務**
   ```bash
   docker-compose down
   ```

### 訪問地址

- 應用程式：http://localhost
- 後端 API：http://localhost:8080
- API 文件：http://localhost:8080/swagger-ui.html

## 🌐 生產環境部署

### 雲端平台部署

#### AWS 部署

1. **使用 AWS ECS**
   - 上傳 Docker 映像到 ECR
   - 建立 ECS 任務定義
   - 配置 Application Load Balancer
   - 設定 Auto Scaling

2. **使用 AWS Elastic Beanstalk**
   - 打包應用程式為 JAR 檔案
   - 上傳到 Elastic Beanstalk
   - 配置環境變數

#### Google Cloud Platform

1. **使用 Cloud Run**
   ```bash
   # 建置並推送映像
   gcloud builds submit --tag gcr.io/PROJECT_ID/social-media-backend
   gcloud builds submit --tag gcr.io/PROJECT_ID/social-media-frontend
   
   # 部署服務
   gcloud run deploy social-media-backend --image gcr.io/PROJECT_ID/social-media-backend
   gcloud run deploy social-media-frontend --image gcr.io/PROJECT_ID/social-media-frontend
   ```

#### Azure 部署

1. **使用 Azure Container Instances**
   ```bash
   # 建立資源群組
   az group create --name social-media-rg --location eastus
   
   # 部署容器
   az container create --resource-group social-media-rg --file docker-compose.yml
   ```

### 傳統伺服器部署

1. **準備伺服器環境**
   - Ubuntu 20.04 LTS 或 CentOS 8
   - Java 17
   - Nginx
   - 防火牆配置

2. **部署後端**
   ```bash
   # 建置 JAR 檔案
   mvn clean package -DskipTests
   
   # 複製到伺服器
   scp target/social-media-0.0.1-SNAPSHOT.jar user@server:/opt/social-media/
   
   # 建立 systemd 服務
   sudo systemctl enable social-media
   sudo systemctl start social-media
   ```

3. **部署前端**
   ```bash
   # 建置前端
   cd frontend
   npm run build
   
   # 複製到 Nginx 目錄
   sudo cp -r dist/* /var/www/html/
   ```

## ⚙️ 環境變數配置

### 後端環境變數

| 變數名稱 | 預設值 | 說明 |
|---------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Spring 設定檔 |
| `DATABASE_PATH` | `./data/social_media.db` | SQLite 資料庫路徑 |
| `JWT_SECRET` | `your-secret-key` | JWT 簽名密鑰 |
| `JWT_EXPIRATION` | `86400000` | JWT 過期時間（毫秒） |
| `JWT_REFRESH_EXPIRATION` | `604800000` | 刷新 Token 過期時間 |

### 前端環境變數

| 變數名稱 | 預設值 | 說明 |
|---------|--------|------|
| `VITE_API_BASE_URL` | `/api` | API 基礎 URL |
| `VITE_APP_TITLE` | `玉山社群` | 應用程式標題 |

### Docker 環境變數設定

建立 `.env` 檔案：

```env
# 資料庫配置
DATABASE_PATH=/app/data/social_media.db

# JWT 配置
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-environment
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# 其他配置
SPRING_PROFILES_ACTIVE=prod
```

## 📊 監控與維護

### 健康檢查

- 後端健康檢查：`GET /actuator/health`
- 應用程式指標：`GET /actuator/metrics`

### 日誌管理

1. **應用程式日誌**
   ```bash
   # Docker 環境
   docker-compose logs -f backend
   
   # 傳統部署
   tail -f /app/logs/social-media.log
   ```

2. **Nginx 日誌**
   ```bash
   tail -f /var/log/nginx/access.log
   tail -f /var/log/nginx/error.log
   ```

### 資料庫備份

```bash
# SQLite 備份
sqlite3 /app/data/social_media.db ".backup /backup/social_media_$(date +%Y%m%d_%H%M%S).db"

# 定期備份腳本
0 2 * * * /usr/local/bin/backup-database.sh
```

### 效能監控

1. **使用 Prometheus + Grafana**
   - 配置 Micrometer 指標
   - 設定 Grafana 儀表板
   - 監控 JVM、HTTP 請求、資料庫連接

2. **使用 APM 工具**
   - New Relic
   - Datadog
   - Elastic APM

## 🔒 安全性考量

### 生產環境安全檢查清單

- [ ] 更改預設的 JWT 密鑰
- [ ] 啟用 HTTPS
- [ ] 配置防火牆規則
- [ ] 設定 CORS 白名單
- [ ] 啟用 SQL 注入防護
- [ ] 配置 Rate Limiting
- [ ] 定期更新依賴套件
- [ ] 設定日誌監控和告警

### SSL/TLS 配置

```nginx
server {
    listen 443 ssl http2;
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    
    # SSL 安全配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;
}
```

## 🆘 故障排除

### 常見問題

1. **後端無法啟動**
   - 檢查 Java 版本
   - 確認埠號 8080 未被占用
   - 檢查資料庫檔案權限

2. **前端無法連接後端**
   - 確認後端服務正在運行
   - 檢查 CORS 配置
   - 驗證 API 基礎 URL

3. **Docker 部署問題**
   - 檢查 Docker 版本
   - 確認映像建置成功
   - 查看容器日誌

### 聯絡支援

如有部署問題，請聯絡開發團隊：
- Email: dev-team@esunbank.com
- 內部 Slack: #social-media-support
