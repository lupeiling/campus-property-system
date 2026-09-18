<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">宿舍审核</h1>
      <p class="apple-caption">入住/退宿/调宿审核管理</p>
    </div>
    <div style="margin-bottom:12px;display:flex;gap:10px">
      <el-select v-model="searchStatus" placeholder="审核状态" clearable style="width:130px" @change="loadData">
        <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已拒绝" :value="2" />
      </el-select>
      <el-select v-model="searchType" placeholder="审核类型" clearable style="width:130px" @change="loadData">
        <el-option label="入住" value="入住" /><el-option label="退宿" value="退宿" /><el-option label="调宿" value="调宿" />
      </el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="userId" label="申请人ID" width="90" />
      <el-table-column prop="auditType" label="类型" width="80" />
      <el-table-column prop="roomId" label="房间ID" width="80" />
      <el-table-column prop="bedNo" label="床位" width="60" />
      <el-table-column prop="reason" label="申请原因" min-width="150" />
      <el-table-column label="状态" width="90">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 0 ? '待审核' : row.status === 1 ? '已通过' : '已拒绝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditBy" label="审核人" width="90" />
      <el-table-column prop="auditTime" label="审核时间" width="160" />
      <el-table-column label="操作" width="150">
        <template slot-scope="{row}">
          <template v-if="row.status === 0">
            <el-button size="mini" type="success" @click="handleAudit(row, 1)">通过</el-button>
            <el-button size="mini" type="danger" @click="handleAudit(row, 2)">拒绝</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </div>
</template>
<script>
import { getDormAuditList, approveDormAudit, rejectDormAudit } from '@/api/newModules'
export default {
  name: 'DormAuditManage',
  data() {
    return { tableData: [], current: 1, size: 10, total: 0, searchStatus: null, searchType: '' }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.searchStatus !== null && this.searchStatus !== '') params.status = this.searchStatus
      if (this.searchType) params.auditType = this.searchType
      try {
        const res = await getDormAuditList(params)
        this.tableData = res.data.records; this.total = res.data.total
      } catch (e) { this.$message.error('加载失败') }
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    async handleAudit(row, status) {
      const action = status === 1 ? '通过' : '拒绝'
      try {
        await this.$confirm(`确认${action}该申请？`, '审核确认', { type: 'warning' })
        const user = this.$store.getters.user
        const data = { auditBy: user ? user.realName : '管理员', auditRemark: '' }
        if (status === 1) { await approveDormAudit(row.id, data) }
        else { await rejectDormAudit(row.id, data) }
        this.$message.success(`已${action}`); this.loadData()
      } catch (e) { if (e !== 'cancel') this.$message.error('操作失败') }
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
