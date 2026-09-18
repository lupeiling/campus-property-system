<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">住房情况</h1>
      <p class="apple-caption">教师已选住房情况</p>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="userId" label="教师ID" width="80" />
      <el-table-column prop="apartmentName" label="公寓名称" width="140" />
      <el-table-column prop="location" label="位置" min-width="180" />
      <el-table-column prop="titleLevel" label="职称等级" width="100" />
      <el-table-column label="月租金" width="100">
        <template slot-scope="{row}">¥{{ row.rent }}</template>
      </el-table-column>
      <el-table-column label="物业费/月" width="110">
        <template slot-scope="{row}">¥{{ row.propertyFee }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '在住' : '退房' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </div>
</template>
<script>
import { getTeacherHousingList } from '@/api/newModules'
export default {
  name: 'StaffHousingView',
  data() {
    return { tableData: [], current: 1, size: 10, total: 0 }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const res = await getTeacherHousingList({ current: this.current, size: this.size })
        this.tableData = res.data.records; this.total = res.data.total
      } catch (e) { this.$message.error('加载失败') }
    },
    handlePageChange(page) { this.current = page; this.loadData() }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
