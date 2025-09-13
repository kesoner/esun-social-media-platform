<template>
  <div class="profile-container">
    <div class="container">
      <div class="profile-layout">
        <!-- 個人資料卡片 -->
        <div class="profile-card">
          <div class="profile-header">
            <div class="avatar-section">
              <el-avatar :size="120" :src="userInfo.avatar">
                <el-icon size="60"><User /></el-icon>
              </el-avatar>
              <el-button
                type="primary"
                size="small"
                @click="showEditDialog = true"
                class="edit-button"
              >
                編輯資料
              </el-button>
            </div>
            
            <div class="profile-info">
              <h1 class="username">{{ userInfo.username }}</h1>
              <p class="email">{{ userInfo.email }}</p>
              <p class="biography">{{ userInfo.biography || '這個人很懶，什麼都沒有留下。' }}</p>
              
              <div class="stats">
                <div class="stat-item">
                  <span class="stat-number">{{ userInfo.postCount || 0 }}</span>
                  <span class="stat-label">發文</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userInfo.commentCount || 0 }}</span>
                  <span class="stat-label">留言</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ formatDate(userInfo.createdAt) }}</span>
                  <span class="stat-label">加入時間</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 發文列表 -->
        <div class="posts-section">
          <div class="section-header">
            <h2>我的發文</h2>
            <el-button
              type="primary"
              @click="$router.push('/create-post')"
            >
              建立發文
            </el-button>
          </div>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
            <el-skeleton :rows="3" animated />
          </div>
          
          <div v-else-if="posts.length === 0" class="empty-state">
            <el-empty description="還沒有發文">
              <el-button type="primary" @click="$router.push('/create-post')">
                建立第一篇發文
              </el-button>
            </el-empty>
          </div>
          
          <div v-else class="posts-list">
            <PostCard
              v-for="post in posts"
              :key="post.id"
              :post="post"
              @delete="handleDeletePost"
            />
            
            <!-- 載入更多 -->
            <div class="load-more" v-if="hasMore">
              <el-button
                @click="loadMorePosts"
                :loading="loadingMore"
                size="large"
              >
                載入更多
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 編輯資料對話框 -->
    <el-dialog
      v-model="showEditDialog"
      title="編輯個人資料"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="使用者名稱" prop="username">
          <el-input
            v-model="editForm.username"
            placeholder="請輸入使用者名稱"
          />
        </el-form-item>
        
        <el-form-item label="電子郵件" prop="email">
          <el-input
            v-model="editForm.email"
            placeholder="請輸入電子郵件"
            disabled
          />
        </el-form-item>
        
        <el-form-item label="個人簡介" prop="biography">
          <el-input
            v-model="editForm.biography"
            type="textarea"
            :rows="4"
            placeholder="介紹一下自己..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleUpdateProfile"
          :loading="updating"
        >
          儲存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { postApi } from '@/api/post'
import PostCard from '@/components/post/PostCard.vue'
import dayjs from 'dayjs'
import { User } from '@element-plus/icons-vue'

const authStore = useAuthStore()

// 響應式資料
const posts = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(0)
const showEditDialog = ref(false)
const updating = ref(false)
const editFormRef = ref()

// 編輯表單
const editForm = reactive({
  username: '',
  email: '',
  biography: ''
})

// 表單驗證規則
const editRules = {
  username: [
    { required: true, message: '請輸入使用者名稱', trigger: 'blur' },
    { min: 3, message: '使用者名稱至少 3 個字元', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '使用者名稱只能包含字母、數字和底線', trigger: 'blur' }
  ],
  biography: [
    { max: 200, message: '個人簡介不能超過 200 字元', trigger: 'blur' }
  ]
}

// 計算屬性
const userInfo = computed(() => authStore.userInfo)

// 方法
const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月')
}

const loadPosts = async (reset = false) => {
  if (reset) {
    currentPage.value = 0
    hasMore.value = true
    posts.value = []
  }

  loading.value = reset
  loadingMore.value = !reset

  try {
    const response = await postApi.getPostsByAuthor(userInfo.value.id, {
      page: currentPage.value,
      size: 10
    })

    const newPosts = response.data.content || []
    
    if (reset) {
      posts.value = newPosts
    } else {
      posts.value.push(...newPosts)
    }

    hasMore.value = !response.data.last
    currentPage.value++
  } catch (error) {
    console.error('載入發文失敗:', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMorePosts = () => {
  if (!loadingMore.value && hasMore.value) {
    loadPosts(false)
  }
}

const handleDeletePost = (postId) => {
  posts.value = posts.value.filter(post => post.id !== postId)
}

const openEditDialog = () => {
  editForm.username = userInfo.value.username
  editForm.email = userInfo.value.email
  editForm.biography = userInfo.value.biography || ''
  showEditDialog.value = true
}

const handleUpdateProfile = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    
    updating.value = true
    await authStore.updateProfile({
      username: editForm.username,
      biography: editForm.biography
    })
    
    showEditDialog.value = false
  } catch (error) {
    console.error('更新資料失敗:', error)
  } finally {
    updating.value = false
  }
}

// 生命週期
onMounted(() => {
  loadPosts(true)
})

// 監聽編輯對話框開啟
watch(() => showEditDialog.value, (newVal) => {
  if (newVal) {
    openEditDialog()
  }
})
</script>

<style scoped>
.profile-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.profile-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-card {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.profile-header {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.edit-button {
  width: 120px;
}

.profile-info {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--el-text-color-primary);
}

.email {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0 0 16px;
}

.biography {
  font-size: 16px;
  line-height: 1.6;
  color: var(--el-text-color-primary);
  margin: 0 0 24px;
}

.stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.stat-number {
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.posts-section {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.loading-container {
  space-y: 16px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.posts-list {
  space-y: 16px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .profile-layout {
    padding: 16px;
  }
  
  .profile-card {
    padding: 24px;
  }
  
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 24px;
  }
  
  .username {
    font-size: 24px;
  }
  
  .stats {
    justify-content: center;
    gap: 24px;
  }
  
  .posts-section {
    padding: 20px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
}
</style>
