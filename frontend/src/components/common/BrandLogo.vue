<template>
  <div class="brand-logo" :class="{ [`brand-logo--${size}`]: size }">
    <div class="logo-container">
      <!-- 玉山 LOGO SVG -->
      <svg 
        class="logo-icon" 
        viewBox="0 0 100 100" 
        xmlns="http://www.w3.org/2000/svg"
      >
        <!-- 圓形背景 -->
        <circle 
          cx="50" 
          cy="50" 
          r="50" 
          :fill="backgroundColor"
        />
        
        <!-- 山峰圖案 -->
        <g class="mountain-group">
          <!-- 主山峰 -->
          <path 
            d="M20 70 L35 45 L50 55 L65 35 L80 70 Z" 
            :fill="mountainColor"
            class="main-mountain"
          />
          
          <!-- 雪峰效果 -->
          <path 
            d="M20 70 L35 45 L42 50 L35 55 L50 55 L57 50 L65 35 L72 40 L65 45 L80 70 Z" 
            :fill="snowColor"
            class="snow-peaks"
          />
          
          <!-- 山脈細節 -->
          <path 
            d="M35 45 L42 52 L50 55 L57 52 L65 35" 
            stroke="rgba(255,255,255,0.3)" 
            stroke-width="1" 
            fill="none"
            class="mountain-detail"
          />
        </g>
      </svg>
      
      <!-- 品牌文字 -->
      <div v-if="showText" class="brand-text">
        <h1 class="brand-title">玉山銀行</h1>
        <p class="brand-subtitle">社群媒體平台</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large', 'xlarge'].includes(value)
  },
  showText: {
    type: Boolean,
    default: true
  },
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'white', 'dark'].includes(value)
  }
})

const backgroundColor = computed(() => {
  switch (props.variant) {
    case 'white':
      return '#FFFFFF'
    case 'dark':
      return '#1A5F4F'
    default:
      return '#2D9B7A'
  }
})

const mountainColor = computed(() => {
  switch (props.variant) {
    case 'white':
      return '#2D9B7A'
    case 'dark':
      return '#FFFFFF'
    default:
      return '#FFFFFF'
  }
})

const snowColor = computed(() => {
  switch (props.variant) {
    case 'white':
      return 'rgba(45, 155, 122, 0.1)'
    case 'dark':
      return 'rgba(255, 255, 255, 0.2)'
    default:
      return 'rgba(255, 255, 255, 0.3)'
  }
})
</script>

<style scoped>
.brand-logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.logo-icon {
  transition: var(--transition-base);
}

.logo-icon:hover {
  transform: scale(1.05);
}

.mountain-group {
  transition: var(--transition-base);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.brand-title {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.2;
  letter-spacing: 0.5px;
}

.brand-subtitle {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.2;
  font-weight: 400;
}

/* 尺寸變體 */
.brand-logo--small .logo-icon {
  width: 32px;
  height: 32px;
}

.brand-logo--small .brand-title {
  font-size: var(--font-size-base);
}

.brand-logo--small .brand-subtitle {
  font-size: var(--font-size-xs);
}

.brand-logo--medium .logo-icon {
  width: 48px;
  height: 48px;
}

.brand-logo--large .logo-icon {
  width: 64px;
  height: 64px;
}

.brand-logo--large .brand-title {
  font-size: var(--font-size-xxl);
}

.brand-logo--large .brand-subtitle {
  font-size: var(--font-size-base);
}

.brand-logo--xlarge .logo-icon {
  width: 80px;
  height: 80px;
}

.brand-logo--xlarge .brand-title {
  font-size: var(--font-size-title);
}

.brand-logo--xlarge .brand-subtitle {
  font-size: var(--font-size-lg);
}

/* 響應式設計 */
@media (max-width: 768px) {
  .brand-logo {
    gap: var(--spacing-sm);
  }
  
  .logo-container {
    gap: var(--spacing-sm);
  }
  
  .brand-title {
    font-size: var(--font-size-lg);
  }
  
  .brand-subtitle {
    font-size: var(--font-size-xs);
  }
}
</style>
