# 開發環境設置指南

## 📋 必要軟體安裝

### 1. Java Development Kit (JDK) 17+

#### Windows 安裝方式：
```powershell
# 方法一：使用 Chocolatey (推薦)
choco install openjdk17

# 方法二：使用 Scoop
scoop install openjdk17

# 方法三：手動下載安裝
# 前往 https://adoptium.net/ 下載 Eclipse Temurin JDK 17
```

#### 驗證安裝：
```bash
java -version
javac -version
```

### 2. Apache Maven 3.8+

#### Windows 安裝方式：
```powershell
# 方法一：使用 Chocolatey (推薦)
choco install maven

# 方法二：使用 Scoop
scoop install maven

# 方法三：手動安裝
# 1. 下載 Maven: https://maven.apache.org/download.cgi
# 2. 解壓縮到 C:\Program Files\Apache\maven
# 3. 設置環境變數 MAVEN_HOME 和 PATH
```

#### 驗證安裝：
```bash
mvn -version
```

### 3. Node.js 18+ (前端開發用)

#### Windows 安裝方式：
```powershell
# 方法一：使用 Chocolatey
choco install nodejs

# 方法二：使用 Scoop
scoop install nodejs

# 方法三：官方安裝程式
# 前往 https://nodejs.org/ 下載 LTS 版本
```

#### 驗證安裝：
```bash
node -version
npm -version
```

### 4. Git 版本控制

#### Windows 安裝方式：
```powershell
# 使用 Chocolatey
choco install git

# 或下載官方安裝程式
# https://git-scm.com/download/win
```

## 🚀 專案啟動步驟

### 後端啟動

1. **複製專案**
```bash
git clone <repository-url>
cd social-media-platform
```

2. **建立資料目錄**
```bash
mkdir data
mkdir uploads
mkdir logs
```

3. **編譯專案**
```bash
mvn clean compile
```

4. **執行測試**
```bash
mvn test
```

5. **啟動應用程式**
```bash
# 開發模式
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或使用預設配置
mvn spring-boot:run
```

6. **驗證啟動**
- API 文件: http://localhost:8080/api/swagger-ui.html
- 健康檢查: http://localhost:8080/api/actuator/health

### 前端啟動 (後續實作)

```bash
cd frontend
npm install
npm run dev
```

## 🛠 IDE 設置建議

### IntelliJ IDEA
1. 安裝 Spring Boot 插件
2. 設置 JDK 17
3. 配置 Maven 設定
4. 啟用 Lombok 插件

### Visual Studio Code
1. 安裝 Java Extension Pack
2. 安裝 Spring Boot Extension Pack
3. 配置 Java 路徑

## 🔧 常見問題解決

### Maven 相關問題

**問題**: `mvn` 命令找不到
**解決**: 確認 Maven 已正確安裝並加入 PATH 環境變數

**問題**: 依賴下載失敗
**解決**: 
```bash
# 清除本地倉庫快取
mvn dependency:purge-local-repository

# 強制更新依賴
mvn clean install -U
```

### Java 相關問題

**問題**: `JAVA_HOME` 未設置
**解決**: 設置環境變數
```powershell
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot"
```

### SQLite 相關問題

**問題**: 資料庫檔案權限錯誤
**解決**: 確保應用程式對 `./data/` 目錄有讀寫權限

## 📊 開發工具推薦

### 資料庫管理
- **DB Browser for SQLite**: 免費的 SQLite 資料庫瀏覽器
- **DBeaver**: 通用資料庫管理工具

### API 測試
- **Postman**: API 測試工具
- **Insomnia**: 輕量級 REST 客戶端
- **curl**: 命令列 HTTP 工具

### 程式碼品質
- **SonarLint**: 程式碼品質檢查插件
- **Checkstyle**: Java 程式碼風格檢查
- **SpotBugs**: 靜態程式碼分析

## 🔍 除錯指南

### 應用程式無法啟動
1. 檢查 Java 版本是否為 17+
2. 確認 8080 埠口未被占用
3. 檢查資料庫檔案權限
4. 查看日誌檔案 `logs/social-media-platform.log`

### 資料庫連接問題
1. 確認 SQLite 驅動程式已正確載入
2. 檢查資料庫檔案路徑
3. 驗證外鍵約束設定

### 測試執行失敗
1. 確認測試資料庫 (H2) 配置正確
2. 檢查測試資源檔案路徑
3. 清除測試快取：`mvn clean test`

---

如有任何問題，請參考專案 README.md 或建立 GitHub Issue。
