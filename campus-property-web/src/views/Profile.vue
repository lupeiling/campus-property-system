<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">个人信息</h1>
      <p class="apple-caption">管理你的账户设置</p>
    </div>
    <div class="apple-card-flat" style="max-width:600px;margin-top:8px">
      <el-form :model="form" label-width="80px" label-position="top">
        <el-form-item label="用户名">
          <el-input :value="user.username" disabled />
        </el-form-item>
        <el-form-item label="职称">
          <el-tag v-if="user.title" type="warning">{{ user.title }}</el-tag>
          <el-tag v-else type="info">{{ roleLabel }}</el-tag>
        </el-form-item>
        <el-form-item v-if="user.department" label="院系/部门">
          <el-input :value="user.department" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <div style="display:flex;gap:12px">
            <el-button type="primary" @click="handleSave">保存修改</el-button>
            <el-button @click="showPasswordDialog = true">修改密码</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog title="修改密码" :visible.sync="showPasswordDialog" width="400px">
      <el-form :model="pwdForm" label-position="top">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { updateProfile, changePassword } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    return {
      form: {},
      pwdForm: { oldPassword: '', newPassword: '' },
      showPasswordDialog: false
    }
  },
  computed: {
    user() { return this.$store.getters.user },
    roleLabel() {
      const map = { ADMIN: '管理员', STAFF: '物业人员', TEACHER: '教师', STUDENT: '学生' }
      return map[this.user?.role] || ''
    }
  },
  created() {
    this.form = { ...this.user }
  },
  methods: {
    async handleSave() {
      try {
        await updateProfile(this.form)
        this.$message.success('保存成功')
        this.$store.dispatch('fetchUser')
      } catch (e) {}
    },
    async handleChangePassword() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        this.$message.warning('请填写完整')
        return
      }
      try {
        await changePassword({ userId: this.user.id, ...this.pwdForm })
        this.$message.success('密码修改成功，请重新登录')
        this.showPasswordDialog = false
        this.$store.dispatch('logout')
        this.$router.push('/login')
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.apple-hero-section {
  margin-bottom: 8px;
}

.apple-card-flat {
  background: var(--apple-white);
  border-radius: var(--apple-radius-xl);
  border: none;
  box-shadow: none;
  padding: 32px;
}
</style>
