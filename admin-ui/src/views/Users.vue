<template>
  <div class="page-container">
    <div class="page-header">
      <h2>User Management</h2>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>User List</span>
          <el-button type="primary" size="small" @click="refreshUsers">Refresh</el-button>
        </div>
      </template>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="Username" width="150" />
        <el-table-column prop="email" label="Email" />
        <el-table-column prop="role" label="Role" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="Status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? 'Active' : 'Disabled' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created" width="180" />
        <el-table-column label="Actions" width="150">
          <template #default="{ row }">
            <el-button 
              :type="row.enabled ? 'danger' : 'success'" 
              size="small" 
              @click="toggleUser(row)"
            >
              {{ row.enabled ? 'Disable' : 'Enable' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([
  { id: 1, username: 'admin', email: 'admin@example.com', role: 'admin', enabled: true, createdAt: '2024-01-01 10:00:00' },
  { id: 2, username: 'user1', email: 'user1@example.com', role: 'user', enabled: true, createdAt: '2024-01-05 14:30:00' },
  { id: 3, username: 'user2', email: 'user2@example.com', role: 'user', enabled: false, createdAt: '2024-01-10 09:15:00' }
])

const loading = ref(false)

onMounted(async () => {
  await fetchUsers()
})

async function fetchUsers() {
  loading.value = true
  try {
    const response = await api.user.getUsers()
    users.value = response.users || []
  } catch (error) {
    console.log('Using default users')
  } finally {
    loading.value = false
  }
}

async function toggleUser(user) {
  const action = user.enabled ? 'disable' : 'enable'
  try {
    await ElMessageBox.confirm(
      `Are you sure you want to ${action} user "${user.username}"?`,
      'Confirm',
      {
        confirmButtonText: 'Yes',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )
    
    await api.user.toggleUser(user.id, !user.enabled)
    user.enabled = !user.enabled
    ElMessage.success(`User ${action}d successfully`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Operation failed')
    }
  }
}

function refreshUsers() {
  fetchUsers()
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
