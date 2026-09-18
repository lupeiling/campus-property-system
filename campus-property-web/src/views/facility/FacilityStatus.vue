<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">设施状态</h1><p class="apple-caption">校园公共设施运行状态一览</p></div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="facilityName" label="设施名称" min-width="140" />
      <el-table-column prop="location" label="位置" min-width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="{row}"><el-tag :type="['danger','success','warning'][row.status]">{{ ['不可用','正常','维修中'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100" />
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </el-card>
</template>

<script>
import { getFacilityList } from '@/api/facility'
export default {
  name: 'FacilityStatus',
  data() { return { tableData: [], current: 1, size: 10, total: 0 } },
  created() { this.loadData() },
  methods: {
    async loadData() {
      const res = await getFacilityList({ current: this.current, size: this.size })
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() }
  }
}
</script>

<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
