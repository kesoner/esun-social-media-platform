# 玉山銀行社群媒體平台 API 文件

## 📋 概述

這是一個基於 Spring Boot 開發的社群媒體平台後端服務，提供完整的使用者管理、發文和留言功能。

### 🔧 技術棧
- **框架**: Spring Boot 3.2.0
- **安全性**: Spring Security 6.x + JWT
- **資料庫**: SQLite + JPA/Hibernate
- **API 文件**: OpenAPI 3.0 (Swagger)
- **測試**: JUnit 5 + Mockito

### 🌐 API 基礎資訊
- **Base URL**: `http://localhost:8080`
- **API 版本**: v1.0.0
- **認證方式**: Bearer Token (JWT)

---

## 🔐 認證 API

### 使用者註冊
```http
POST /auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "biography": "string"
}
```

**回應**:
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "string",
    "email": "string",
    "biography": "string",
    "postCount": 0,
    "commentCount": 0,
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 使用者登入
```http
POST /auth/login
Content-Type: application/json

{
  "usernameOrEmail": "string",
  "password": "string"
}
```

### 獲取個人資料
```http
GET /auth/profile
Authorization: Bearer {token}
```

### 更新個人資料
```http
PUT /auth/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "biography": "string"
}
```

### 檢查使用者名稱可用性
```http
GET /auth/check-username?username=string
```

### 檢查電子郵件可用性
```http
GET /auth/check-email?email=string
```

### 刷新 Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "string"
}
```

---

## 📝 發文 API

### 建立發文
```http
POST /posts
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "string",
  "image": "string (optional)"
}
```

### 獲取所有發文
```http
GET /posts?page=0&size=10
```

### 根據 ID 獲取發文
```http
GET /posts/{id}
```

### 更新發文
```http
PUT /posts/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "string",
  "image": "string (optional)"
}
```

### 刪除發文
```http
DELETE /posts/{id}
Authorization: Bearer {token}
```

### 根據作者獲取發文
```http
GET /posts/author/{authorId}?page=0&size=10
```

### 搜尋發文
```http
GET /posts/search?keyword=string&page=0&size=10
```

### 獲取熱門發文
```http
GET /posts/popular?page=0&size=10
```

### 獲取最新發文
```http
GET /posts/latest?limit=5
```

---

## 💬 留言 API

### 建立留言
```http
POST /comments/posts/{postId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "string"
}
```

### 根據 ID 獲取留言
```http
GET /comments/{id}
```

### 根據發文 ID 獲取留言
```http
GET /comments/posts/{postId}
```

### 根據發文 ID 獲取留言（分頁）
```http
GET /comments/posts/{postId}/page?page=0&size=20
```

### 根據作者獲取留言
```http
GET /comments/author/{authorId}?page=0&size=10
```

### 刪除留言
```http
DELETE /comments/{id}
Authorization: Bearer {token}
```

### 搜尋留言
```http
GET /comments/search?keyword=string&page=0&size=10
```

### 獲取最新留言
```http
GET /comments/latest?limit=10
```

### 統計發文留言數
```http
GET /comments/posts/{postId}/count
```

---

## 👥 使用者 API

### 根據 ID 獲取使用者
```http
GET /users/{id}
```

### 根據使用者名稱獲取使用者
```http
GET /users/username/{username}
```

### 搜尋使用者
```http
GET /users/search?keyword=string
```

---

## 📊 HTTP 狀態碼

| 狀態碼 | 說明 |
|--------|------|
| 200 | 請求成功 |
| 201 | 資源建立成功 |
| 400 | 請求參數錯誤 |
| 401 | 未授權 |
| 403 | 沒有權限 |
| 404 | 資源不存在 |
| 409 | 資源衝突 |
| 500 | 伺服器內部錯誤 |

---

## 🔒 認證說明

1. **註冊或登入**後會獲得 `accessToken` 和 `refreshToken`
2. 在需要認證的 API 請求中，在 Header 加入：
   ```
   Authorization: Bearer {accessToken}
   ```
3. `accessToken` 過期時，使用 `refreshToken` 獲取新的 Token
4. Token 過期時間：
   - Access Token: 1 小時
   - Refresh Token: 7 天

---

## 🧪 測試說明

### 單元測試
- **UserServiceTest**: 使用者服務測試
- **PostServiceTest**: 發文服務測試
- **CommentServiceTest**: 留言服務測試
- **AuthServiceTest**: 認證服務測試
- **JwtUtilTest**: JWT 工具測試

### 整合測試
- **AuthControllerIntegrationTest**: 認證控制器整合測試
- **PostControllerIntegrationTest**: 發文控制器整合測試

### 執行測試
```bash
# 使用 Maven
mvn test

# 使用 IDE
# 在 IntelliJ IDEA 或 Eclipse 中右鍵點擊測試類別或方法執行
```

---

## 📝 Swagger UI

啟動應用程式後，可以透過以下網址存取 API 文件：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## 🚀 快速開始

1. **啟動應用程式**
   ```bash
   mvn spring-boot:run
   ```

2. **註冊新使用者**
   ```bash
   curl -X POST http://localhost:8080/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "username": "testuser",
       "email": "test@example.com",
       "password": "password123",
       "biography": "測試使用者"
     }'
   ```

3. **使用返回的 Token 建立發文**
   ```bash
   curl -X POST http://localhost:8080/posts \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer {your-token}" \
     -d '{
       "content": "我的第一篇發文！"
     }'
   ```

---

## 📞 聯絡資訊

- **開發團隊**: dev-team@esun.com
- **專案網站**: https://www.esunbank.com.tw
- **授權**: MIT License
