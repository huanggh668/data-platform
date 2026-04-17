<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div>
            <el-button type="primary" size="small" @click="openCreateDialog">新建用户</el-button>
            <el-button size="small" @click="fetchUsers" style="margin-left:8px">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="users" stripe v-loading="loading" style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openEditDialog(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small" link
              @click="handleToggle(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="fetchUsers"
        />
      </div>
    </el-card>

    <!-- 新建/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingUser ? '编辑用户' : '新建用户'"
      width="480px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="用户名" prop="username" v-if="!editingUser">
          <el-input v-model="form.username" placeholder="3-50个字符" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!editingUser">
          <el-input v-model="form.password" type="password" placeholder="6-100个字符" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="可选" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="可选" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="editingUser">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

// ── 列表状态 ──────────────────────────────────────────────
const allUsers   = ref([])
const loading    = ref(false)
const currentPage = ref(1)
const pageSize   = ref(10)

const total = computed(() => allUsers.value.length)
const users = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return allUsers.value.slice(start, start + pageSize.value)
})

onMounted(fetchUsers)

async function fetchUsers() {
  loading.value = true
  try {
    const res = await api.user.getUsers()
    // 后端直接返回数组或包在 data 字段中
    allUsers.value = Array.isArray(res) ? res : (res?.data || res?.users || [])
  } catch (err) {
    ElMessage.error('加载用户列表失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// ── 对话框状态 ─────────────────────────────────────────────
const dialogVisible = ref(false)
const submitting    = ref(false)
const editingUser   = ref(null)
const formRef       = ref()

const form = ref({
  username: '',
  password: '',
  email: '',
  phone: '',
  role: 'user',
  status: 1
})

const formRules = {
  username: [{ required: true, min: 3, max: 50, message: '用户名3-50个字符', trigger: 'blur' }],
  password: [{ required: true, min: 6, max: 100, message: '密码6-100个字符', trigger: 'blur' }],
  role:     [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function openCreateDialog() {
  editingUser.value = null
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  editingUser.value = row
  form.value = {
    username: row.username,
    password: '',
    email:    row.email  || '',
    phone:    row.phone  || '',
    role:     row.role   || 'user',
    status:   row.status ?? 1
  }
  dialogVisible.value = true
}

function resetForm() {
  form.value = { username: '', password: '', email: '', phone: '', role: 'user', status: 1 }
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editingUser.value) {
      await api.user.updateUser(editingUser.value.id, {
        email:  form.value.email  || undefined,
        phone:  form.value.phone  || undefined,
        role:   form.value.role,
        status: form.value.status
      })
      ElMessage.success('用户更新成功')
    } else {
      await api.user.createUser({
        username: form.value.username,
        password: form.value.password,
        email:    form.value.email  || undefined,
        phone:    form.value.phone  || undefined,
        role:     form.value.role
      })
      ElMessage.success('用户创建成功')
    }
    dialogVisible.value = false
    await fetchUsers()
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// ── 启用/禁用 ──────────────────────────────────────────────
async function handleToggle(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${row.username}" 吗？`,
      '确认操作',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    const newStatus = row.status === 1 ? 0 : 1
    await api.user.toggleUser(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(`用户已${action}`)
  } catch (err) {
    if (err !== 'cancel') ElMessage.error(err.message || '操作失败')
  }
}

// ── 删除 ───────────────────────────────────────────────────
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'danger' }
    )
    await api.user.deleteUser(row.id)
    ElMessage.success('用户已删除')
    await fetchUsers()
  } catch (err) {
    if (err !== 'cancel') ElMessage.error(err.message || '删除失败')
  }
}

// ── 工具函数 ───────────────────────────────────────────────
function formatDate(val) {
  if (!val) return '—'
  return new Date(val).toLocaleString('zh-CN')
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
