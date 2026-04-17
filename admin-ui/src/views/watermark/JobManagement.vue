<template>
  <div class="page-container">
    <div class="page-header">
      <h2>任务管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>任务列表</span>
              <el-button type="primary" size="small" @click="addItem">新建</el-button>
            </div>
          </template>
          <el-table :data="items" stripe>
            <el-table-column prop="name" label="任务名" />
            <el-table-column prop="type" label="类型" width="110">
              <template #default="{ row }">
                <el-tag>{{ row.type === 'EMBED' ? '嵌入' : '提取' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="进度" width="140">
              <template #default="{ row }">
                <el-progress
                  v-if="row.status === 'RUNNING' || (row.progress != null && row.progress > 0)"
                  :percentage="row.progress || 0"
                  :status="row.status === 'FAILED' ? 'exception' : row.status === 'SUCCESS' ? 'success' : undefined"
                  :stroke-width="8"
                />
                <span v-else>—</span>
              </template>
            </el-table-column>
            <el-table-column prop="cronExpression" label="Cron" width="120" />
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editItem(row)">编辑</el-button>
                <el-button type="success" size="small" @click="executeJob(row)">执行</el-button>
                <el-button type="info" size="small" @click="viewProgress(row)">进度</el-button>
                <el-button type="danger" size="small" @click="deleteItem(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card>
          <template #header>
            <span>{{ isEditing ? '编辑任务' : '创建任务' }}</span>
          </template>
          <el-form :model="form" label-width="100px">
            <el-form-item label="任务名">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="form.type" placeholder="选择类型">
                <el-option label="嵌入水印" value="EMBED" />
                <el-option label="提取水印" value="EXTRACT" />
              </el-select>
            </el-form-item>
            <el-form-item label="水印因子">
              <el-select v-model="form.factorIds" multiple placeholder="选择因子">
                <el-option
                  v-for="factor in factors"
                  :key="factor.id"
                  :label="factor.name"
                  :value="factor.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="源路径">
              <el-input v-model="form.sourcePath" placeholder="/path/to/source" />
            </el-form-item>
            <el-form-item label="目标路径">
              <el-input v-model="form.targetPath" placeholder="/path/to/target" />
            </el-form-item>
            <el-form-item label="Cron表达式">
              <el-input v-model="form.cronExpression" placeholder="0 0 * * *" />
            </el-form-item>
            <el-form-item label="定时执行">
              <el-switch v-model="form.isScheduled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm">{{ isEditing ? '更新' : '创建' }}</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 任务进度对话框 -->
    <el-dialog v-model="progressDialogVisible" title="任务执行进度" width="420px" @close="stopPolling">
      <div v-if="progressJob">
        <p><strong>任务：</strong>{{ progressJob.name }}</p>
        <p>
          <strong>状态：</strong>
          <el-tag :type="getStatusType(progressJob.status)" size="small">{{ statusLabel(progressJob.status) }}</el-tag>
        </p>
        <el-progress
          :percentage="progressJob.progress || 0"
          :status="progressJob.status === 'FAILED' ? 'exception' : progressJob.status === 'SUCCESS' ? 'success' : undefined"
          :stroke-width="12"
          style="margin: 16px 0"
        />
        <p v-if="progressJob.errorMessage" style="color:#f56c6c">
          <strong>错误：</strong>{{ progressJob.errorMessage }}
        </p>
      </div>
      <template #footer>
        <el-button @click="progressDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import api from '../../api/watermark'
import { ElMessage } from 'element-plus'

const items = ref([])
const factors = ref([])
const form = reactive({
  name: '',
  type: 'EMBED',
  factorIds: [],
  sourcePath: '',
  targetPath: '',
  cronExpression: '',
  isScheduled: false
})
const isEditing = ref(false)
const editingId = ref(null)

// 进度对话框
const progressDialogVisible = ref(false)
const progressJob = ref(null)
let pollingTimer = null

function statusLabel(status) {
  const map = { PENDING: '等待', RUNNING: '执行中', SUCCESS: '成功', FAILED: '失败' }
  return map[status] || (status || '等待')
}

function getStatusType(status) {
  const types = { PENDING: 'info', RUNNING: 'warning', SUCCESS: 'success', FAILED: 'danger' }
  return types[status] || 'info'
}

async function loadItems() {
  try {
    const response = await api.job.list()
    items.value = response.data || response || []
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function loadFactors() {
  try {
    const response = await api.factor.list()
    factors.value = response.data || response || []
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
    type: row.type || 'EMBED',
    factorIds: row.factorIds || [],
    sourcePath: row.sourcePath || '',
    targetPath: row.targetPath || '',
    cronExpression: row.cronExpression || '',
    isScheduled: row.isScheduled || false
  })
}

async function submitForm() {
  try {
    if (isEditing.value) {
      await api.job.update(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await api.job.create(form)
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
    await api.job.delete(row.id)
    ElMessage.success('删除成功')
    loadItems()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function executeJob(row) {
  try {
    await api.job.execute(row.id)
    ElMessage.success('任务已启动')
    loadItems()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

async function viewProgress(row) {
  progressJob.value = { ...row }
  progressDialogVisible.value = true
  await refreshProgress(row.id)
  if (row.status === 'RUNNING') startPolling(row.id)
}

async function refreshProgress(id) {
  try {
    const res = await api.job.getProgress(id)
    const data = res.data || res
    progressJob.value = data
    const item = items.value.find(i => i.id === id)
    if (item) { item.progress = data.progress; item.status = data.status }
    if (data.status !== 'RUNNING') stopPolling()
  } catch (err) {
    ElMessage.error('获取进度失败：' + err.message)
  }
}

function startPolling(id) {
  stopPolling()
  pollingTimer = setInterval(() => refreshProgress(id), 3000)
}

function stopPolling() {
  if (pollingTimer) { clearInterval(pollingTimer); pollingTimer = null }
}

function resetForm() {
  form.name = ''
  form.type = 'EMBED'
  form.factorIds = []
  form.sourcePath = ''
  form.targetPath = ''
  form.cronExpression = ''
  form.isScheduled = false
}

onMounted(() => { loadItems(); loadFactors() })
onUnmounted(() => stopPolling())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
