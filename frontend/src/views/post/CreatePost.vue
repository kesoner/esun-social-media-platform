<template>
  <div class="create-post-container">
    <div class="container">
      <div class="create-post-card">
        <!-- 頁面標題 -->
        <div class="page-header">
          <el-button
            @click="$router.back()"
            circle
            size="large"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <h1 class="page-title">建立發文</h1>
          <div class="header-actions">
            <el-button
              type="primary"
              @click="handleSubmit"
              :loading="submitting"
              :disabled="!canSubmit"
            >
              發布
            </el-button>
          </div>
        </div>

        <!-- 發文表單 -->
        <div class="post-form">
          <!-- 使用者資訊 -->
          <div class="author-info">
            <el-avatar :size="48" :src="userInfo.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="author-details">
              <p class="author-name">{{ userInfo.username }}</p>
              <p class="post-visibility">公開發文</p>
            </div>
          </div>

          <!-- 內容輸入 -->
          <div class="content-input">
            <el-input
              v-model="postContent"
              type="textarea"
              placeholder="分享您的想法..."
              :rows="8"
              :maxlength="5000"
              show-word-limit
              resize="none"
              class="content-textarea"
            />
          </div>

          <!-- 圖片上傳 -->
          <div class="image-upload" v-if="showImageUpload">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              @change="handleImageChange"
            >
              <div class="upload-area">
                <el-icon size="48" class="upload-icon">
                  <Plus />
                </el-icon>
                <p class="upload-text">點擊上傳圖片</p>
                <p class="upload-hint">支援 JPG、PNG 格式，最大 5MB</p>
              </div>
            </el-upload>
          </div>

          <!-- 圖片預覽 -->
          <div v-if="imagePreview" class="image-preview">
            <div class="preview-container">
              <img :src="imagePreview" alt="預覽圖片" class="preview-image" />
              <el-button
                @click="removeImage"
                circle
                size="small"
                class="remove-button"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- 工具欄 -->
          <div class="toolbar">
            <div class="toolbar-left">
              <el-button
                @click="toggleImageUpload"
                text
                :type="showImageUpload ? 'primary' : 'default'"
              >
                <el-icon><Picture /></el-icon>
                圖片
              </el-button>
              
              <el-button text disabled>
                <el-icon><VideoCamera /></el-icon>
                影片
              </el-button>
              
              <el-button text disabled>
                <el-icon><Location /></el-icon>
                位置
              </el-button>
            </div>
            
            <div class="toolbar-right">
              <span class="char-count" :class="{ 'over-limit': isOverLimit }">
                {{ postContent.length }}/5000
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { postApi } from '@/api/post'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  User,
  Plus,
  Close,
  Picture,
  VideoCamera,
  Location
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

// 響應式資料
const postContent = ref('')
const showImageUpload = ref(false)
const imageFile = ref(null)
const imagePreview = ref('')
const submitting = ref(false)
const uploadRef = ref()

// 計算屬性
const userInfo = computed(() => authStore.userInfo)
const canSubmit = computed(() => {
  return postContent.value.trim().length > 0 && !isOverLimit.value
})
const isOverLimit = computed(() => {
  return postContent.value.length > 5000
})

// 方法
const toggleImageUpload = () => {
  showImageUpload.value = !showImageUpload.value
  if (!showImageUpload.value) {
    removeImage()
  }
}

const handleImageChange = (file) => {
  const rawFile = file.raw
  
  // 檢查檔案大小
  if (rawFile.size > 5 * 1024 * 1024) {
    ElMessage.error('圖片大小不能超過 5MB')
    return
  }
  
  // 檢查檔案類型
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.error('只能上傳圖片檔案')
    return
  }
  
  imageFile.value = rawFile
  
  // 建立預覽
  const reader = new FileReader()
  reader.onload = (e) => {
    imagePreview.value = e.target.result
  }
  reader.readAsDataURL(rawFile)
}

const removeImage = () => {
  imageFile.value = null
  imagePreview.value = ''
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const handleSubmit = async () => {
  if (!canSubmit.value) return
  
  submitting.value = true
  
  try {
    const postData = {
      content: postContent.value.trim()
    }
    
    // TODO: 處理圖片上傳
    if (imageFile.value) {
      // 這裡應該先上傳圖片到伺服器，然後獲取圖片 URL
      // postData.image = uploadedImageUrl
      ElMessage.warning('圖片上傳功能開發中')
    }
    
    await postApi.createPost(postData)
    ElMessage.success('發文成功')
    router.push('/')
  } catch (error) {
    console.error('發文失敗:', error)
    ElMessage.error('發文失敗，請重試')
  } finally {
    submitting.value = false
  }
}

// 快捷鍵支援
const handleKeydown = (event) => {
  // Ctrl/Cmd + Enter 發布
  if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
    handleSubmit()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.create-post-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
  padding: 20px 0;
}

.create-post-card {
  max-width: 600px;
  margin: 0 auto;
  background: var(--el-bg-color);
  border-radius: 16px;
  box-shadow: var(--el-box-shadow-light);
  overflow: hidden;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  flex: 1;
  text-align: center;
}

.header-actions {
  width: 80px;
  display: flex;
  justify-content: flex-end;
}

.post-form {
  padding: 24px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.author-details {
  flex: 1;
}

.author-name {
  margin: 0 0 4px;
  font-weight: 600;
  font-size: 16px;
}

.post-visibility {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.content-input {
  margin-bottom: 20px;
}

.content-textarea :deep(.el-textarea__inner) {
  border: none;
  box-shadow: none;
  font-size: 16px;
  line-height: 1.6;
  padding: 0;
  resize: none;
}

.content-textarea :deep(.el-textarea__inner):focus {
  box-shadow: none;
}

.image-upload {
  margin-bottom: 20px;
}

.upload-area {
  border: 2px dashed var(--el-border-color);
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: var(--el-color-primary);
}

.upload-icon {
  color: var(--el-text-color-secondary);
  margin-bottom: 12px;
}

.upload-text {
  margin: 0 0 8px;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.upload-hint {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.image-preview {
  margin-bottom: 20px;
}

.preview-container {
  position: relative;
  display: inline-block;
  border-radius: 8px;
  overflow: hidden;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  display: block;
}

.remove-button {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.toolbar-left {
  display: flex;
  gap: 8px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.char-count {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.char-count.over-limit {
  color: var(--el-color-danger);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .create-post-container {
    padding: 0;
  }
  
  .create-post-card {
    border-radius: 0;
    min-height: 100vh;
  }
  
  .page-header {
    padding: 16px 20px;
  }
  
  .post-form {
    padding: 20px;
  }
  
  .page-title {
    font-size: 18px;
  }
}
</style>
