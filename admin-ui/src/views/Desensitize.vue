<template>
  <div class="page-container">
    <div class="page-header">
      <h2>Desensitization Management</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Rule Configuration</span>
              <el-button type="primary" size="small" @click="addRule">Add Rule</el-button>
            </div>
          </template>
          <el-table :data="rules" stripe>
            <el-table-column prop="type" label="Type" width="120" />
            <el-table-column prop="pattern" label="Pattern" />
            <el-table-column prop="replacement" label="Replacement" width="150" />
            <el-table-column label="Actions" width="120">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteRule(row)">Delete</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="10">
        <el-card class="test-card">
          <template #header>
            <span>Test Desensitization</span>
          </template>
          <el-form :model="testForm" label-width="80px">
            <el-form-item label="Type">
              <el-select v-model="testForm.type" placeholder="Select type">
                <el-option label="Phone" value="phone" />
                <el-option label="Email" value="email" />
                <el-option label="ID Card" value="idcard" />
                <el-option label="Custom" value="custom" />
              </el-select>
            </el-form-item>
            <el-form-item label="Input">
              <el-input v-model="testForm.input" type="textarea" :rows="3" placeholder="Enter value to desensitize" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="testing" @click="testDesensitize">Test</el-button>
            </el-form-item>
          </el-form>
          <div v-if="testResult" class="test-result">
            <div class="test-result-label">Result:</div>
            <div class="test-result-content">{{ testResult }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="batch-card">
      <template #header>
        <div class="card-header">
          <span>Batch Desensitize</span>
          <el-button type="success" size="small" @click="showBatchDialog = true">Upload File</el-button>
        </div>
      </template>
      <p class="batch-tip">Upload a CSV or JSON file to batch desensitize data</p>
    </el-card>

    <el-dialog v-model="showBatchDialog" title="Batch Desensitize" width="500px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="Type">
          <el-select v-model="batchForm.type" placeholder="Select type">
            <el-option label="Phone" value="phone" />
            <el-option label="Email" value="email" />
            <el-option label="ID Card" value="idcard" />
            <el-option label="Auto Detect" value="auto" />
          </el-select>
        </el-form-item>
        <el-form-item label="File">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".csv,.json"
            :on-change="handleFileChange"
          >
            <el-button>Select File</el-button>
            <template #tip>
              <div class="el-upload__tip">CSV or JSON files</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBatchDialog = false">Cancel</el-button>
        <el-button type="primary" :loading="uploading" @click="uploadBatch">Upload</el-button>
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
    ElMessage.warning('Please enter input value')
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
    ElMessage.warning('Please select a file')
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', batchForm.file)
    formData.append('type', batchForm.type)
    await api.desensitize.batchDesensitize(formData)
    ElMessage.success('File uploaded successfully')
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
</style>
