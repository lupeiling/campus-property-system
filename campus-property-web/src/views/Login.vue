<template>
  <div class="apple-login-page">
    <div class="login-container">
      <h1 class="login-title">高校后勤物业管理系统</h1>
      <p class="login-subtitle">登录以访问管理平台</p>
      <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-form" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" placeholder="密码" type="password" show-password @keyup.enter.native="handleLogin" />
        </el-form-item>
        <el-form-item>
          <button type="button" class="apple-login-btn" :disabled="loading" @click="handleLogin">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </el-form-item>
        <div class="login-footer">
          <router-link to="/register" class="apple-link">还没有账号？立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.loginForm)
          this.$store.dispatch('login', res.data)
          this.$message.success('登录成功')
          const role = res.data.user ? res.data.user.role : ''
          if (role === 'ADMIN' || role === 'STAFF') {
            this.$router.push('/dashboard')
          } else {
            this.$router.push('/')
          }
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
}

.login-container {
  width: 420px;
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
  margin-bottom: 40px;
}

.login-form .el-form-item {
  margin-bottom: 16px;
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

.apple-login-btn:hover {
  background: #0077ed;
}

.apple-login-btn:active {
  background: #0066cc;
}

.apple-login-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.login-footer {
  margin-top: 20px;
}

.login-footer .apple-link {
  color: var(--apple-bright-blue);
  font-size: 14px;
}
</style>
