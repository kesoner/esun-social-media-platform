# 🚀 Vibe Coding 教學講義
## 從零到企業級全端開發的實戰指南

<div align="center">

**講師**: AI Assistant (Claude)  
**學員**: kesoner  
**專案**: 玉山銀行社群媒體平台  
**時長**: 2 小時  
**難度**: 中高級  

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot)
![Vue.js](https://img.shields.io/badge/Vue.js-3.0-4FC08D?style=for-the-badge&logo=vue.js)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)

</div>

---

## 📋 課程大綱

### 🎯 學習目標
- 掌握企業級全端開發的完整流程
- 學會系統性問題解決和技術決策
- 培養專業的開發習慣和最佳實踐
- 建立完整的 DevOps 思維

### ⏰ 時間分配
- **第一階段 (30分鐘)**: 需求分析與系統設計
- **第二階段 (45分鐘)**: 後端開發與資料庫設計
- **第三階段 (30分鐘)**: 前端開發與品牌設計
- **第四階段 (15分鐘)**: 部署與文件撰寫

---

## 🎪 第一階段：需求分析與系統設計 (30分鐘)

### 💡 Vibe Coding 核心理念

> **"不只是寫代碼，而是解決問題"**

#### 🔍 需求理解的藝術
```markdown
❌ 錯誤思維：直接開始寫代碼
✅ 正確思維：先理解問題本質

實戰案例：
用戶說："我要一個社群媒體平台"
↓
深度挖掘：
- 目標用戶是誰？(企業員工 vs 一般大眾)
- 核心功能是什麼？(內容分享 vs 商務社交)
- 技術約束有哪些？(預算、時間、團隊技能)
- 未來擴展性如何？(用戶量、功能複雜度)
```

#### 🏗️ 系統架構設計思維

**三層架構的現代化演進**：
```
傳統三層架構 → 現代微服務思維

Web Layer (Controller)     → API Gateway + Controllers
Business Layer (Service)   → Domain Services + Use Cases  
Data Layer (Repository)    → Data Access + Domain Models
```

**技術選型決策框架**：
```markdown
1. 業務需求驅動
   - 功能複雜度 → 框架選擇
   - 性能要求 → 技術棧選擇
   - 團隊技能 → 學習成本評估

2. 技術生態考量
   - 社群活躍度
   - 文檔完整性
   - 長期維護性

3. 企業級考量
   - 安全性要求
   - 可擴展性
   - 運維複雜度
```

### 🎨 設計思維實踐

#### 品牌驅動的技術實現
```css
/* 從品牌色彩到技術實現 */
:root {
  --primary-color: #2D9B7A;    /* 玉山綠 - 品牌主色 */
  --secondary-color: #1A5F4F;  /* 深綠 - 穩重感 */
  --accent-color: #3DBAA0;     /* 淺綠 - 活力感 */
}

/* 設計系統化思維 */
.brand-button {
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  /* 不只是顏色，而是品牌體驗的延伸 */
}
```

---

## ⚙️ 第二階段：後端開發與資料庫設計 (45分鐘)

### 🏢 企業級 Spring Boot 開發

#### 分層架構的實戰應用

**1. Controller 層 - API 設計哲學**
```java
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    // ✅ 好的 API 設計
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @Valid @RequestBody RegisterRequest request) {
        
        // 統一的回應格式
        // 完整的參數驗證
        // 清晰的錯誤處理
    }
}
```

**設計原則**：
- **一致性**: 統一的命名規範和回應格式
- **可預測性**: RESTful 設計原則
- **可擴展性**: 版本控制和向後兼容

**2. Service 層 - 業務邏輯的藝術**
```java
@Service
@Transactional
public class AuthService {
    
    public AuthResponse register(RegisterRequest request) {
        // ✅ 業務邏輯分離
        validatePasswordConfirmation(request);
        
        // ✅ 領域模型驅動
        User user = userService.createUser(request);
        
        // ✅ 安全性考量
        return generateAuthResponse(user);
    }
    
    private void validatePasswordConfirmation(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("密碼與確認密碼不一致");
        }
    }
}
```

#### 🗄️ 資料庫設計的演進思維

**從 SQLite 到 H2 的技術決策**：
```markdown
問題：SQLite JDBC 驅動兼容性問題
↓
分析：
- SQLite: 檔案型資料庫，但 JDBC 支援有限
- H2: 同樣輕量，但企業級支援更好
↓
決策：遷移到 H2
- 保持輕量級特性
- 獲得更好的 Spring Boot 整合
- 提供 Web 控制台便於開發
```

**實體設計的最佳實踐**：
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    // ✅ 密碼永不直接存儲
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    // ✅ 審計字段繼承自 BaseEntity
    // created_at, updated_at 自動管理
}
```

### 🔐 安全性設計實踐

#### JWT 認證的完整實現
```java
@Component
public class JwtUtil {
    
