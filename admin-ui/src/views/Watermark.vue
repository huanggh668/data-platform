<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $t('nav.watermark') }}</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="config-card">
          <template #header>
            <span>水印配置</span>
          </template>
          <el-form :model="config" label-width="80px">
            <el-form-item label="类型">
              <el-radio-group v-model="config.type">
                <el-radio label="text">文本</el-radio>
                <el-radio label="image">图片</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="强度">
              <el-slider v-model="config.strength" :min="1" :max="100" show-input />
            </el-form-item>
            <el-form-item label="位置">
              <el-select v-model="config.position" placeholder="请选择位置">
                <el-option label="居中" value="center" />
                <el-option label="左上" value="top-left" />
                <el-option label="右上" value="top-right" />
                <el-option label="左下" value="bottom-left" />
                <el-option label="右下" value="bottom-right" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="embed-card">
          <template #header>
            <span>嵌入水印</span>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="文本水印" name="text">
              <el-form :model="textForm" label-width="80px">
                <el-form-item label="水印文本">
                  <el-input v-model="textForm.text" placeholder="请输入水印文本" />
                </el-form-item>
                <el-form-item label="用户 ID">
                  <el-input v-model="textForm.userId" placeholder="请输入用户 ID" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="embedding" @click="embedTextWatermark">嵌入</el-button>
                </el-form-item>
              </el-form>
              <div v-if="textResult" class="test-result">
                <div class="test-result-label">结果：</div>
                <div class="test-result-content">{{ textResult }}</div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="图片水印" name="image">
              <el-form :model="imageForm" label-width="80px">
                <el-form-item label="图片">
                  <el-upload
                    ref="imageUploadRef"
                    :auto-upload="false"
                    :limit="1"
                    accept="image/*"
                    :on-change="handleImageChange"
                  >
                    <el-button>选择图片</el-button>
                  </el-upload>
                </el-form-item>
                <el-form-item label="用户 ID">
                  <el-input v-model="imageForm.userId" placeholder="请输入用户 ID" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="embedding" @click="embedImageWatermark">嵌入</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="verify-card">
      <template #header>
        <span>验证水印</span>
      </template>
      <el-form :model="verifyForm" inline>
        <el-form-item label="内容">
          <el-input v-model="verifyForm.content" placeholder="请输入内容或上传文件" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" :loading="verifying" @click="verifyWatermark">验证</el-button>
        </el-form-item>
      </el-form>
      <div v-if="verifyResult" class="test-result">
        <div class="test-result-label">验证结果：</div>
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
    ElMessage.warning('请输入水印文本和用户 ID')
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
    textResult.value = response.result || '水印嵌入成功'
    ElMessage.success('水印嵌入成功')
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
    ElMessage.warning('请选择图片并输入用户 ID')
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
    ElMessage.success('图片水印嵌入成功')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    embedding.value = false
  }
}

async function verifyWatermark() {
  if (!verifyForm.content) {
    ElMessage.warning('请输入待验证的内容')
    return
  }
  verifying.value = true
  try {
    const response = await api.watermark.verify({
      content: verifyForm.content
    })
    verifyResult.value = response.exists ? `检测到水印 - 用户 ID：${response.userId}` : '未检测到水印'
  } catch (error) {
    verifyResult.value = '验证失败：' + error.message
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
