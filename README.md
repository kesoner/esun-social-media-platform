# 社群媒體平台 - Social Media Platform

> 玉山銀行 E.SUN BANK 後端工程師 - Java 實作題

## 📌 專案概述

此專案為模擬一個具備基本社群媒體功能的平台，藉此展現後端系統設計與開發能力。

### 核心功能模組
- ✅ 使用者註冊與登入
- ✅ 使用者身份驗證
- ✅ 發文功能（新增、列表、編輯、刪除）
- ✅ 留言功能（針對文章新增留言）

## 🏗 系統架構

### 架構設計
採用 **三層式架構**：
```
Web Layer (Controller)
    ↓
Business Layer (Service)
    ↓
Data Access Layer (Repository/DAO)
    ↓
SQLite Database
```

### 分層說明
- **Web Layer**: REST API 控制器，處理 HTTP 請求與回應
- **Business Layer**: 業務邏輯處理，包含驗證與資料轉換
- **Data Access Layer**: 資料存取抽象層，與資料庫互動

## ⚙️ 技術規格

### 後端技術棧
- **框架**: Spring Boot 3.x
- **建構工具**: Maven
- **資料庫**: SQLite 3.x
- **ORM**: Spring Data JPA + Hibernate
- **安全性**: Spring Security + JWT
- **API 風格**: RESTful API
- **資料格式**: JSON

### 前端技術棧
- **框架**: Vue.js 3.x
- **HTTP 客戶端**: Axios
- **UI 框架**: Element Plus / Vuetify

### 安全性要求
- 🔐 使用者密碼採用 **BCrypt** 加密儲存
- 🛡 防禦 **SQL Injection** 攻擊（使用 Prepared Statement）
- 🚫 防禦 **XSS** 攻擊（輸入驗證與輸出編碼）
- 🔑 JWT Token 身份驗證
- 🔒 支援多使用者並發操作，避免資料競爭

## 📂 功能模組詳細說明

### 1. 使用者管理
- **註冊功能**
  - 使用者可透過 Email 與密碼註冊帳號
  - Email 格式驗證與重複檢查
  - 密碼強度驗證（至少 8 字元，包含英數字）

- **登入功能**
  - Email + 密碼登入
  - JWT Token 產生與驗證
  - 登入狀態維持

- **個人資料管理**
  - 使用者名稱設定
  - 個人簡介編輯
  - 封面圖片上傳（選填）

### 2. 發文功能
- **文章管理**
  - ✏️ 新增文章（需登入）
  - 📋 列出所有文章（公開瀏覽）
  - ✏️ 編輯文章（僅作者本人）
  - 🗑 刪除文章（僅作者本人）
  - 🖼 圖片上傳支援（選填）

- **權限控制**
  - 僅登入使用者可發文
  - 僅文章作者可編輯/刪除自己的文章

### 3. 留言功能
- **留言管理**
  - 💬 針對文章新增留言（需登入）
  - 📝 顯示留言列表
  - 🕒 留言時間排序

- **互動功能**
  - 留言者資訊顯示
  - 留言時間戳記

## 🗄 資料庫設計 (SQLite)

### 資料庫選擇說明
- **SQLite**: 輕量級嵌入式資料庫，適合開發與測試環境
- **優點**: 零配置、檔案型資料庫、支援 ACID 事務
- **檔案位置**: `./data/social_media.db`

### 資料表結構

#### 1. Users 使用者表
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    cover_image VARCHAR(255),
    biography TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

| 欄位名稱 | 資料型別 | 說明 | 約束條件 |
|---------|---------|------|---------|
| id | INTEGER | 使用者 ID | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | 使用者名稱 | NOT NULL, UNIQUE |
| email | VARCHAR(100) | 電子郵件 | NOT NULL, UNIQUE |
| password_hash | VARCHAR(255) | 密碼雜湊值 | NOT NULL (BCrypt) |
| cover_image | VARCHAR(255) | 封面圖片路徑 | 可為空 |
| biography | TEXT | 個人簡介 | 可為空 |
| created_at | DATETIME | 建立時間 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新時間 | DEFAULT CURRENT_TIMESTAMP |

#### 2. Posts 發文表
```sql
CREATE TABLE posts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

| 欄位名稱 | 資料型別 | 說明 | 約束條件 |
|---------|---------|------|---------|
| id | INTEGER | 發文 ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | INTEGER | 使用者 ID | NOT NULL, FOREIGN KEY |
| content | TEXT | 文章內容 | NOT NULL |
| image | VARCHAR(255) | 圖片路徑 | 可為空 |
| created_at | DATETIME | 發佈時間 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新時間 | DEFAULT CURRENT_TIMESTAMP |

#### 3. Comments 留言表
```sql
CREATE TABLE comments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    post_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
```

| 欄位名稱 | 資料型別 | 說明 | 約束條件 |
|---------|---------|------|---------|
| id | INTEGER | 留言 ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | INTEGER | 使用者 ID | NOT NULL, FOREIGN KEY |
| post_id | INTEGER | 文章 ID | NOT NULL, FOREIGN KEY |
| content | TEXT | 留言內容 | NOT NULL |
| created_at | DATETIME | 留言時間 | DEFAULT CURRENT_TIMESTAMP |

### 索引設計
```sql
-- 提升查詢效能的索引
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_users_email ON users(email);
```
## 🚀 API 設計規範

### RESTful API 端點

#### 使用者相關 API
```
POST   /api/auth/register     # 使用者註冊
POST   /api/auth/login        # 使用者登入
GET    /api/auth/profile      # 取得個人資料 (需驗證)
PUT    /api/auth/profile      # 更新個人資料 (需驗證)
POST   /api/auth/logout       # 登出 (需驗證)
```

#### 發文相關 API
```
GET    /api/posts             # 取得所有文章列表
GET    /api/posts/{id}        # 取得特定文章
POST   /api/posts             # 新增文章 (需驗證)
PUT    /api/posts/{id}        # 更新文章 (需驗證 + 作者權限)
DELETE /api/posts/{id}        # 刪除文章 (需驗證 + 作者權限)
```

#### 留言相關 API
```
GET    /api/posts/{id}/comments    # 取得文章留言列表
POST   /api/posts/{id}/comments    # 新增留言 (需驗證)
DELETE /api/comments/{id}          # 刪除留言 (需驗證 + 作者權限)
```

### HTTP 狀態碼規範
- `200 OK`: 請求成功
- `201 Created`: 資源建立成功
- `400 Bad Request`: 請求參數錯誤
- `401 Unauthorized`: 未授權（需要登入）
- `403 Forbidden`: 禁止存取（權限不足）
- `404 Not Found`: 資源不存在
- `500 Internal Server Error`: 伺服器內部錯誤

## 🛠 開發環境設置

### 必要軟體
- **Java**: JDK 17 或以上版本
- **Maven**: 3.8+ 或 **Gradle**: 7.0+
- **Node.js**: 16+ (前端開發)
- **Git**: 版本控制

### 後端啟動步驟
```bash
# 1. 複製專案
git clone <repository-url>
cd social-media-platform

