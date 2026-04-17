<template>
  <div class="page-container">
    <div class="page-header">
      <h2>规则管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>规则列表</span>
              <el-button type="primary" size="small" @click="addItem">新建</el-button>
            </div>
          </template>
          <el-table :data="items" stripe>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="dataType" label="数据类型" width="120" />
            <el-table-column prop="pattern" label="匹配模式" />
            <el-table-column prop="priority" label="优先级" width="80" />
            <el-table-column prop="enabled" label="启用" width="80">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '是' : '否' }}</el-tag>
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
            <span>{{ isEditing ? '编辑规则' : '新建规则' }}</span>
          </template>
          <el-form :model="form" label-width="90px">
            <el-form-item label="名称">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="数据类型">
              <el-select v-model="form.dataType" placeholder="请选择类型">
                <el-option label="手机号" value="PHONE" />
                <el-option label="邮箱" value="EMAIL" />
                <el-option label="身份证" value="ID_CARD" />
                <el-option label="自定义" value="CUSTOM" />
              </el-select>
            </el-form-item>
            <el-form-item label="匹配模式">
              <el-input v-model="form.pattern" placeholder="正则表达式" />
            </el-form-item>
            <el-form-item label="替换规则">
              <el-input v-model="form.replacement" placeholder="替换字符串" />
            </el-form-item>
            <el-form-item label="优先级">
              <el-input-number v-model="form.priority" :min="0" :max="100" />
            </el-form-item>
            <el-form-item label="启用">
              <el-switch v-model="form.enabled" />
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
  dataType: '',
  pattern: '',
  replacement: '',
  priority: 0,
  enabled: true
})
const isEditing = ref(false)
const editingId = ref(null)

async function loadItems() {
  try {
    const response = await api.rule.list()
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
    dataType: row.dataType || '',
    pattern: row.pattern || '',
    replacement: row.replacement || '',
    priority: row.priority || 0,
    enabled: row.enabled !== false
  })
}

async function submitForm() {
  try {
    if (isEditing.value) {
      await api.rule.update(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await api.rule.create(form)
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
    await api.rule.delete(row.id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetForm() {
  form.name = ''
  form.dataType = ''
  form.pattern = ''
  form.replacement = ''
  form.priority = 0
  form.enabled = true
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
