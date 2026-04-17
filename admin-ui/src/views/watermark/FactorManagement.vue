<template>
  <div class="page-container">
    <div class="page-header">
      <h2>水印因子管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>因子列表</span>
              <el-button type="primary" size="small" @click="addItem">新建</el-button>
            </div>
          </template>
          <el-table :data="items" stripe>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="150" />
            <el-table-column prop="value" label="值" />
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
            <span>{{ isEditing ? '编辑因子' : '新建因子' }}</span>
          </template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="名称">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option label="用户 ID" value="USER_ID" />
                <el-option label="时间戳" value="TIMESTAMP" />
                <el-option label="业务编码" value="BUSINESS_CODE" />
              </el-select>
            </el-form-item>
            <el-form-item label="值">
              <el-input v-model="form.value" placeholder="请输入值" />
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
import api from '../../api/watermark'
import { ElMessage } from 'element-plus'

const items = ref([])
const form = reactive({
  name: '',
  type: '',
  value: ''
})
const isEditing = ref(false)
const editingId = ref(null)

async function loadItems() {
  try {
    const response = await api.factor.list()
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
    value: row.value || ''
  })
}

async function submitForm() {
  try {
    if (isEditing.value) {
      await api.factor.update(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await api.factor.create(form)
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
    await api.factor.delete(row.id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetForm() {
  form.name = ''
  form.type = ''
  form.value = ''
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
</style>
