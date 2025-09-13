<template>
  <div class="register-page">
    <!-- 左側品牌區域 -->
    <div class="brand-section">
      <div class="brand-content">
        <!-- 品牌 Logo -->
        <div class="brand-logo">
          <BrandLogo size="large" variant="light" />
        </div>

        <!-- 品牌標語 -->
        <div class="brand-text">
          <h1 class="brand-title">加入玉山社群</h1>
          <p class="brand-subtitle">與志同道合的夥伴分享生活點滴</p>
          <div class="brand-features">
            <div class="feature-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>即時互動交流</span>
            </div>
            <div class="feature-item">
              <el-icon><User /></el-icon>
              <span>建立個人檔案</span>
            </div>
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>發現精彩內容</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 山峰背景裝飾 -->
      <div class="mountain-decoration">
        <svg viewBox="0 0 400 200" class="mountain-svg">
          <defs>
            <linearGradient id="mountainGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#3DBAA0;stop-opacity:0.3" />
              <stop offset="100%" style="stop-color:#2D9B7A;stop-opacity:0.1" />
            </linearGradient>
          </defs>
          <path d="M0 200 L80 120 L160 140 L240 80 L320 100 L400 60 L400 200 Z" fill="url(#mountainGradient)" />
          <path d="M0 200 L60 140 L140 160 L220 100 L300 120 L400 80 L400 200 Z" fill="url(#mountainGradient)" opacity="0.6" />
        </svg>
      </div>
    </div>

    <!-- 右側註冊表單區域 -->
    <div class="form-section">
      <div class="form-container">
        <!-- 表單標題 -->
        <div class="form-header">
          <h2 class="form-title">建立新帳號</h2>
          <p class="form-subtitle">填寫以下資訊開始您的社群之旅</p>
        </div>

        <!-- 註冊表單 -->
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="使用者名稱"
              size="large"
              :prefix-icon="User"
              clearable
              @blur="checkUsernameAvailability"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="電子郵件"
              size="large"
              :prefix-icon="Message"
              clearable
              @blur="checkEmailAvailability"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密碼"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="確認密碼"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item prop="biography">
            <el-input
              v-model="registerForm.biography"
              type="textarea"
              placeholder="個人簡介（選填）"
              :rows="2"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="register-button"
              :loading="authStore.loading"
              @click="handleRegister"
            >
              建立帳號
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 登入連結 -->
        <div class="form-footer">
          <p class="login-prompt">
            已經有帳號？
            <router-link to="/login" class="login-link">
              立即登入
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
import { ChatDotRound, User, Message, Lock, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import BrandLogo from '@/components/common/BrandLogo.vue'

const authStore = useAuthStore()
const registerFormRef = ref()

// 表單資料
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  biography: ''
})

// 自定義驗證器
const validateUsername = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('請輸入使用者名稱'))
    return
  }
  if (value.length < 3) {
    callback(new Error('使用者名稱至少 3 個字元'))
    return
  }
  if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('使用者名稱只能包含字母、數字和底線'))
    return
  }
  callback()
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error('請輸入電子郵件'))
    return
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(value)) {
    callback(new Error('請輸入有效的電子郵件格式'))
    return
  }
  callback()
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('請輸入密碼'))
    return
  }
  if (value.length < 6) {
    callback(new Error('密碼長度至少 6 個字元'))
    return
  }
  if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密碼必須包含字母和數字'))
    return
  }
  callback()
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('請確認密碼'))
    return
  }
  if (value !== registerForm.password) {
    callback(new Error('兩次輸入的密碼不一致'))
    return
  }
  callback()
}

// 表單驗證規則
const registerRules = {
  username: [{ validator: validateUsername, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

// 檢查使用者名稱可用性
const checkUsernameAvailability = async () => {
  if (registerForm.username && registerForm.username.length >= 3) {
    try {
      const available = await authStore.checkUsernameAvailability(registerForm.username)
      if (!available) {
        ElMessage.warning('使用者名稱已被使用')
      }
    } catch (error) {
      console.error('檢查使用者名稱失敗:', error)
    }
  }
}

// 檢查電子郵件可用性
const checkEmailAvailability = async () => {
  if (registerForm.email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    try {
      const available = await authStore.checkEmailAvailability(registerForm.email)
      if (!available) {
        ElMessage.warning('電子郵件已被使用')
      }
    } catch (error) {
      console.error('檢查電子郵件失敗:', error)
    }
  }
}

// 處理註冊
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    await authStore.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword,
      biography: registerForm.biography || '這個人很懶，什麼都沒有留下。'
    })
  } catch (error) {
    console.error('註冊失敗:', error)
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  background: var(--gradient-primary);
}

/* 左側品牌區域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.brand-content {
  text-align: center;
  z-index: 2;
  position: relative;
}

.brand-logo {
  margin-bottom: 40px;
  animation: fadeInUp 0.8s ease-out;
}

.brand-text {
  color: white;
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: fadeInUp 0.8s ease-out 0.2s both;
}

.brand-subtitle {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
  animation: fadeInUp 0.8s ease-out 0.4s both;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  animation: fadeInUp 0.8s ease-out 0.6s both;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 16px;
  opacity: 0.9;
}

.feature-item .el-icon {
  font-size: 20px;
}

/* 山峰背景裝飾 */
.mountain-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 200px;
  z-index: 1;
}

.mountain-svg {
  width: 100%;
  height: 100%;
}

/* 右側表單區域 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  padding: 60px;
}

.form-container {
  width: 100%;
  max-width: 400px;
  animation: fadeInRight 0.8s ease-out;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

.register-form {
  margin-bottom: 32px;
}

.register-form .el-form-item {
  margin-bottom: 24px;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: var(--gradient-primary);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(45, 155, 122, 0.3);
}

.form-footer {
  text-align: center;
}

.login-prompt {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.login-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
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

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 響應式設計 */
@media (max-width: 1024px) {
  .brand-title {
    font-size: 36px;
  }

  .brand-subtitle {
    font-size: 18px;
  }

  .form-title {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .register-page {
    flex-direction: column;
  }

  .brand-section {
    flex: none;
    min-height: 40vh;
    padding: 40px 20px;
  }

  .brand-title {
    font-size: 28px;
  }

  .brand-subtitle {
    font-size: 16px;
    margin-bottom: 30px;
  }

  .brand-features {
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
    gap: 15px;
  }

  .feature-item {
    font-size: 14px;
  }

  .form-section {
    flex: none;
    padding: 40px 20px;
  }

  .form-title {
    font-size: 24px;
  }

  .mountain-decoration {
    height: 120px;
  }
}

@media (max-width: 480px) {
  .brand-section {
    padding: 30px 16px;
  }

  .form-section {
    padding: 30px 16px;
  }

  .brand-title {
    font-size: 24px;
  }

  .form-title {
    font-size: 20px;
  }
}
</style>
