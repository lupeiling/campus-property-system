<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">消费记录</h1><p class="apple-caption">查看餐饮消费记录</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>消费记录列表</span><div style="display:flex;gap:8px"><el-dropdown v-if="isStaff" @command="handleExport"><el-button size="small">导出<i class="el-icon-arrow-down el-icon--right"></i></el-button><el-dropdown-menu slot="dropdown"><el-dropdown-item command="excel">导出 Excel</el-dropdown-item><el-dropdown-item command="pdf">导出 PDF</el-dropdown-item></el-dropdown-menu></el-dropdown></div></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-input v-model="searchUserId" placeholder="按学生ID查询" style="width:180px" clearable @keyup.enter.native="loadData" />
      <el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="userId" label="学生ID" width="80" />
      <el-table-column label="食堂" min-width="120">
        <template slot-scope="{row}">{{ getCanteenName(row.canteenId) }}</template>
      </el-table-column>
      <el-table-column label="菜品" min-width="100">
        <template slot-scope="{row}">{{ getDishName(row.dishId) }}</template>
      </el-table-column>
      <el-table-column prop="amount" label="消费金额(元)" width="110" />
      <el-table-column prop="consumeTime" label="消费时间" min-width="160" />
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </el-card>
</template>
<script>
import { getConsumeList, getCanteenList, getDishList } from '@/api/canteen'
import { exportCanteenConsume } from '@/api/export'
export default {
  name: 'ConsumeRecord',
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0,
      canteens: [], dishes: [], searchUserId: ''
    }
  },
  computed: { isStaff() { return this.$store.getters.isStaff } },
  created() { this.loadAuxData(); this.loadData() },
  methods: {
    async loadAuxData() {
      try {
        const [canteensRes, dishesRes] = await Promise.all([
          getCanteenList({ current: 1, size: 200 }),
          getDishList({ current: 1, size: 200 })
        ])
        this.canteens = canteensRes.data.records
        this.dishes = dishesRes.data.records
      } catch (e) {}
    },
    getCanteenName(id) { const c = this.canteens.find(item => item.id === id); return c ? c.canteenName : id },
    getDishName(id) { const d = this.dishes.find(item => item.id === id); return d ? d.dishName : id },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.searchUserId) {
        params.userId = this.searchUserId
      } else if (!this.isStaff) {
        const userId = this.$store.getters.user ? this.$store.getters.user.id : null
        if (userId) params.userId = userId
      }
      const res = await getConsumeList(params)
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    async handleExport(format) {
      try {
        const res = await exportCanteenConsume(format)
        const blob = new Blob([res], { type: format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '餐饮消费汇总表.' + (format === 'pdf' ? 'pdf' : 'xlsx')
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (e) { this.$message.error('导出失败') }
    }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
