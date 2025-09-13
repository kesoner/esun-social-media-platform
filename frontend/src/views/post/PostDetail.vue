<template>
  <div class="post-detail-container">
    <div class="container">
      <div class="post-detail-layout">
        <!-- 返回按鈕 -->
        <div class="page-header">
          <el-button
            @click="$router.back()"
            circle
            size="large"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <h1 class="page-title">發文詳情</h1>
        </div>

        <!-- 載入狀態 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 發文不存在 -->
        <div v-else-if="!post" class="not-found">
          <el-empty description="發文不存在或已被刪除">
            <el-button type="primary" @click="$router.push('/')">
              回到首頁
            </el-button>
          </el-empty>
        </div>

        <!-- 發文內容 -->
        <div v-else class="post-detail-content">
          <!-- 發文卡片 -->
          <div class="post-card">
            <PostCard
              :post="post"
              @delete="handlePostDelete"
            />
          </div>

          <!-- 留言區域 -->
          <div class="comments-section">
            <div class="comments-header">
              <h2 class="comments-title">
                留言 ({{ comments.length }})
              </h2>
            </div>

            <!-- 新增留言 -->
            <div class="add-comment">
              <div class="comment-form">
                <el-avatar :size="40" :src="userInfo.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="comment-input">
                  <el-input
                    v-model="newComment"
                    type="textarea"
                    placeholder="寫下您的留言..."
                    :rows="3"
                    maxlength="500"
                    show-word-limit
                  />
                  <div class="comment-actions">
                    <el-button
                      type="primary"
                      @click="handleSubmitComment"
                      :loading="submittingComment"
                      :disabled="!newComment.trim()"
                    >
                      發送留言
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 留言列表 -->
            <div class="comments-list">
              <div v-if="loadingComments" class="loading-comments">
                <el-skeleton :rows="2" animated />
                <el-skeleton :rows="2" animated />
                <el-skeleton :rows="2" animated />
              </div>

              <div v-else-if="comments.length === 0" class="no-comments">
                <el-empty description="還沒有留言，快來搶沙發吧！" />
              </div>

              <div v-else>
                <CommentItem
                  v-for="comment in comments"
                  :key="comment.id"
                  :comment="comment"
                  @delete="handleDeleteComment"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { postApi } from '@/api/post'
import { commentApi } from '@/api/comment'
import PostCard from '@/components/post/PostCard.vue'
import CommentItem from '@/components/comment/CommentItem.vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 響應式資料
const post = ref(null)
const comments = ref([])
const loading = ref(true)
const loadingComments = ref(false)
const newComment = ref('')
const submittingComment = ref(false)

// 計算屬性
const userInfo = computed(() => authStore.userInfo)
const postId = computed(() => route.params.id)

// 方法
const loadPost = async () => {
  try {
    loading.value = true
    const response = await postApi.getPostById(postId.value)
    post.value = response.data
  } catch (error) {
    console.error('載入發文失敗:', error)
    post.value = null
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    loadingComments.value = true
    const response = await commentApi.getCommentsByPostId(postId.value)
    comments.value = response.data || []
  } catch (error) {
    console.error('載入留言失敗:', error)
    comments.value = []
  } finally {
    loadingComments.value = false
  }
}

const handleSubmitComment = async () => {
  if (!newComment.value.trim()) return

  submittingComment.value = true
  try {
    const response = await commentApi.createComment(postId.value, {
      content: newComment.value.trim()
    })

    // 將新留言加到列表頂部
    comments.value.unshift(response.data)
    newComment.value = ''
    
    // 更新發文的留言數量
    if (post.value) {
      post.value.commentCount = (post.value.commentCount || 0) + 1
    }

    ElMessage.success('留言發送成功')
  } catch (error) {
    console.error('發送留言失敗:', error)
    ElMessage.error('發送留言失敗，請重試')
  } finally {
    submittingComment.value = false
  }
}

const handleDeleteComment = (commentId) => {
  comments.value = comments.value.filter(comment => comment.id !== commentId)
  
  // 更新發文的留言數量
  if (post.value) {
    post.value.commentCount = Math.max((post.value.commentCount || 1) - 1, 0)
  }
}

const handlePostDelete = () => {
  // 發文被刪除，返回首頁
  ElMessage.success('發文已刪除')
  router.push('/')
}

// 生命週期
onMounted(async () => {
  await loadPost()
  if (post.value) {
    await loadComments()
  }
})
</script>

<style scoped>
.post-detail-container {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.post-detail-layout {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.loading-container {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.not-found {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 60px 24px;
  text-align: center;
  box-shadow: var(--el-box-shadow-light);
}

.post-detail-content {
  space-y: 24px;
}

.post-card {
  background: var(--el-bg-color);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--el-box-shadow-light);
}

.comments-section {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
  margin-top: 24px;
}

.comments-header {
  margin-bottom: 24px;
}

.comments-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.add-comment {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.comment-form {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.comment-input {
  flex: 1;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.comments-list {
  space-y: 16px;
}

.loading-comments {
  space-y: 16px;
}

.no-comments {
  text-align: center;
  padding: 40px 20px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .post-detail-layout {
    padding: 16px;
  }
  
  .page-header {
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .comments-section {
    padding: 20px;
  }
  
  .comment-form {
    flex-direction: column;
    gap: 12px;
  }
  
  .comment-actions {
    justify-content: stretch;
  }
  
  .comment-actions .el-button {
    width: 100%;
  }
}
</style>
