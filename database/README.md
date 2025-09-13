# 資料庫設計文件

## 📋 概述

本專案使用 **SQLite** 作為資料庫，這是一個輕量級的嵌入式關聯式資料庫，非常適合開發和測試環境。

## 🗂 檔案說明

- `schema.sql`: 資料庫結構定義（表格、索引、觸發器）
- `sample_data.sql`: 範例測試資料
- `social_media.db`: SQLite 資料庫檔案（執行時自動產生）

## 🚀 資料庫初始化

### 方法一：使用 SQLite 命令列工具
```bash
# 1. 安裝 SQLite（如果尚未安裝）
# Windows: 下載 sqlite-tools 並加入 PATH
# macOS: brew install sqlite
# Ubuntu: sudo apt-get install sqlite3

# 2. 建立資料庫並執行結構腳本
sqlite3 social_media.db < schema.sql

# 3. 插入範例資料（選填）
sqlite3 social_media.db < sample_data.sql

# 4. 驗證資料庫
sqlite3 social_media.db ".tables"
```

### 方法二：使用 Spring Boot 自動初始化
在 `application.yml` 中配置：
```yaml
spring:
  datasource:
    url: jdbc:sqlite:./data/social_media.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    hibernate:
      ddl-auto: create-drop  # 開發環境使用
    show-sql: true
  sql:
    init:
      schema-locations: classpath:database/schema.sql
      data-locations: classpath:database/sample_data.sql
      mode: always
```

## 📊 資料表關係圖

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    Users    │       │    Posts    │       │  Comments   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │◄──────┤ user_id (FK)│◄──────┤ user_id (FK)│
│ username    │       │ id (PK)     │◄──────┤ post_id (FK)│
│ email       │       │ content     │       │ id (PK)     │
│ password_hash│       │ image       │       │ content     │
│ cover_image │       │ created_at  │       │ created_at  │
│ biography   │       │ updated_at  │       └─────────────┘
│ created_at  │       └─────────────┘
│ updated_at  │
└─────────────┘
```

## 🔍 常用查詢範例

### 使用者相關查詢
```sql
-- 查詢所有使用者
SELECT id, username, email, biography, created_at FROM users;

-- 根據 email 查詢使用者
SELECT * FROM users WHERE email = 'john@example.com';

-- 查詢使用者發文數量
SELECT u.username, COUNT(p.id) as post_count
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
GROUP BY u.id, u.username;
```

### 發文相關查詢
```sql
-- 查詢所有發文（包含作者資訊）
SELECT p.id, p.content, p.created_at, u.username
FROM posts p
JOIN users u ON p.user_id = u.id
ORDER BY p.created_at DESC;

-- 查詢特定使用者的發文
SELECT * FROM posts WHERE user_id = 1 ORDER BY created_at DESC;

-- 查詢發文及其留言數量
SELECT p.id, p.content, COUNT(c.id) as comment_count
FROM posts p
LEFT JOIN comments c ON p.id = c.post_id
GROUP BY p.id, p.content;
```

### 留言相關查詢
```sql
-- 查詢特定發文的所有留言
SELECT c.content, c.created_at, u.username
FROM comments c
JOIN users u ON c.user_id = u.id
WHERE c.post_id = 1
ORDER BY c.created_at ASC;

-- 查詢使用者的所有留言
SELECT c.content, c.created_at, p.content as post_content
FROM comments c
JOIN posts p ON c.post_id = p.id
WHERE c.user_id = 1
ORDER BY c.created_at DESC;
```

## 🛠 維護操作

### 備份資料庫
```bash
# 備份整個資料庫
cp social_media.db social_media_backup_$(date +%Y%m%d).db

# 匯出為 SQL 格式
sqlite3 social_media.db .dump > backup.sql
```

### 效能監控
```sql
-- 查看查詢計劃
EXPLAIN QUERY PLAN SELECT * FROM posts WHERE user_id = 1;

-- 分析資料庫統計
ANALYZE;

-- 查看索引使用情況
.schema --indent
```

### 清理操作
```sql
-- 清空所有資料（保留結構）
DELETE FROM comments;
DELETE FROM posts;
DELETE FROM users;

-- 重置自動遞增序列
DELETE FROM sqlite_sequence;

-- 壓縮資料庫檔案
VACUUM;
```

## ⚠️ 注意事項

1. **外鍵約束**: SQLite 預設不啟用外鍵約束，需要執行 `PRAGMA foreign_keys = ON;`
2. **資料型別**: SQLite 使用動態型別系統，但建議明確指定型別
3. **並發處理**: SQLite 支援多讀者，但只允許一個寫入者
4. **檔案權限**: 確保應用程式對資料庫檔案有讀寫權限
5. **備份策略**: 定期備份資料庫檔案，特別是在生產環境

## 🔧 疑難排解

### 常見問題
1. **資料庫鎖定**: 確保沒有其他程序正在使用資料庫
2. **權限問題**: 檢查檔案系統權限
3. **路徑問題**: 使用絕對路徑或確保相對路徑正確
4. **編碼問題**: 確保使用 UTF-8 編碼

### 除錯指令
```bash
# 檢查資料庫完整性
sqlite3 social_media.db "PRAGMA integrity_check;"

# 查看資料庫資訊
sqlite3 social_media.db ".dbinfo"

# 查看所有表格
sqlite3 social_media.db ".tables"

# 查看表格結構
sqlite3 social_media.db ".schema users"
```