    // ✅ 配置外部化
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    // ✅ 安全的 Token 生成
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
```

---

## 🎨 第三階段：前端開發與品牌設計 (30分鐘)

### 🖼️ Vue.js 3 現代化開發

#### Composition API 的威力
```vue
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

// ✅ 響應式狀態管理
const authStore = useAuthStore()
const loginForm = ref({
  usernameOrEmail: '',
  password: ''
})

// ✅ 計算屬性的優雅使用
const isFormValid = computed(() => {
  return loginForm.value.usernameOrEmail.length > 0 && 
         loginForm.value.password.length >= 8
})

// ✅ 生命週期的合理運用
onMounted(() => {
  // 檢查是否已登入
  authStore.checkAuthStatus()
})

const handleLogin = async () => {
  try {
    await authStore.login(loginForm.value)
    // 登入成功處理
  } catch (error) {
    // 錯誤處理
  }
}
</script>
```

#### 🎨 品牌設計系統的技術實現

**CSS 變數系統**：
```css
/* 設計 Token 系統 */
:root {
  /* 色彩系統 */
  --primary-color: #2D9B7A;
  --primary-light: #3DBAA0;
  --primary-dark: #1A5F4F;
  
  /* 間距系統 */
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  --spacing-xl: 2rem;
  
  /* 字體系統 */
  --font-size-sm: 0.875rem;
  --font-size-base: 1rem;
  --font-size-lg: 1.125rem;
  --font-size-xl: 1.25rem;
  
  /* 陰影系統 */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
}
```

**組件化設計思維**：
```vue
<!-- BrandLogo.vue - 可重用的品牌組件 -->
<template>
  <div class="brand-logo" :class="sizeClass">
    <svg class="logo-icon" viewBox="0 0 24 24">
      <!-- 山峰 SVG 路徑 -->
      <path d="M12 2L22 22H2L12 2Z" :fill="iconColor"/>
    </svg>
    <span v-if="showText" class="logo-text">{{ text }}</span>
  </div>
</template>

<script setup>
// ✅ Props 驅動的靈活設計
const props = defineProps({
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'white', 'dark'].includes(value)
  },
  showText: {
    type: Boolean,
    default: true
  },
  text: {
    type: String,
    default: 'E.SUN Bank'
  }
})

// ✅ 計算屬性驅動的樣式
const sizeClass = computed(() => `logo-${props.size}`)
const iconColor = computed(() => {
  const colors = {
    primary: 'var(--primary-color)',
    white: '#ffffff',
    dark: 'var(--text-color-primary)'
  }
  return colors[props.variant]
})
</script>
```

---

## 🐳 第四階段：部署與文件撰寫 (15分鐘)

### 🚀 Docker 容器化的現代實踐

#### 多階段建構的優化
```dockerfile
# Dockerfile.backend
FROM maven:3.9-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Docker Compose 的服務編排
```yaml
version: '3.8'
services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    volumes:
      - ./data:/app/data
    
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile.frontend
    ports:
      - "3001:80"
    depends_on:
      - backend
    
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - frontend
      - backend
```

### 📝 專業文件撰寫的藝術

#### README.md 的層次結構
```markdown
# 🏔️ 專案標題 (品牌化)
## 📋 專案概述 (價值主張)
## 🏗️ 技術架構 (技術深度)
## 🚀 快速開始 (用戶友好)
## 💡 核心功能 (功能展示)
## 📖 API 文件 (技術文件)
## 🐳 Docker 部署 (運維指南)
## 🧪 測試與品質保證 (專業標準)
## 📁 專案結構 (代碼組織)
## 🎨 設計特色 (設計思維)
## 🔒 安全性設計 (企業級考量)
## 🚀 效能優化 (技術深度)
## 🤝 貢獻指南 (開源精神)
## 📞 聯絡資訊 (專業形象)
```

