<template>
  <div class="page-container">
    <div class="page-header">
      <h2>数据源管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据源列表</span>
              <el-button type="primary" size="small" @click="addItem">新建</el-button>
            </div>
          </template>
          <el-table :data="items" stripe>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="config" label="配置预览">
              <template #default="{ row }">
                <span class="config-preview">{{ formatConfig(row.config) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editItem(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="deleteItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card>
          <template #header>
            <span>{{ isEditing ? '编辑数据源' : '新建数据源' }}</span>
          </template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="名称">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option label="数据库" value="DATABASE" />
                <el-option label="文件" value="FILE" />
              </el-select>
            </el-form-item>
            <el-form-item label="配置">
              <el-input v-model="form.config" type="textarea" :rows="6" placeholder='{"host": "localhost", "port": 5432}' />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm">{{ isEditing ? '更新' : '创建' }}</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../api/desensitization'
import { ElMessage } from 'element-plus'

const items = ref([])
const form = reactive({
  name: '',
  type: '',
  config: ''
})
const isEditing = ref(false)
const editingId = ref(null)

function formatConfig(config) {
  if (!config) return ''
  try {
    const parsed = typeof config === 'string' ? JSON.parse(config) : config
    return JSON.stringify(parsed).substring(0, 50) + '...'
  } catch {
    return String(config).substring(0, 50) + '...'
  }
}

async function loadItems() {
  try {
    const response = await api.dataSource.list()
    items.value = response.data || response || []
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function addItem() {
  isEditing.value = false
  editingId.value = null
  resetForm()
}

function editItem(row) {
  isEditing.value = true
  editingId.value = row.id
  Object.assign(form, {
    name: row.name || '',
    type: row.type || '',
    config: typeof row.config === 'string' ? row.config : JSON.stringify(row.config, null, 2)
  })
}

async function submitForm() {
  try {
    const submitData = {
      ...form,
      config: form.config ? JSON.parse(form.config) : {}
    }
    if (isEditing.value) {
      await api.dataSource.update(editingId.value, submitData)
      ElMessage.success('更新成功')
    } else {
      await api.dataSource.create(submitData)
      ElMessage.success('创建成功')
    }
    loadItems()
    resetForm()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function deleteItem(row) {
  try {
    await api.dataSource.delete(row.id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetForm() {
  form.name = ''
  form.type = ''
  form.config = ''
}

onMounted(() => {
  loadItems()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.config-preview {
  font-size: 12px;
  color: #909399;
}
</style>
