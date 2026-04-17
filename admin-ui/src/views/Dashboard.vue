<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $t('nav.dashboard') }}</h2>
    </div>

    <div class="card-grid">
      <el-card class="status-card" v-for="service in services" :key="service.name">
        <template #header>
          <div class="card-header">
            <span class="service-name">{{ service.name }}</span>
            <span :class="['status-dot', service.online ? 'online' : 'offline']"></span>
          </div>
        </template>
        <div class="service-content">
          <div class="service-status">{{ service.online ? '在线' : '离线' }}</div>
          <div class="service-detail">{{ service.detail }}</div>
        </div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/desensitize')">
              <el-icon><Hide /></el-icon>
              数据脱敏
            </el-button>
            <el-button type="success" @click="$router.push('/watermark')">
              <el-icon><Picture /></el-icon>
              数字水印
            </el-button>
            <el-button type="warning" @click="$router.push('/encryption')">
              <el-icon><Lock /></el-icon>
              数据加密
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近动态</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in activities"
              :key="activity.id"
              :timestamp="activity.timestamp"
              placement="top"
            >
              <el-card>
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Hide, Picture, Lock } from '@element-plus/icons-vue'

const services = ref([
  { name: '用户服务', online: true, detail: '端口 8080' },
  { name: '脱敏服务', online: true, detail: '端口 8081' },
  { name: '水印服务', online: true, detail: '端口 8082' },
  { name: '加密服务', online: true, detail: '端口 8083' }
])

const activities = ref([
  { id: 1, title: '用户登录', description: '管理员登录成功', timestamp: '2024-01-15 10:30:00' },
  { id: 2, title: '脱敏规则更新', description: '手机号脱敏规则已修改', timestamp: '2024-01-15 09:15:00' },
  { id: 3, title: '加密密钥生成', description: '新的 AES-256 密钥已创建', timestamp: '2024-01-14 16:45:00' },
  { id: 4, title: '水印嵌入', description: '文本水印嵌入成功', timestamp: '2024-01-14 14:20:00' }
])
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.service-name {
  font-size: 16px;
  font-weight: 500;
}

.service-content {
  text-align: center;
  padding: 10px 0;
}

.service-status {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 10px;
}

.service-detail {
  font-size: 14px;
  color: #909399;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-actions .el-button {
  justify-content: flex-start;
}

.activity-card {
  height: 100%;
}

.activity-card h4 {
  margin: 0 0 5px;
  font-size: 14px;
  font-weight: 500;
}

.activity-card p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}
</style>
