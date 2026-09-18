<template>
  <div class="apple-login-page">
    <div class="register-container">
      <h1 class="login-title">创建账户</h1>
      <p class="login-subtitle">注册以使用高校后勤物业管理系统</p>
      <el-form ref="registerForm" :model="registerForm" :rules="rules" label-width="0" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" prefix-icon="el-icon-user" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" prefix-icon="el-icon-lock" placeholder="密码（至少6位）" type="password" show-password />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="registerForm.realName" prefix-icon="el-icon-s-custom" placeholder="真实姓名" />
        </el-form-item>
        <div class="form-row">
          <el-form-item>
            <el-select v-model="registerForm.gender" placeholder="性别" style="width:100%">
              <el-option label="男" :value="1" /><el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="registerForm.role" placeholder="角色" style="width:100%">
              <el-option label="学生" value="STUDENT" /><el-option label="教师" value="TEACHER" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item>
          <el-input v-model="registerForm.phone" prefix-icon="el-icon-phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.email" prefix-icon="el-icon-message" placeholder="邮箱" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.department" prefix-icon="el-icon-office-building" placeholder="院系/部门" />
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'STUDENT'">
          <el-input v-model="registerForm.studentNo" prefix-icon="el-icon-document" placeholder="学号" />
        </el-form-item>
        <el-form-item>
          <button type="button" class="apple-login-btn" :disabled="loading" @click="handleRegister">
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </el-form-item>
        <div class="login-footer">
          <router-link to="/login" class="apple-link">已有账号？返回登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Register',
  data() {
    return {
      registerForm: { username: '', password: '', realName: '', gender: 1, phone: '', email: '', role: 'STUDENT', department: '', studentNo: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await register(this.registerForm)
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.apple-login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #000000;
  overflow-y: auto;
  padding: 40px 0;
}

.register-container {
  width: 440px;
  text-align: center;
}

.login-title {
  font-family: var(--apple-font-display);
  font-size: 40px;
  font-weight: 600;
  line-height: 1.1;
  color: #ffffff;
  margin-bottom: 8px;
}

.login-subtitle {
  font-family: var(--apple-font-text);
  font-size: 21px;
  font-weight: 400;
  line-height: 1.19;
  letter-spacing: 0.231px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 32px;
}

.login-form .el-form-item {
  margin-bottom: 14px;
}

.login-form .el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #ffffff;
  height: 44px;
  border-radius: 11px;
  padding-left: 40px;
}

.login-form .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.login-form .el-input__inner:focus {
  border-color: var(--apple-blue);
  box-shadow: 0 0 0 2px rgba(0, 113, 227, 0.3);
  background: rgba(255, 255, 255, 0.1);
}

.login-form .el-input__prefix {
  color: rgba(255, 255, 255, 0.4);
  left: 12px;
}

.login-form .el-select .el-input__inner {
  padding-left: 14px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-row .el-form-item {
  flex: 1;
}

.apple-login-btn {
  width: 100%;
  height: 44px;
  border-radius: var(--apple-radius-md);
  background: var(--apple-blue);
  color: #ffffff;
  border: none;
  font-family: var(--apple-font-text);
  font-size: 17px;
  font-weight: 400;
  cursor: pointer;
  transition: background 0.2s ease;
}

.apple-login-btn:hover { background: #0077ed; }
.apple-login-btn:active { background: #0066cc; }
.apple-login-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.login-footer { margin-top: 16px; }
.login-footer .apple-link { color: var(--apple-bright-blue); font-size: 14px; }
</style>
