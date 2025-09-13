<template>
  <div class="comment-item">
    <div class="comment-header">
      <div class="author-info">
        <el-avatar :size="28" :src="comment.author.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
        <div class="author-details">
          <router-link
            :to="`/user/${comment.author.id}`"
            class="author-name"
          >
            {{ comment.author.username }}
          </router-link>
          <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
        </div>
      </div>
      
      <!-- 刪除按鈕 -->
      <el-button
        v-if="canDelete"
        size="small"
        text
        @click="handleDelete"
      >
        <el-icon><Delete /></el-icon>
      </el-button>
    </div>

    <div class="comment-content">
      <p class="comment-text">{{ comment.content }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { commentApi } from '@/api/comment'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-tw'
import { User, Delete } from '@element-plus/icons-vue'

dayjs.extend(relativeTime)
dayjs.locale('zh-tw')

const props = defineProps({
  comment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['delete'])

const authStore = useAuthStore()

// 計算屬性
const currentUser = computed(() => authStore.userInfo)
const canDelete = computed(() => {
  return currentUser.value.id === props.comment.author.id
})

// 方法
const formatTime = (time) => {
  return dayjs(time).fromNow()
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '確定要刪除這則留言嗎？',
      '確認刪除',
      {
        confirmButtonText: '刪除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await commentApi.deleteComment(props.comment.id)
    ElMessage.success('留言已刪除')
    emit('delete', props.comment.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('刪除留言失敗:', error)
    }
  }
}
</script>

<style scoped>
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-extra-light);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
  text-decoration: none;
  font-size: 13px;
}

.author-name:hover {
  color: var(--el-color-primary);
}

.comment-time {
  font-size: 11px;
  color: var(--el-text-color-secondary);
  margin-top: 1px;
}

.comment-content {
  margin-left: 36px;
}

.comment-text {
  margin: 0;
  line-height: 1.5;
  color: var(--el-text-color-primary);
  font-size: 14px;
  white-space: pre-wrap;
}
</style>