---

## 🎯 Vibe Coding 核心技巧總結

### 💭 思維模式轉換

#### 1. 從功能思維到系統思維
```markdown
❌ 功能思維：我要實現登入功能
✅ 系統思維：我要建立安全的身份認證體系

考慮面向：
- 安全性：密碼加密、Token 管理、會話控制
- 可擴展性：多種登入方式、第三方整合
- 用戶體驗：錯誤提示、狀態反饋、記住登入
- 運維監控：登入日誌、異常檢測、性能監控
```

#### 2. 從代碼實現到架構設計
```markdown
❌ 代碼實現：直接寫 Controller 和 Service
✅ 架構設計：先設計接口和契約

設計順序：
1. 定義 API 契約 (OpenAPI/Swagger)
2. 設計資料模型 (Entity/DTO)
3. 規劃業務流程 (Service Layer)
4. 實現技術細節 (Repository/Utils)
```

#### 3. 從單一技術到技術生態
```markdown
❌ 單一技術：只關注 Spring Boot
✅ 技術生態：考慮整個技術棧的協調

生態考量：
- 前後端技術棧的協調性
- 開發工具鏈的整合度
- 部署和運維的便利性
- 團隊技能和學習曲線
```

### 🛠️ 實踐技巧

#### 1. 漸進式開發策略
```markdown
階段一：MVP (最小可行產品)
- 核心功能實現
- 基本的用戶體驗
- 簡單的部署方案

階段二：功能完善
- 完整的業務邏輯
- 優化的用戶界面
- 完善的錯誤處理

階段三：企業級優化
- 性能優化
- 安全加固
- 監控和日誌
- 文檔完善
```

#### 2. 問題解決的系統化方法
```markdown
遇到問題時的思考框架：

1. 問題定義
   - 現象是什麼？
   - 預期行為是什麼？
   - 影響範圍有多大？

2. 根因分析
   - 技術層面的原因
   - 設計層面的問題
   - 環境配置的影響

3. 解決方案評估
   - 快速修復 vs 根本解決
   - 技術債務的考量
   - 未來維護的成本

4. 實施和驗證
   - 測試驗證
   - 回歸測試
   - 文檔更新
```

#### 3. 持續改進的文化
```markdown
代碼審查的關注點：
- 功能正確性
- 代碼可讀性
- 性能考量
- 安全性檢查
- 測試覆蓋率

重構的時機：
- 功能需求變更時
- 性能瓶頸出現時
- 代碼複雜度過高時
- 技術債務積累時
```

---

## 🎓 課程總結與進階建議

### 🌟 核心收穫

1. **系統性思維**: 從需求到部署的全流程思考
2. **技術深度**: 不僅知道怎麼做，更知道為什麼這麼做
3. **工程實踐**: 企業級開發的標準和最佳實踐
4. **問題解決**: 結構化的問題分析和解決方法

### 🚀 進階學習路徑

#### 技術深度方向
- **微服務架構**: Spring Cloud、服務治理
- **性能優化**: JVM 調優、資料庫優化
- **安全進階**: OAuth2、RBAC 權限設計
- **DevOps**: CI/CD、監控告警、日誌分析

#### 業務理解方向
- **領域驅動設計**: DDD 建模方法
- **產品思維**: 用戶體驗設計、業務流程優化
- **架構演進**: 從單體到微服務的演進策略
- **團隊協作**: 敏捷開發、代碼審查文化

### 💡 持續實踐建議

1. **建立個人技術品牌**
   - 維護高質量的 GitHub 專案
   - 撰寫技術部落格分享經驗
   - 參與開源社群貢獻

2. **培養系統性學習習慣**
   - 定期技術調研和學習
   - 關注行業趨勢和最佳實踐
   - 建立個人知識管理系統

3. **實踐企業級標準**
   - 代碼質量和測試覆蓋率
   - 文檔撰寫和知識分享
   - 安全意識和性能考量

