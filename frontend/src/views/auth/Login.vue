<template>
  <div class="login-container">
    <!-- 背景裝飾 -->
    <div class="background-decoration">
      <div class="mountain-bg"></div>
      <div class="gradient-overlay"></div>
    </div>

    <div class="login-content">
      <!-- 左側品牌區域 -->
      <div class="brand-section">
        <BrandLogo size="xlarge" :show-text="true" variant="white" />
        <div class="brand-description">
          <h2 class="brand-slogan">連結每一座山峰</h2>
          <p class="brand-text">玉山銀行社群媒體平台，讓您與世界保持連結，分享生活中的每一個精彩時刻。</p>
        </div>
      </div>

      <!-- 右側登入卡片 -->
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">歡迎回來</h1>
          <p class="login-subtitle">請登入您的帳戶以繼續使用</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="usernameOrEmail">
            <el-input
              v-model="loginForm.usernameOrEmail"
              placeholder="使用者名稱或電子郵件"
              size="large"
              :prefix-icon="User"
              clearable
              class="form-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密碼"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              class="form-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="authStore.loading"
              @click="handleLogin"
            >
              <span v-if="!authStore.loading">登入</span>
              <span v-else>登入中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 註冊連結 -->
        <div class="login-footer">
          <p class="register-prompt">
            還沒有帳號？
            <router-link to="/register" class="register-link">
              立即註冊
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { User, Lock } from '@element-plus/icons-vue'
import BrandLogo from '@/components/common/BrandLogo.vue'

const authStore = useAuthStore()
const loginFormRef = ref()

// 表單資料
const loginForm = reactive({
  usernameOrEmail: '',
  password: ''
})

// 表單驗證規則
const loginRules = {
  usernameOrEmail: [
    { required: true, message: '請輸入使用者名稱或電子郵件', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '請輸入密碼', trigger: 'blur' },
    { min: 6, message: '密碼長度至少 6 個字元', trigger: 'blur' }
  ]
}

// 處理登入
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    await authStore.login(loginForm)
  } catch (error) {
    console.error('登入失敗:', error)
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.mountain-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-primary);
  opacity: 0.9;
}

.mountain-bg::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60%;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1200 600'%3E%3Cpath d='M0,600 L200,300 L400,400 L600,200 L800,350 L1000,150 L1200,300 L1200,600 Z' fill='rgba(255,255,255,0.1)'/%3E%3Cpath d='M0,600 L150,400 L350,450 L550,250 L750,380 L950,200 L1200,350 L1200,600 Z' fill='rgba(255,255,255,0.05)'/%3E%3C/svg%3E") no-repeat center bottom;
  background-size: cover;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg,
    rgba(45, 155, 122, 0.8) 0%,
    rgba(61, 186, 160, 0.6) 50%,
    rgba(124, 199, 178, 0.4) 100%);
}

.login-content {
  display: flex;
  width: 100%;
  position: relative;
  z-index: 1;
}

.brand-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-xxl);
  text-align: center;
}

.brand-description {
  margin-top: var(--spacing-xl);
  max-width: 480px;
}

.brand-slogan {
  font-size: var(--font-size-title);
  font-weight: 700;
  color: white;
  margin-bottom: var(--spacing-md);
  line-height: 1.2;
}

.brand-text {
  font-size: var(--font-size-lg);
  color: rgba(255, 255, 255, 0.9);
  line-height: var(--line-height-lg);
  font-weight: 300;
}

.login-card {
  flex: 0 0 480px;
  background: var(--bg-color);
  border-radius: var(--border-radius-large);
  box-shadow: var(--shadow-dark);
  padding: var(--spacing-xxl);
  margin: var(--spacing-xl);
  display: flex;
  flex-direction: column;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.login-title {
  font-size: var(--font-size-xxl);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  line-height: 1.2;
}

.login-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--line-height-base);
}

.login-form {
  margin-bottom: var(--spacing-lg);
}

.form-input {
  margin-bottom: var(--spacing-md);
}

.form-input :deep(.el-input__wrapper) {
  border-radius: var(--border-radius-base);
  box-shadow: var(--shadow-light);
  border: 1px solid var(--border-light);
  transition: var(--transition-base);
}

.form-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light);
}

.form-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(45, 155, 122, 0.1);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-base);
  font-weight: 600;
  border-radius: var(--border-radius-base);
  background: var(--gradient-primary);
  border: none;
  transition: var(--transition-base);
}

.login-button:hover {
  background: var(--gradient-secondary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-dark);
}

.login-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
}

.register-prompt {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: var(--transition-base);
}

.register-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

/* 響應式設計 */
@media (max-width: 1024px) {
  .login-content {
    flex-direction: column;
  }

  .brand-section {
    flex: 0 0 auto;
    padding: var(--spacing-xl) var(--spacing-lg);
    min-height: 40vh;
  }

  .brand-slogan {
    font-size: var(--font-size-xl);
  }

  .brand-text {
    font-size: var(--font-size-base);
  }

  .login-card {
    flex: 1;
    margin: 0;
    border-radius: var(--border-radius-large) var(--border-radius-large) 0 0;
  }
}

@media (max-width: 768px) {
  .brand-section {
    padding: var(--spacing-lg);
    min-height: 30vh;
  }

  .brand-slogan {
    font-size: var(--font-size-lg);
  }

  .brand-text {
    font-size: var(--font-size-sm);
  }

  .login-card {
    padding: var(--spacing-lg);
  }

  .login-title {
    font-size: var(--font-size-xl);
  }

  .login-subtitle {
    font-size: var(--font-size-sm);
  }
}

@media (max-width: 480px) {
  .brand-section {
    padding: var(--spacing-md);
  }

  .login-card {
    padding: var(--spacing-md);
  }

  .login-button {
    height: 44px;
  }
}

/* 動畫效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-card {
  animation: fadeInUp 0.6s ease-out;
}

.brand-section {
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

/* 加載狀態 */
.login-button.is-loading {
  background: var(--primary-light);
}

.login-button.is-loading:hover {
  background: var(--primary-light);
  transform: none;
}

/* 背景裝飾 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: -75px;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: -50px;
  left: 50%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 響應式設計 */
@media (max-width: 480px) {
  .login-card {
    padding: 24px;
    margin: 16px;
  }
  
  .title {
    font-size: 20px;
  }
  
  .login-button {
    height: 44px;
  }
}
</style>
