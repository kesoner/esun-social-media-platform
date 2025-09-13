<template>
  <div class="auth-debug" v-if="showDebug">
    <div class="debug-panel">
      <h4>認證狀態調試</h4>
      <div class="debug-info">
        <p><strong>isAuthenticated:</strong> {{ authStore.isAuthenticated }}</p>
        <p><strong>accessToken:</strong> {{ authStore.accessToken ? '存在' : '不存在' }}</p>
        <p><strong>refreshToken:</strong> {{ authStore.refreshToken ? '存在' : '不存在' }}</p>
        <p><strong>user:</strong> {{ authStore.user ? authStore.user.username : '不存在' }}</p>
        <p><strong>loading:</strong> {{ authStore.loading }}</p>
      </div>
      <div class="debug-actions">
        <el-button size="small" @click="checkAuth">檢查認證</el-button>
        <el-button size="small" @click="clearAuth">清除認證</el-button>
        <el-button size="small" @click="goHome">前往首頁</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const showDebug = computed(() => {
  return process.env.NODE_ENV === 'development'
})

const checkAuth = () => {
  console.log('當前認證狀態:', {
    isAuthenticated: authStore.isAuthenticated,
    accessToken: authStore.accessToken,
    refreshToken: authStore.refreshToken,
    user: authStore.user
  })
}

const clearAuth = () => {
  authStore.clearTokens()
  console.log('已清除認證狀態')
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.auth-debug {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.debug-panel {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 16px;
  border-radius: 8px;
  font-size: 12px;
  min-width: 250px;
}

.debug-panel h4 {
  margin: 0 0 12px 0;
  color: #4CAF50;
}

.debug-info p {
  margin: 4px 0;
  font-family: monospace;
}

.debug-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.debug-actions .el-button {
  font-size: 11px;
  padding: 4px 8px;
}
</style>
