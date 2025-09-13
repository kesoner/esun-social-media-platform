<template>
  <div class="test-auth">
    <div class="container">
      <h1>認證狀態測試</h1>
      
      <div class="auth-status">
        <h2>當前認證狀態</h2>
        <div class="status-item">
          <strong>isAuthenticated:</strong> {{ authStore.isAuthenticated }}
        </div>
        <div class="status-item">
          <strong>accessToken:</strong> {{ authStore.accessToken ? '存在' : '不存在' }}
        </div>
        <div class="status-item">
          <strong>refreshToken:</strong> {{ authStore.refreshToken ? '存在' : '不存在' }}
        </div>
        <div class="status-item">
          <strong>user:</strong> {{ authStore.user ? authStore.user.username : '不存在' }}
        </div>
        <div class="status-item">
          <strong>loading:</strong> {{ authStore.loading }}
        </div>
      </div>

      <div class="test-actions">
        <h2>測試操作</h2>
        <el-button @click="testLogin" :loading="loginLoading">測試登入</el-button>
        <el-button @click="testProfile" :loading="profileLoading">測試獲取資料</el-button>
        <el-button @click="clearAuth">清除認證</el-button>
        <el-button @click="goHome">前往首頁</el-button>
        <el-button @click="goLogin">前往登入頁</el-button>
      </div>

      <div class="test-results" v-if="testResult">
        <h2>測試結果</h2>
        <pre>{{ testResult }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const loginLoading = ref(false)
const profileLoading = ref(false)
const testResult = ref('')

const testLogin = async () => {
  loginLoading.value = true
  testResult.value = ''
  
  try {
    const result = await authStore.login({
      usernameOrEmail: 'testuser',
      password: 'Test123456'
    })
    
    testResult.value = JSON.stringify(result, null, 2)
  } catch (error) {
    testResult.value = `登入失敗: ${error.message}`
  } finally {
    loginLoading.value = false
  }
}

const testProfile = async () => {
  profileLoading.value = true
  testResult.value = ''
  
  try {
    const result = await authStore.fetchUserProfile()
    testResult.value = JSON.stringify(result, null, 2)
  } catch (error) {
    testResult.value = `獲取資料失敗: ${error.message}`
  } finally {
    profileLoading.value = false
  }
}

const clearAuth = () => {
  authStore.clearTokens()
  testResult.value = '認證狀態已清除'
}

const goHome = () => {
  router.push('/')
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.test-auth {
  min-height: 100vh;
  padding: 20px;
  background: #f5f5f5;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.auth-status {
  margin: 20px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.status-item {
  margin: 10px 0;
  font-family: monospace;
}

.test-actions {
  margin: 20px 0;
}

.test-actions .el-button {
  margin: 5px;
}

.test-results {
  margin: 20px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.test-results pre {
  background: #2d3748;
  color: #e2e8f0;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
}
</style>
