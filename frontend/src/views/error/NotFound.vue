<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-illustration">
        <el-icon size="120" class="error-icon">
          <DocumentDelete />
        </el-icon>
      </div>
      
      <div class="error-info">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">頁面不存在</h2>
        <p class="error-description">
          抱歉，您訪問的頁面不存在或已被移除。
        </p>
        
        <div class="error-actions">
          <el-button
            type="primary"
            size="large"
            @click="goHome"
          >
            回到首頁
          </el-button>
          
          <el-button
            size="large"
            @click="goBack"
          >
            返回上頁
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { DocumentDelete } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.not-found-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.not-found-content {
  text-align: center;
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 60px 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.error-illustration {
  margin-bottom: 32px;
}

.error-icon {
  color: var(--el-color-info);
}

.error-code {
  font-size: 72px;
  font-weight: 700;
  color: var(--el-color-primary);
  margin: 0 0 16px;
  line-height: 1;
}

.error-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 16px;
}

.error-description {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0 0 32px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* 響應式設計 */
@media (max-width: 480px) {
  .not-found-content {
    padding: 40px 24px;
  }
  
  .error-code {
    font-size: 60px;
  }
  
  .error-title {
    font-size: 20px;
  }
  
  .error-actions {
    flex-direction: column;
  }
}
</style>
