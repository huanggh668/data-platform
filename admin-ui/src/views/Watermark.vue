<template>
  <div class="page-container">
    <div class="page-header">
      <h2>Watermark Management</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="config-card">
          <template #header>
            <span>Watermark Configuration</span>
          </template>
          <el-form :model="config" label-width="100px">
            <el-form-item label="Type">
              <el-radio-group v-model="config.type">
                <el-radio label="text">Text</el-radio>
                <el-radio label="image">Image</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="Strength">
              <el-slider v-model="config.strength" :min="1" :max="100" show-input />
            </el-form-item>
            <el-form-item label="Position">
              <el-select v-model="config.position" placeholder="Select position">
                <el-option label="Center" value="center" />
                <el-option label="Top-Left" value="top-left" />
                <el-option label="Top-Right" value="top-right" />
                <el-option label="Bottom-Left" value="bottom-left" />
                <el-option label="Bottom-Right" value="bottom-right" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="embed-card">
          <template #header>
            <span>Embed Watermark</span>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="Text Watermark" name="text">
              <el-form :model="textForm" label-width="100px">
                <el-form-item label="Text">
                  <el-input v-model="textForm.text" placeholder="Enter watermark text" />
                </el-form-item>
                <el-form-item label="User ID">
                  <el-input v-model="textForm.userId" placeholder="Enter user ID" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="embedding" @click="embedTextWatermark">Embed</el-button>
                </el-form-item>
              </el-form>
              <div v-if="textResult" class="test-result">
                <div class="test-result-label">Result:</div>
                <div class="test-result-content">{{ textResult }}</div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="Image Watermark" name="image">
              <el-form :model="imageForm" label-width="100px">
                <el-form-item label="Image">
                  <el-upload
                    ref="imageUploadRef"
                    :auto-upload="false"
                    :limit="1"
                    accept="image/*"
                    :on-change="handleImageChange"
                  >
                    <el-button>Select Image</el-button>
                  </el-upload>
                </el-form-item>
                <el-form-item label="User ID">
                  <el-input v-model="imageForm.userId" placeholder="Enter user ID" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="embedding" @click="embedImageWatermark">Embed</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="verify-card">
      <template #header>
        <span>Verify Watermark</span>
      </template>
      <el-form :model="verifyForm" inline>
        <el-form-item label="Content">
          <el-input v-model="verifyForm.content" placeholder="Enter content or upload file" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" :loading="verifying" @click="verifyWatermark">Verify</el-button>
        </el-form-item>
      </el-form>
      <div v-if="verifyResult" class="test-result">
        <div class="test-result-label">Verification Result:</div>
        <div class="test-result-content">{{ verifyResult }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const config = reactive({
  type: 'text',
  strength: 50,
  position: 'center'
})

const activeTab = ref('text')

const textForm = reactive({
  text: '',
  userId: ''
})

const imageForm = reactive({
  image: null,
  userId: ''
})

const verifyForm = reactive({
  content: ''
})

const textResult = ref('')
const verifyResult = ref('')
const embedding = ref(false)
const verifying = ref(false)

async function embedTextWatermark() {
  if (!textForm.text || !textForm.userId) {
    ElMessage.warning('Please enter text and user ID')
    return
  }
  embedding.value = true
  try {
    const response = await api.watermark.embedText({
      text: textForm.text,
      userId: textForm.userId,
      strength: config.strength,
      position: config.position
    })
    textResult.value = response.result || 'Watermark embedded successfully'
    ElMessage.success('Watermark embedded successfully')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    embedding.value = false
  }
}

function handleImageChange(file) {
  imageForm.image = file.raw
}

async function embedImageWatermark() {
  if (!imageForm.image || !imageForm.userId) {
    ElMessage.warning('Please select image and enter user ID')
    return
  }
  embedding.value = true
  try {
    const formData = new FormData()
    formData.append('image', imageForm.image)
    formData.append('userId', imageForm.userId)
    formData.append('strength', config.strength)
    formData.append('position', config.position)
    await api.watermark.embedImage(formData)
    ElMessage.success('Image watermark embedded successfully')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    embedding.value = false
  }
}

async function verifyWatermark() {
  if (!verifyForm.content) {
    ElMessage.warning('Please enter content to verify')
    return
  }
  verifying.value = true
  try {
    const response = await api.watermark.verify({
      content: verifyForm.content
    })
    verifyResult.value = response.exists ? `Watermark found - User ID: ${response.userId}` : 'No watermark found'
  } catch (error) {
    verifyResult.value = 'Verification failed: ' + error.message
  } finally {
    verifying.value = false
  }
}
</script>

<style scoped>
.config-card,
.embed-card {
  margin-bottom: 20px;
}

.verify-card {
  margin-top: 20px;
}
</style>
