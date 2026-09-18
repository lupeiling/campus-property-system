<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">系统日志</h1><p class="apple-caption">系统操作日志审计</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>系统日志</span></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-input v-model="username" placeholder="搜索用户名" style="width:200px" clearable />
      <el-input v-model="operation" placeholder="搜索操作内容" style="width:200px" clearable />
      <el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="operation" label="操作内容" />
      <el-table-column prop="method" label="请求方法" width="200" />
      <el-table-column prop="ip" label="IP地址" width="130" />
      <el-table-column prop="createTime" label="操作时间" width="160" />
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </el-card>
</template>
<script>
import { getLogList } from '@/api/system'
export default {
  name: 'LogManage',
  data() {
    return { tableData: [], current: 1, size: 10, total: 0, username: '', operation: '' }
  },
  created() { this.loadData() },
  methods: {
    async loadData() { const res = await getLogList({ current: this.current, size: this.size, username: this.username, operation: this.operation }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
