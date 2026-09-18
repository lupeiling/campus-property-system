<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">用户管理</h1><p class="apple-caption">管理系统用户和角色分配</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>用户列表</span><el-button type="primary" size="small" @click="showAddDialog">新增用户</el-button></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-input v-model="keyword" placeholder="搜索用户名/姓名/手机号" style="width:280px" clearable @clear="loadData" @keyup.enter.native="loadData" />
      <el-select v-model="roleFilter" placeholder="角色筛选" clearable @change="loadData">
        <el-option label="管理员" value="ADMIN" /><el-option label="物业人员" value="STAFF" />
        <el-option label="教师" value="TEACHER" /><el-option label="学生" value="STUDENT" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60">
        <template slot-scope="{row}">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="role" label="角色" width="100">
        <template slot-scope="{row}"><el-tag :type="roleTagType(row.role)">{{ roleLabel(row.role) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="department" label="院系/部门" min-width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{row}"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="用户名"><el-input v-model="editForm.username" :disabled="dialogTitle === '编辑用户'" /></el-form-item>
        <el-form-item label="密码" v-if="dialogTitle === '新增用户'"><el-input v-model="editForm.password" type="password" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="editForm.realName" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender"><el-radio :label="1">男</el-radio><el-radio :label="2">女</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width:100%">
            <el-option label="管理员" value="ADMIN" /><el-option label="物业人员" value="STAFF" />
            <el-option label="教师" value="TEACHER" /><el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="院系/部门"><el-input v-model="editForm.department" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'
export default {
  name: 'UserManage',
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0,
      keyword: '', roleFilter: '',
      showDialog: false, dialogTitle: '新增用户',
      editForm: { username: '', password: '', realName: '', gender: 1, phone: '', email: '', role: 'STUDENT', department: '', status: 1 }
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const res = await getUserList({ current: this.current, size: this.size, keyword: this.keyword, role: this.roleFilter })
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    roleLabel(role) { return { ADMIN: '管理员', STAFF: '物业人员', TEACHER: '教师', STUDENT: '学生' }[role] || role },
    roleTagType(role) { return { ADMIN: 'danger', STAFF: 'warning', TEACHER: '', STUDENT: 'success' }[role] || 'info' },
    showAddDialog() { this.dialogTitle = '新增用户'; this.editForm = { username: '', password: '', realName: '', gender: 1, phone: '', email: '', role: 'STUDENT', department: '', status: 1 }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑用户'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增用户') await addUser(this.editForm); else await updateUser(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除该用户？', '提示', { type: 'warning' }); await deleteUser(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