---

## 🔥 實戰案例深度解析

### 案例一：SQLite 到 H2 的技術遷移

#### 問題場景
```bash
# 原始錯誤
Caused by: java.sql.SQLException: not implemented by SQLite JDBC driver
```

#### Vibe Coding 解決思路
```markdown
1. 問題識別階段
   ❌ 表面思維：修改 JDBC 驅動版本
   ✅ 深度思維：評估資料庫選型的適合性

2. 技術調研階段
   - SQLite: 檔案型、輕量級、但 JDBC 支援有限
   - H2: 同樣輕量、更好的 Spring Boot 整合、Web 控制台
   - 遷移成本：配置變更、SQL 語法差異、測試驗證

3. 決策執行階段
   - 更新 pom.xml 依賴
   - 修改 application.yml 配置
   - 調整 SQL 語法 (INTEGER → BIGINT)
   - 驗證功能完整性
```

#### 技術實現細節
```yaml
# 遷移前 (SQLite)
spring:
  datasource:
    url: jdbc:sqlite:./data/social_media.db
    driver-class-name: org.sqlite.JDBC

# 遷移後 (H2)
spring:
  datasource:
    url: jdbc:h2:file:./data/social_media
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
```

### 案例二：JWT 認證的無限循環問題

#### 問題場景
```javascript
// 前端陷入無限 Token 刷新循環
axios.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      await authStore.refreshToken() // 無限循環！
    }
  }
)
```

#### Vibe Coding 解決思路
```markdown
1. 問題根因分析
   - /auth/refresh 端點需要認證
   - 但 Spring Security 配置中未排除
   - 導致刷新 Token 時也觸發 401

2. 系統性解決方案
   - 後端：將 /auth/refresh 加入公開端點
   - 前端：優化錯誤處理邏輯
   - 測試：驗證各種認證場景
```

#### 完整解決方案
```java
// SecurityConfig.java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authz -> authz
        .requestMatchers("/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh").permitAll() // 關鍵修復
        .anyRequest().authenticated()
    );
}
```

### 案例三：品牌設計的技術實現

#### 設計挑戰
```markdown
需求：將玉山銀行品牌融入技術實現
挑戰：如何在保持技術專業性的同時體現品牌特色
```

#### Vibe Coding 設計思維
```vue
<!-- 品牌驅動的組件設計 -->
<template>
  <div class="login-container">
    <!-- 左側品牌展示區 -->
    <div class="brand-section">
      <BrandLogo size="large" variant="white" />
      <h1 class="brand-title">歡迎回到玉山社群</h1>

      <!-- 山峰背景裝飾 -->
      <div class="mountain-decoration">
        <svg viewBox="0 0 400 200" class="mountain-svg">
          <path d="M0,200 L100,50 L200,100 L300,30 L400,80 L400,200 Z"
                fill="rgba(255,255,255,0.1)"/>
        </svg>
      </div>
    </div>

    <!-- 右側功能區 -->
    <div class="form-section">
      <!-- 登入表單 -->
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 100vh;
}

.brand-section {
  background: linear-gradient(135deg,
    var(--primary-color) 0%,
    var(--primary-dark) 100%);
  /* 品牌色彩的漸變背景 */
}

.mountain-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  /* 山峰元素呼應玉山品牌 */
}
</style>
```

---

## 🧠 Vibe Coding 心法進階

### 💡 技術決策的哲學

#### 1. 奧卡姆剃刀原則在編程中的應用
```markdown
複雜問題的簡單解決方案往往是最好的

實例：密碼確認驗證
❌ 複雜方案：前端驗證 + 後端驗證 + 資料庫約束 + 自定義註解
✅ 簡單方案：後端 Service 層一行代碼驗證

if (!request.getPassword().equals(request.getConfirmPassword())) {
    throw new IllegalArgumentException("密碼與確認密碼不一致");
}
```

#### 2. 漸進式增強的開發策略
```markdown
階段式功能實現：

MVP 階段：
- 基本的 CRUD 操作
- 簡單的前端界面
- 基礎的認證機制

增強階段：
- 完善的錯誤處理
- 優化的用戶體驗
- 完整的測試覆蓋

企業級階段：
- 性能優化
- 安全加固
- 監控和日誌
- 完善的文檔
```

