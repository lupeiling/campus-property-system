<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">角色管理</h1><p class="apple-caption">系统角色与权限管理</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>角色管理</span></div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="120" />
      <el-table-column prop="description" label="描述" />
    </el-table>
  </el-card>
</template>
<script>
import { getRoleList } from '@/api/system'
export default {
  name: 'RoleManage',
  data() { return { tableData: [] } },
  created() { this.loadData() },
  methods: { async loadData() { const res = await getRoleList(); this.tableData = res.data } }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
