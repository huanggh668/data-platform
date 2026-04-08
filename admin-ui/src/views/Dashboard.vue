<template>
  <div class="page-container">
    <div class="page-header">
      <h2>Dashboard</h2>
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
          <div class="service-status">{{ service.online ? 'Online' : 'Offline' }}</div>
          <div class="service-detail">{{ service.detail }}</div>
        </div>
      </el-card>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>Quick Actions</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/desensitize')">
              <el-icon><Hide /></el-icon>
              Desensitization
            </el-button>
            <el-button type="success" @click="$router.push('/watermark')">
              <el-icon><Picture /></el-icon>
              Watermark
            </el-button>
            <el-button type="warning" @click="$router.push('/encryption')">
              <el-icon><Lock /></el-icon>
              Encryption
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>Recent Activity</span>
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
  { name: 'User Service', online: true, detail: 'Port 8080' },
  { name: 'Desensitization', online: true, detail: 'Port 8081' },
  { name: 'Watermark Service', online: true, detail: 'Port 8082' },
  { name: 'Encryption Service', online: true, detail: 'Port 8083' }
])

const activities = ref([
  { id: 1, title: 'User Login', description: 'Admin logged in successfully', timestamp: '2024-01-15 10:30:00' },
  { id: 2, title: 'Desensitization Rule Updated', description: 'Rule for phone numbers was modified', timestamp: '2024-01-15 09:15:00' },
  { id: 3, title: 'Encryption Key Generated', description: 'New AES-256 key created', timestamp: '2024-01-14 16:45:00' },
  { id: 4, title: 'Watermark Embedded', description: 'Text watermark embedded successfully', timestamp: '2024-01-14 14:20:00' }
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
