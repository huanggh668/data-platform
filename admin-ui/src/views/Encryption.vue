<template>
  <div class="page-container">
    <div class="page-header">
      <h2>Encryption Management</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Key Management</span>
              <el-button type="primary" size="small" @click="showGenerateDialog = true">Generate Key</el-button>
            </div>
          </template>
          <el-table :data="keys" stripe>
            <el-table-column prop="keyId" label="Key ID" width="200" />
            <el-table-column prop="algorithm" label="Algorithm" width="120" />
            <el-table-column prop="createdAt" label="Created Date" />
            <el-table-column label="Actions" width="100">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteKey(row)">Delete</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="generate-card">
          <template #header>
            <span>Generate New Key</span>
          </template>
          <el-form :model="generateForm" label-width="100px">
            <el-form-item label="Algorithm">
              <el-select v-model="generateForm.algorithm" placeholder="Select algorithm">
                <el-option label="AES-256" value="AES-256" />
                <el-option label="RSA-2048" value="RSA-2048" />
                <el-option label="RSA-4096" value="RSA-4096" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="generating" @click="generateKey">Generate</el-button>
            </el-form-item>
          </el-form>
          <div v-if="generatedKey" class="test-result">
            <div class="test-result-label">Generated Key:</div>
            <div class="test-result-content">{{ generatedKey }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="test-card">
          <template #header>
            <span>Encrypt/Decrypt Test</span>
          </template>
          <el-form :model="cryptoForm" label-width="80px">
            <el-form-item label="Key ID">
              <el-select v-model="cryptoForm.keyId" placeholder="Select key" style="width: 100%">
                <el-option v-for="key in keys" :key="key.keyId" :label="key.keyId" :value="key.keyId" />
              </el-select>
            </el-form-item>
            <el-form-item label="Input">
              <el-input v-model="cryptoForm.input" type="textarea" :rows="3" placeholder="Enter text to encrypt" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="encrypting" @click="encrypt">Encrypt</el-button>
              <el-button type="success" :loading="decrypting" @click="decrypt">Decrypt</el-button>
            </el-form-item>
          </el-form>
          <div v-if="cryptoResult" class="test-result">
            <div class="test-result-label">Result:</div>
            <div class="test-result-content">{{ cryptoResult }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="test-card">
          <template #header>
            <span>Sign/Verify Test</span>
          </template>
          <el-form :model="signForm" label-width="80px">
            <el-form-item label="Key ID">
              <el-select v-model="signForm.keyId" placeholder="Select key" style="width: 100%">
                <el-option v-for="key in keys" :key="key.keyId" :label="key.keyId" :value="key.keyId" />
              </el-select>
            </el-form-item>
            <el-form-item label="Message">
              <el-input v-model="signForm.message" type="textarea" :rows="3" placeholder="Enter message to sign" />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :loading="signing" @click="sign">Sign</el-button>
              <el-button type="info" :loading="verifying" @click="verifySign">Verify</el-button>
            </el-form-item>
          </el-form>
          <div v-if="signResult" class="test-result">
            <div class="test-result-label">Result:</div>
            <div class="test-result-content">{{ signResult }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showGenerateDialog" title="Generate Key" width="400px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="Algorithm">
          <el-select v-model="generateForm.algorithm" placeholder="Select algorithm">
            <el-option label="AES-256" value="AES-256" />
            <el-option label="RSA-2048" value="RSA-2048" />
            <el-option label="RSA-4096" value="RSA-4096" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGenerateDialog = false">Cancel</el-button>
        <el-button type="primary" :loading="generating" @click="generateKey">Generate</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const keys = ref([
  { keyId: 'key-aes-001', algorithm: 'AES-256', createdAt: '2024-01-10 10:00:00' },
  { keyId: 'key-rsa-001', algorithm: 'RSA-2048', createdAt: '2024-01-08 14:30:00' }
])

const generateForm = reactive({
  algorithm: 'AES-256'
})

const cryptoForm = reactive({
  keyId: '',
  input: ''
})

const signForm = reactive({
  keyId: '',
  message: ''
})

const generatedKey = ref('')
const cryptoResult = ref('')
const signResult = ref('')

const generating = ref(false)
const encrypting = ref(false)
const decrypting = ref(false)
const signing = ref(false)
const verifying = ref(false)
const showGenerateDialog = ref(false)

onMounted(async () => {
  try {
    const response = await api.encryption.getKeys()
    keys.value = response.keys || []
  } catch (error) {
    console.log('Using default keys')
  }
})

async function generateKey() {
  generating.value = true
  try {
    const response = await api.encryption.generateKey({
      algorithm: generateForm.algorithm
    })
    generatedKey.value = response.keyId
    keys.value.push({
      keyId: response.keyId,
      algorithm: generateForm.algorithm,
      createdAt: new Date().toLocaleString()
    })
    ElMessage.success('Key generated successfully')
    showGenerateDialog.value = false
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    generating.value = false
  }
}

function deleteKey(row) {
  const index = keys.value.indexOf(row)
  if (index > -1) {
    keys.value.splice(index, 1)
    ElMessage.success('Key deleted')
  }
}

async function encrypt() {
  if (!cryptoForm.keyId || !cryptoForm.input) {
    ElMessage.warning('Please select key and enter text')
    return
  }
  encrypting.value = true
  try {
    const response = await api.encryption.encrypt({
      keyId: cryptoForm.keyId,
      plaintext: cryptoForm.input
    })
    cryptoResult.value = response.ciphertext
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    encrypting.value = false
  }
}

async function decrypt() {
  if (!cryptoForm.keyId || !cryptoForm.input) {
    ElMessage.warning('Please select key and enter ciphertext')
    return
  }
  decrypting.value = true
  try {
    const response = await api.encryption.decrypt({
      keyId: cryptoForm.keyId,
      ciphertext: cryptoForm.input
    })
    cryptoResult.value = response.plaintext
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    decrypting.value = false
  }
}

async function sign() {
  if (!signForm.keyId || !signForm.message) {
    ElMessage.warning('Please select key and enter message')
    return
  }
  signing.value = true
  try {
    const response = await api.encryption.sign({
      keyId: signForm.keyId,
      message: signForm.message
    })
    signResult.value = `Signature: ${response.signature}`
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    signing.value = false
  }
}

async function verifySign() {
  if (!signForm.keyId || !signForm.message) {
    ElMessage.warning('Please select key and enter message')
    return
  }
  verifying.value = true
  try {
    const response = await api.encryption.verify({
      keyId: signForm.keyId,
      message: signForm.message
    })
    signResult.value = response.valid ? 'Signature is valid' : 'Signature is invalid'
  } catch (error) {
    signResult.value = 'Verification failed: ' + error.message
  } finally {
    verifying.value = false
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.generate-card,
.test-card {
  margin-bottom: 20px;
}
</style>
