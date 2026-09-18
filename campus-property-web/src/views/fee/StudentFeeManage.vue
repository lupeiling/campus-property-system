<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">费用事项</h1>
      <p class="apple-caption">查看费用清单与缴纳</p>
    </div>
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="待缴费用" name="unpaid">
        <el-table :data="tableData" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="feeType" label="费用类型" width="100" />
          <el-table-column prop="amount" label="金额(元)" width="100" />
          <el-table-column prop="semester" label="学期" width="130" />
          <el-table-column prop="dueDate" label="截止日期" width="120" />
          <el-table-column prop="remark" label="备注" min-width="150" />
          <el-table-column label="操作" width="100">
            <template slot-scope="{row}">
              <el-button size="small" type="primary" @click="handlePay(row)">缴费</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已缴记录" name="paid">
        <el-table :data="tableData" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="feeType" label="费用类型" width="100" />
          <el-table-column prop="amount" label="金额(元)" width="100" />
          <el-table-column prop="semester" label="学期" width="130" />
          <el-table-column prop="payTime" label="缴费时间" min-width="160" />
          <el-table-column prop="remark" label="备注" min-width="150" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="全部明细" name="all">
        <div style="margin-bottom:12px;display:flex;gap:10px">
          <el-select v-model="searchSemester" placeholder="选择学期" clearable style="width:160px" @change="loadData">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s" />
          </el-select>
          <el-select v-model="searchFeeType" placeholder="费用类型" clearable style="width:130px" @change="loadData">
            <el-option label="住宿费" value="住宿费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
          </el-select>
        </div>
        <el-table :data="tableData" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="feeType" label="费用类型" width="100" />
          <el-table-column prop="amount" label="金额(元)" width="100" />
          <el-table-column prop="semester" label="学期" width="130" />
          <el-table-column label="状态" width="90">
            <template slot-scope="{row}">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">{{ row.status === 1 ? '已缴' : '待缴' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="截止日期" width="120" />
          <el-table-column prop="payTime" label="缴费时间" min-width="160" />
          <el-table-column prop="remark" label="备注" min-width="120" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </div>
</template>
<script>
import { getFeeList, payFee, getSemesters } from '@/api/newModules'
export default {
  name: 'StudentFeeManage',
  data() {
    return {
      activeTab: 'unpaid',
      tableData: [], current: 1, size: 10, total: 0,
      semesters: [], searchSemester: '', searchFeeType: ''
    }
  },
  created() {
    this.loadSemesters()
    this.loadData()
  },
  methods: {
    async loadSemesters() {
      try { const res = await getSemesters(); this.semesters = res.data } catch (e) {}
    },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.activeTab === 'unpaid') params.status = 0
      else if (this.activeTab === 'paid') params.status = 1
      if (this.searchSemester) params.semester = this.searchSemester
      if (this.searchFeeType) params.feeType = this.searchFeeType
      try {
        const res = await getFeeList(params)
        this.tableData = res.data.records; this.total = res.data.total
      } catch (e) { this.$message.error('加载失败') }
    },
    handleTabChange() { this.current = 1; this.loadData() },
    handlePageChange(page) { this.current = page; this.loadData() },
    async handlePay(row) {
      try {
        await this.$confirm('确认缴纳该费用？', '缴费确认', { type: 'warning' })
        await payFee(row.id)
        this.$message.success('缴费成功')
        this.loadData()
      } catch (e) { if (e !== 'cancel') this.$message.error('缴费失败') }
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
