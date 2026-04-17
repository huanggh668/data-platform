<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $t('nav.desensitization') }}</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>规则配置</span>
              <el-button type="primary" size="small" @click="addRule">添加规则</el-button>
            </div>
          </template>
          <el-table :data="rules" stripe>
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="pattern" label="匹配模式" />
            <el-table-column prop="replacement" label="替换规则" width="150" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteRule(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="test-card">
          <template #header>
            <span>脱敏测试</span>
          </template>
          <el-form :model="testForm" label-width="80px">
            <el-form-item label="类型">
              <el-select v-model="testForm.type" placeholder="请选择类型">
                <el-option label="手机号" value="phone" />
                <el-option label="邮箱" value="email" />
                <el-option label="身份证" value="idcard" />
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>
            <el-form-item label="输入">
              <el-input v-model="testForm.input" type="textarea" :rows="3" placeholder="请输入待脱敏的数据" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="testing" @click="testDesensitize">测试</el-button>
            </el-form-item>
          </el-form>
          <div v-if="testResult" class="test-result">
            <div class="test-result-label">结果：</div>
            <div class="test-result-content">{{ testResult }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="batch-card">
      <template #header>
        <div class="card-header">
          <span>批量脱敏</span>
          <el-button type="success" size="small" @click="showBatchDialog = true">上传文件</el-button>
        </div>
      </template>
      <p class="batch-tip">上传 CSV 或 JSON 文件以批量对数据进行脱敏处理</p>
    </el-card>

    <el-dialog v-model="showBatchDialog" title="批量脱敏" width="500px">
      <el-form :model="batchForm" label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="batchForm.type" placeholder="请选择类型">
            <el-option label="手机号" value="phone" />
            <el-option label="邮箱" value="email" />
            <el-option label="身份证" value="idcard" />
            <el-option label="自动检测" value="auto" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".csv,.json"
            :on-change="handleFileChange"
          >
            <el-button>选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 CSV 或 JSON 格式</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBatchDialog = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="uploadBatch">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const rules = ref([
  { type: 'phone', pattern: '^1[3-9]\\d{9}$', replacement: '$1****$2' },
  { type: 'email', pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$', replacement: '***@***.com' },
  { type: 'idcard', pattern: '^\\d{6}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$', replacement: '$1***********$2' }
])

const testForm = reactive({
  type: 'phone',
  input: ''
})

const testResult = ref('')
const testing = ref(false)
const showBatchDialog = ref(false)
const uploading = ref(false)

const batchForm = reactive({
  type: 'auto',
  file: null
})

async function testDesensitize() {
  if (!testForm.input) {
    ElMessage.warning('请输入待脱敏的数据')
    return
  }
  testing.value = true
  try {
    const response = await api.desensitize.desensitize({
      type: testForm.type,
      value: testForm.input
    })
    testResult.value = response.result
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    testing.value = false
  }
}

function addRule() {
  rules.value.push({ type: 'custom', pattern: '', replacement: '' })
}

function deleteRule(row) {
  const index = rules.value.indexOf(row)
  if (index > -1) {
    rules.value.splice(index, 1)
  }
}

function handleFileChange(file) {
  batchForm.file = file.raw
}

async function uploadBatch() {
  if (!batchForm.file) {
    ElMessage.warning('请先选择文件')
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', batchForm.file)
    formData.append('type', batchForm.type)
    await api.desensitize.batchDesensitize(formData)
    ElMessage.success('文件上传成功')
    showBatchDialog.value = false
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-card {
  margin-bottom: 20px;
}

.batch-card {
  margin-top: 20px;
}

.batch-tip {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.test-result {
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.test-result-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}

.test-result-content {
  font-size: 14px;
  word-break: break-all;
}
</style>