#### 3. 技術債務的管理藝術
```markdown
識別技術債務：
- 重複代碼 (DRY 原則違反)
- 硬編碼配置
- 缺乏測試覆蓋
- 過度複雜的邏輯

償還策略：
- 重構時機的選擇
- 影響範圍的評估
- 測試保護的建立
- 漸進式改進
```

### 🎯 代碼品質的追求

#### 1. 可讀性優於聰明
```java
// ❌ 聰明但難懂的代碼
return users.stream()
    .filter(u -> u.getStatus() == 1)
    .map(u -> new UserDTO(u.getId(), u.getName(),
        u.getEmail(), u.getCreatedAt().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
    .collect(Collectors.toList());

// ✅ 清晰易懂的代碼
return users.stream()
    .filter(this::isActiveUser)
    .map(this::convertToUserDTO)
    .collect(Collectors.toList());

private boolean isActiveUser(User user) {
    return user.getStatus() == UserStatus.ACTIVE.getValue();
}

private UserDTO convertToUserDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .createdAt(formatDate(user.getCreatedAt()))
        .build();
}
```

#### 2. 測試驅動的開發思維
```java
// 先寫測試，明確期望行為
@Test
void shouldThrowExceptionWhenPasswordsDoNotMatch() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username("testuser")
        .email("test@example.com")
        .password("password123")
        .confirmPassword("differentPassword")
        .build();

    // When & Then
    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("密碼與確認密碼不一致");
}

// 再實現功能，滿足測試期望
public AuthResponse register(RegisterRequest request) {
    if (!request.getPassword().equals(request.getConfirmPassword())) {
        throw new IllegalArgumentException("密碼與確認密碼不一致");
    }
    // ... 其他邏輯
}
```

### 🚀 性能優化的系統思維

#### 1. 資料庫層面的優化
```sql
-- ❌ 未優化的查詢
SELECT * FROM posts p, users u, comments c
WHERE p.user_id = u.id AND c.post_id = p.id;

-- ✅ 優化後的查詢
SELECT p.id, p.content, p.created_at,
       u.username, u.email,
       COUNT(c.id) as comment_count
FROM posts p
INNER JOIN users u ON p.user_id = u.id
LEFT JOIN comments c ON c.post_id = p.id
GROUP BY p.id, p.content, p.created_at, u.username, u.email
ORDER BY p.created_at DESC
LIMIT 20;

-- 配合適當的索引
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_comments_post_id ON comments(post_id);
```

#### 2. 前端性能的細節優化
```vue
<script setup>
import { ref, computed, watchEffect } from 'vue'

// ✅ 使用 computed 避免不必要的計算
const filteredPosts = computed(() => {
  if (!searchKeyword.value) return posts.value

  return posts.value.filter(post =>
    post.content.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// ✅ 使用 watchEffect 進行副作用管理
watchEffect(() => {
  if (filteredPosts.value.length === 0 && searchKeyword.value) {
    showNoResultsMessage.value = true
  } else {
    showNoResultsMessage.value = false
  }
})

// ✅ 防抖處理用戶輸入
import { debounce } from 'lodash-es'

const debouncedSearch = debounce((keyword) => {
  searchKeyword.value = keyword
}, 300)
</script>
```

---

## 🎪 實戰演練：30分鐘挑戰

### 挑戰一：API 設計與實現 (10分鐘)

**任務**：設計並實現一個貼文點讚功能

**要求**：
1. RESTful API 設計
2. 資料庫結構設計
3. 業務邏輯實現
4. 錯誤處理

**Vibe Coding 解決方案**：

