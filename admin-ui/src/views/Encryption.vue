<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $t('nav.encryption') }}</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>密钥管理</span>
              <el-button type="primary" size="small" @click="showGenerateDialog = true">生成密钥</el-button>
            </div>
          </template>
          <el-table :data="keys" stripe>
            <el-table-column prop="keyId" label="密钥 ID" width="200" />
            <el-table-column prop="algorithm" label="算法" width="120" />
            <el-table-column prop="createdAt" label="创建时间" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteKey(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="generate-card">
          <template #header>
            <span>生成新密钥</span>
          </template>
          <el-form :model="generateForm" label-width="80px">
            <el-form-item label="算法">
              <el-select v-model="generateForm.algorithm" placeholder="请选择算法">
                <el-option label="AES-256" value="AES-256" />
                <el-option label="RSA-2048" value="RSA-2048" />
                <el-option label="RSA-4096" value="RSA-4096" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="generating" @click="generateKey">生成</el-button>
            </el-form-item>
          </el-form>
          <div v-if="generatedKey" class="test-result">
            <div class="test-result-label">已生成密钥：</div>
            <div class="test-result-content">{{ generatedKey }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="test-card">
          <template #header>
            <span>加密 / 解密测试</span>
          </template>
          <el-form :model="cryptoForm" label-width="80px">
            <el-form-item label="密钥 ID">
              <el-select v-model="cryptoForm.keyId" placeholder="请选择密钥" style="width: 100%">
                <el-option v-for="key in keys" :key="key.keyId" :label="key.keyId" :value="key.keyId" />
              </el-select>
            </el-form-item>
            <el-form-item label="输入">
              <el-input v-model="cryptoForm.input" type="textarea" :rows="3" placeholder="请输入待加密的文本" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="encrypting" @click="encrypt">加密</el-button>
              <el-button type="success" :loading="decrypting" @click="decrypt">解密</el-button>
            </el-form-item>
          </el-form>
          <div v-if="cryptoResult" class="test-result">
            <div class="test-result-label">结果：</div>
            <div class="test-result-content">{{ cryptoResult }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="test-card">
          <template #header>
            <span>签名 / 验签测试</span>
          </template>
          <el-form :model="signForm" label-width="80px">
            <el-form-item label="密钥 ID">
              <el-select v-model="signForm.keyId" placeholder="请选择密钥" style="width: 100%">
                <el-option v-for="key in keys" :key="key.keyId" :label="key.keyId" :value="key.keyId" />
              </el-select>
            </el-form-item>
            <el-form-item label="消息">
              <el-input v-model="signForm.message" type="textarea" :rows="3" placeholder="请输入待签名的消息" />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :loading="signing" @click="sign">签名</el-button>
              <el-button type="info" :loading="verifying" @click="verifySign">验签</el-button>
            </el-form-item>
          </el-form>
          <div v-if="signResult" class="test-result">
            <div class="test-result-label">结果：</div>
            <div class="test-result-content">{{ signResult }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showGenerateDialog" title="生成密钥" width="400px">
      <el-form :model="generateForm" label-width="80px">
        <el-form-item label="算法">
          <el-select v-model="generateForm.algorithm" placeholder="请选择算法">
            <el-option label="AES-256" value="AES-256" />
            <el-option label="RSA-2048" value="RSA-2048" />
            <el-option label="RSA-4096" value="RSA-4096" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGenerateDialog = false">取消</el-button>
        <el-button type="primary" :loading="generating" @click="generateKey">生成</el-button>
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
    console.log('使用默认密钥列表')
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
    ElMessage.success('密钥生成成功')
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
    ElMessage.success('密钥已删除')
  }
}

async function encrypt() {
  if (!cryptoForm.keyId || !cryptoForm.input) {
    ElMessage.warning('请选择密钥并输入文本')
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
    ElMessage.warning('请选择密钥并输入密文')
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
    ElMessage.warning('请选择密钥并输入消息')
    return
  }
  signing.value = true
  try {
    const response = await api.encryption.sign({
      keyId: signForm.keyId,
      message: signForm.message
    })
    signResult.value = `签名：${response.signature}`
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    signing.value = false
  }
}

async function verifySign() {
  if (!signForm.keyId || !signForm.message) {
    ElMessage.warning('请选择密钥并输入消息')
    return
  }
  verifying.value = true
  try {
    const response = await api.encryption.verify({
      keyId: signForm.keyId,
      message: signForm.message
    })
    signResult.value = response.valid ? '签名有效' : '签名无效'
  } catch (error) {
    signResult.value = '验签失败：' + error.message
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
