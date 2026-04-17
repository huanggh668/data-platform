<template>
  <div class="page-container">
    <div class="page-header">
      <h2>溯源记录</h2>
    </div>

    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="任务 ID">
          <el-input v-model="searchForm.jobId" placeholder="请输入任务 ID" clearable />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="result-card">
      <template #header>
        <span>溯源记录（共 {{ items.length }} 条）</span>
      </template>
      <el-table :data="items" stripe @row-click="showDetail">
        <el-table-column prop="jobId" label="任务 ID" width="120" />
        <el-table-column prop="factorId" label="因子 ID" width="120" />
        <el-table-column prop="embeddedData" label="嵌入数据">
          <template #default="{ row }">
            <span class="data-cell">{{ row.embeddedData || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="记录详情" width="500px">
      <el-descriptions :column="1" border v-if="currentRecord">
        <el-descriptions-item label="ID">{{ currentRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="任务 ID">{{ currentRecord.jobId }}</el-descriptions-item>
        <el-descriptions-item label="因子 ID">{{ currentRecord.factorId }}</el-descriptions-item>
        <el-descriptions-item label="嵌入数据">{{ currentRecord.embeddedData }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentRecord.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../../api/watermark'
import { ElMessage } from 'element-plus'

const items = ref([])
const searchForm = reactive({
  jobId: '',
  dateRange: []
})
const detailDialogVisible = ref(false)
const currentRecord = ref(null)

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

async function searchRecords() {
  try {
    const params = {}
    if (searchForm.jobId) {
      params.jobId = searchForm.jobId
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const response = await api.traceRecord.list(params)
    items.value = response.data || response || []
  } catch (error) {
    ElMessage.error(error.message)
  }
}

function resetSearch() {
  searchForm.jobId = ''
  searchForm.dateRange = []
  items.value = []
}

function showDetail(row) {
  currentRecord.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  searchRecords()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.result-card {
  margin-bottom: 20px;
}

.data-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
}
</style>