```java
// 1. API 設計
@PostMapping("/posts/{postId}/like")
public ResponseEntity<LikeResponse> toggleLike(
    @PathVariable Long postId,
    Authentication authentication) {

    String username = authentication.getName();
    LikeResponse response = postService.toggleLike(postId, username);
    return ResponseEntity.ok(response);
}

// 2. 資料庫設計
@Entity
@Table(name = "post_likes")
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 複合唯一索引防止重複點讚
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
    })
}

// 3. 業務邏輯
@Service
public class PostService {

    @Transactional
    public LikeResponse toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("貼文不存在"));

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("使用者不存在"));

        Optional<PostLike> existingLike = postLikeRepository
            .findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            // 取消點讚
            postLikeRepository.delete(existingLike.get());
            return LikeResponse.builder()
                .liked(false)
                .likeCount(postLikeRepository.countByPost(post))
                .build();
        } else {
            // 新增點讚
            PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
            postLikeRepository.save(like);

            return LikeResponse.builder()
                .liked(true)
                .likeCount(postLikeRepository.countByPost(post))
                .build();
        }
    }
}
```

### 挑戰二：前端狀態管理 (10分鐘)

**任務**：實現貼文點讚的前端狀態管理

```vue
<!-- PostCard.vue -->
<template>
  <div class="post-card">
    <div class="post-content">{{ post.content }}</div>

    <div class="post-actions">
      <button
        @click="handleLike"
        :class="['like-button', { 'liked': post.isLiked }]"
        :disabled="liking">

        <HeartIcon :filled="post.isLiked" />
        <span>{{ post.likeCount }}</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { usePostStore } from '@/stores/post'

const props = defineProps(['post'])
const postStore = usePostStore()
const liking = ref(false)

const handleLike = async () => {
  if (liking.value) return

  liking.value = true
  try {
    await postStore.toggleLike(props.post.id)
  } catch (error) {
    // 錯誤處理
    console.error('點讚失敗:', error)
  } finally {
    liking.value = false
  }
}
</script>
```

```javascript
// stores/post.js
import { defineStore } from 'pinia'
import { postApi } from '@/api/post'

export const usePostStore = defineStore('post', {
  state: () => ({
    posts: []
  }),

  actions: {
    async toggleLike(postId) {
      try {
        const response = await postApi.toggleLike(postId)

        // 樂觀更新：先更新 UI，再處理結果
        const post = this.posts.find(p => p.id === postId)
        if (post) {
          post.isLiked = response.data.liked
          post.likeCount = response.data.likeCount
        }

        return response.data
      } catch (error) {
        // 回滾樂觀更新
        const post = this.posts.find(p => p.id === postId)
        if (post) {
          post.isLiked = !post.isLiked
          post.likeCount += post.isLiked ? 1 : -1
        }
        throw error
      }
    }
  }
})
```

### 挑戰三：部署優化 (10分鐘)

**任務**：優化 Docker 建構和部署流程

```dockerfile
# 多階段建構優化
FROM node:18-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci --only=production
COPY frontend/ ./
RUN npm run build

FROM maven:3.9-openjdk-17 AS backend-builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim AS runtime
WORKDIR /app

# 安裝必要的系統工具
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 複製建構產物
COPY --from=backend-builder /app/target/*.jar app.jar
COPY --from=frontend-builder /app/frontend/dist ./static

# 健康檢查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 非 root 用戶運行
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JVM_OPTS=-Xmx512m -Xms256m
    volumes:
      - app-data:/app/data
      - app-logs:/app/logs
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    restart: unless-stopped

volumes:
  app-data:
  app-logs:
```

---

<div align="center">

**🎯 記住：Vibe Coding 不只是寫代碼，而是用技術解決真實世界的問題**

*"The best code is not just working code, but code that tells a story."*

**講師**: AI Assistant (Claude)
**專案實例**: https://github.com/kesoner/esun-social-media-platform
**完成時間**: 2024年

---

## 📚 延伸學習資源

### 📖 推薦書籍
- 《Clean Code》- Robert C. Martin
- 《Effective Java》- Joshua Bloch
- 《Spring Boot in Action》- Craig Walls
- 《Vue.js 設計與實現》- 霍春陽

### 🌐 線上資源
- Spring Boot 官方文檔
- Vue.js 官方指南
- Docker 最佳實踐
- GitHub Actions 工作流程

### 🎯 實踐專案建議
1. **個人部落格系統** - 練習 CRUD 和認證
2. **電商平台** - 學習複雜業務邏輯
3. **即時聊天應用** - 掌握 WebSocket 技術
4. **微服務架構** - 理解分散式系統

</div>