# 2. 安裝相依套件
mvn clean install

# 3. 啟動應用程式
mvn spring-boot:run

# 4. 存取應用程式
# API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### 前端啟動步驟
```bash
# 1. 進入前端目錄
cd frontend

# 2. 安裝相依套件
npm install

# 3. 啟動開發伺服器
npm run dev

# 4. 存取前端應用
# http://localhost:3000
```

## 🧪 測試策略

### 測試類型
- **單元測試**: JUnit 5 + Mockito
- **整合測試**: Spring Boot Test + TestContainers
- **API 測試**: MockMvc + RestAssured
- **前端測試**: Jest + Vue Test Utils

### 測試執行
```bash
# 執行所有測試
mvn test

# 執行特定測試類別
mvn test -Dtest=UserServiceTest

# 產生測試覆蓋率報告
mvn jacoco:report
```

## 📁 專案結構
```
social-media-platform/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/esun/socialmedia/
│   │   │       ├── SocialMediaApplication.java
│   │   │       ├── config/          # 配置類別
│   │   │       ├── controller/      # REST 控制器
│   │   │       ├── service/         # 業務邏輯層
│   │   │       ├── repository/      # 資料存取層
│   │   │       ├── entity/          # JPA 實體類別
│   │   │       ├── dto/             # 資料傳輸物件
│   │   │       ├── security/        # 安全性配置
│   │   │       └── exception/       # 例外處理
│   │   └── resources/
│   │       ├── application.yml      # 應用程式配置
│   │       ├── data.sql            # 初始資料
│   │       └── schema.sql          # 資料庫結構
│   └── test/                       # 測試程式碼
├── frontend/                       # Vue.js 前端
├── data/                          # SQLite 資料庫檔案
├── docs/                          # 專案文件
├── pom.xml                        # Maven 配置
└── README.md                      # 專案說明
```

## 🚀 專案提交與驗收

### 提交要求
1. **GitHub 專案**: 請建立 GitHub Repository 並上傳完整程式碼
2. **README 文件**: 包含專案說明、安裝步驟、API 文件
3. **程式碼品質**: 遵循 Java 編碼規範，包含適當註解
4. **測試覆蓋**: 重要功能需有對應的單元測試
5. **可執行性**: 確保專案可正確編譯與執行

### 驗收標準
- ✅ 功能完整性：所有需求功能正常運作
- ✅ 安全性：密碼加密、SQL Injection 防護、XSS 防護
- ✅ 效能：支援多使用者並發操作
- ✅ 程式碼品質：結構清晰、註解完整
- ✅ 測試完整性：關鍵功能有測試覆蓋

### 提交方式
請將 **GitHub 專案連結** 作為最終成果提交，確保：
- Repository 為 Public 或提供存取權限
- 包含完整的專案程式碼與文件
- README 說明清楚，可依照步驟執行專案

## 🚀 快速啟動指南

### 一鍵啟動（推薦）

**Windows 用戶：**
```bash
# 啟動完整應用程式（前端 + 後端）
start-all.bat

# 或分別啟動
start-backend.bat  # 啟動後端
start-frontend.bat # 啟動前端
```

### Docker 部署

```bash
# 使用 Docker Compose 一鍵部署
docker-compose up -d --build

# 訪問應用程式
# - 前端：http://localhost
# - 後端：http://localhost:8080
```

### 整合測試

```bash
# 運行整合測試腳本
test-integration.bat
```

### 訪問地址

- **前端應用**：http://localhost:3000
- **後端 API**：http://localhost:8080
- **API 文件**：http://localhost:8080/swagger-ui.html
- **健康檢查**：http://localhost:8080/actuator/health

## 📚 詳細文件

- **[部署指南](DEPLOYMENT.md)** - 完整的部署說明和配置
- **[API 文件](API_DOCUMENTATION.md)** - 詳細的 API 規格說明
- **[設置指南](SETUP.md)** - 開發環境設置步驟

---

## 📞 聯絡資訊
如有任何問題或需要協助，請透過以下方式聯絡：
- **Email**: dev-team@esunbank.com
- **GitHub Issues**: 在專案 Repository 中建立 Issue

---
*最後更新時間: 2024-12-09*