<template>
  <div class="post-card">
    <!-- 發文頭部 -->
    <div class="post-header">
      <div class="author-info">
        <el-avatar :size="40" :src="post.author.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
        <div class="author-details">
          <router-link
            :to="`/user/${post.author.id}`"
            class="author-name"
          >
            {{ post.author.username }}
          </router-link>
          <span class="post-time">{{ formatTime(post.createdAt) }}</span>
        </div>
      </div>
      
      <!-- 更多選項 -->
      <el-dropdown
        v-if="canEdit || canDelete"
        @command="handleCommand"
        trigger="click"
      >
        <el-button circle size="small">
          <el-icon><MoreFilled /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-if="canEdit"
              command="edit"
            >
              <el-icon><Edit /></el-icon>
              編輯
            </el-dropdown-item>
            <el-dropdown-item
              v-if="canDelete"
              command="delete"
              divided
            >
              <el-icon><Delete /></el-icon>
              刪除
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 發文內容 -->
    <div class="post-content">
      <p class="post-text">{{ post.content }}</p>
      
      <!-- 圖片 -->
      <div v-if="post.image" class="post-image">
        <el-image
          :src="post.image"
          fit="cover"
          :preview-src-list="[post.image]"
          class="image"
        />
      </div>
    </div>

    <!-- 發文底部操作 -->
    <div class="post-actions">
      <div class="action-buttons">
        <!-- 留言按鈕 -->
        <el-button
          text
          @click="handleComment"
          class="action-button"
        >
          <el-icon><ChatDotRound /></el-icon>
          <span>{{ post.commentCount || 0 }}</span>
        </el-button>

        <!-- 分享按鈕 -->
        <el-button
          text
          @click="handleShare"
          class="action-button"
        >
          <el-icon><Share /></el-icon>
          <span>分享</span>
        </el-button>
      </div>
    </div>

    <!-- 留言預覽 -->
    <div v-if="showComments && comments.length > 0" class="comments-preview">
      <div class="comments-header">
        <span class="comments-title">留言 ({{ post.commentCount }})</span>
        <el-button
          text
          size="small"
          @click="toggleComments"
        >
          {{ showAllComments ? '收起' : '查看全部' }}
        </el-button>
      </div>
      
      <div class="comments-list">
        <CommentItem
          v-for="comment in displayComments"
          :key="comment.id"
          :comment="comment"
          @delete="handleDeleteComment"
        />
      </div>
    </div>

    <!-- 新增留言 -->
    <div v-if="showCommentInput" class="comment-input">
      <div class="input-container">
        <el-avatar :size="32" :src="currentUser.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
        <el-input
          v-model="newComment"
          placeholder="寫下您的留言..."
          @keyup.enter="handleSubmitComment"
          class="comment-field"
        />
        <el-button
          type="primary"
          size="small"
          @click="handleSubmitComment"
          :loading="submittingComment"
        >
          發送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { postApi } from '@/api/post'
import { commentApi } from '@/api/comment'
import CommentItem from '@/components/comment/CommentItem.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-tw'
import {
  User,
  MoreFilled,
  Edit,
  Delete,
  ChatDotRound,
  Share
} from '@element-plus/icons-vue'

dayjs.extend(relativeTime)
dayjs.locale('zh-tw')

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['comment', 'delete'])

const authStore = useAuthStore()

// 響應式資料
const comments = ref([])
const showComments = ref(false)
const showAllComments = ref(false)
const showCommentInput = ref(false)
const newComment = ref('')
const submittingComment = ref(false)

// 計算屬性
const currentUser = computed(() => authStore.userInfo)
const canEdit = computed(() => {
  return currentUser.value.id === props.post.author.id
})
const canDelete = computed(() => {
  return currentUser.value.id === props.post.author.id
})
const displayComments = computed(() => {
  return showAllComments.value ? comments.value : comments.value.slice(0, 3)
})

// 方法
const formatTime = (time) => {
  return dayjs(time).fromNow()
}

const handleCommand = async (command) => {
  switch (command) {
    case 'edit':
      // TODO: 實作編輯功能
      ElMessage.info('編輯功能開發中')
      break
    case 'delete':
      await handleDelete()
      break
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '確定要刪除這篇發文嗎？此操作無法復原。',
      '確認刪除',
      {
        confirmButtonText: '刪除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await postApi.deletePost(props.post.id)
    ElMessage.success('發文已刪除')
    emit('delete', props.post.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('刪除發文失敗:', error)
    }
  }
}

const handleComment = () => {
  showCommentInput.value = !showCommentInput.value
  if (showCommentInput.value && !showComments.value) {
    loadComments()
  }
  emit('comment', props.post.id)
}

const handleShare = async () => {
  try {
    if (navigator.share) {
      await navigator.share({
        title: `${props.post.author.username} 的發文`,
        text: props.post.content,
        url: window.location.href
      })
    } else {
      // 複製連結到剪貼簿
      await navigator.clipboard.writeText(window.location.href)
      ElMessage.success('連結已複製到剪貼簿')
    }
  } catch (error) {
    console.error('分享失敗:', error)
  }
}

const loadComments = async () => {
  try {
    const response = await commentApi.getCommentsByPostId(props.post.id)
    comments.value = response.data
    showComments.value = true
  } catch (error) {
    console.error('載入留言失敗:', error)
  }
}

const toggleComments = () => {
  showAllComments.value = !showAllComments.value
}

const handleSubmitComment = async () => {
  if (!newComment.value.trim()) return
  
  submittingComment.value = true
  try {
    const response = await commentApi.createComment(props.post.id, {
      content: newComment.value.trim()
    })
    
    comments.value.push(response.data)
    newComment.value = ''
    ElMessage.success('留言發送成功')
    
    // 更新發文的留言數量
    props.post.commentCount = (props.post.commentCount || 0) + 1
  } catch (error) {
    console.error('發送留言失敗:', error)
  } finally {
    submittingComment.value = false
  }
}

const handleDeleteComment = (commentId) => {
  comments.value = comments.value.filter(comment => comment.id !== commentId)
  props.post.commentCount = Math.max((props.post.commentCount || 1) - 1, 0)
}
</script>

<style scoped>
.post-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--el-box-shadow-light);
  transition: box-shadow 0.3s;
}

.post-card:hover {
  box-shadow: var(--el-box-shadow-base);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
  text-decoration: none;
  font-size: 14px;
}

.author-name:hover {
  color: var(--el-color-primary);
}

.post-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 2px;
}

.post-content {
  margin-bottom: 16px;
}

.post-text {
  margin: 0 0 12px;
  line-height: 1.6;
  color: var(--el-text-color-primary);
  white-space: pre-wrap;
}

.post-image {
  border-radius: 8px;
  overflow: hidden;
}

.image {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
}

.post-actions {
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 12px;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.action-button:hover {
  color: var(--el-color-primary);
}

.comments-preview {
  margin-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 16px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comments-title {
  font-weight: 600;
  font-size: 14px;
}

.comments-list {
  space-y: 12px;
}

.comment-input {
  margin-top: 16px;
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 16px;
}

.input-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.comment-field {
  flex: 1;
}
</style>
