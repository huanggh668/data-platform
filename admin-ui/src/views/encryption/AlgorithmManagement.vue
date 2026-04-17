<template>
  <div class="page-container">
    <div class="page-header">
      <h2>算法管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>算法列表</span>
              <el-button type="primary" size="small" @click="addItem">新建</el-button>
            </div>
          </template>
          <el-table :data="items" stripe>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="keyLength" label="密钥长度" width="100" />
            <el-table-column prop="mode" label="模式" width="100" />
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
            <span>{{ isEditing ? '编辑算法' : '新建算法' }}</span>
          </template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="名称">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option label="AES" value="AES" />
                <el-option label="RSA" value="RSA" />
                <el-option label="SM4" value="SM4" />
              </el-select>
            </el-form-item>
            <el-form-item label="密钥长度">
              <el-input-number v-model="form.keyLength" :min="0" :max="512" />
            </el-form-item>
            <el-form-item label="模式">
              <el-select v-model="form.mode" placeholder="请选择模式">
                <el-option label="CBC" value="CBC" />
                <el-option label="ECB" value="ECB" />
                <el-option label="GCM" value="GCM" />
                <el-option label="CTR" value="CTR" />
              </el-select>
            </el-form-item>
            <el-form-item label="填充方式">
              <el-select v-model="form.padding" placeholder="请选择填充方式">
                <el-option label="PKCS5" value="PKCS5" />
                <el-option label="PKCS7" value="PKCS7" />
                <el-option label="无填充" value="NONE" />
              </el-select>
            </el-form-item>
            <el-form-item label="配置">
              <el-input v-model="form.config" type="textarea" :rows="4" placeholder='{"iv": "..."}' />
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
import api from '../../api/encryption'
import { ElMessage } from 'element-plus'

const items = ref([])
const form = reactive({
  name: '',
  type: '',
  keyLength: 128,
  mode: '',
  padding: '',
  config: ''
})
const isEditing = ref(false)
const editingId = ref(null)

async function loadItems() {
  try {
    const response = await api.algorithm.list()
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
    keyLength: row.keyLength || 128,
    mode: row.mode || '',
    padding: row.padding || '',
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
      await api.algorithm.update(editingId.value, submitData)
      ElMessage.success('更新成功')
    } else {
      await api.algorithm.create(submitData)
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
    await api.algorithm.delete(row.id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetForm() {
  form.name = ''
  form.type = ''
  form.keyLength = 128
  form.mode = ''
  form.padding = ''
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
</style>
