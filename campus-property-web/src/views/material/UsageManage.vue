<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">领用记录</h1><p class="apple-caption">物资领用与消耗记录</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>物资领用记录</span></div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="物资名称" width="140">
        <template slot-scope="{row}">{{ getMaterialName(row.materialId) }}</template>
      </el-table-column>
      <el-table-column label="领用人" width="160">
        <template slot-scope="{row}">{{ getUserName(row.userId) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="purpose" label="用途" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '使用中' : '已归还' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="领用时间" width="160" />
      <el-table-column v-if="isStaffOnly" label="操作" width="100">
        <template slot-scope="{row}">
          <el-button v-if="row.status === 1" size="small" type="warning" @click="handleReturn(row)">归还</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
  </el-card>
</template>
<script>
import { getUsageList, getMaterialList, returnMaterial } from '@/api/material'
import { getUserList } from '@/api/user'
export default {
  name: 'UsageManage',
  data() { return { tableData: [], materials: [], users: [], current: 1, size: 10, total: 0 } },
  computed: {
    isAdmin() { return this.$store.getters.isAdmin },
    isStaffOnly() { return this.$store.getters.role === 'STAFF' },
    userId() { return this.$store.getters.user ? this.$store.getters.user.id : null }
  },
  created() { this.loadMaterials(); this.loadUsers(); this.loadData() },
  methods: {
    async loadMaterials() { try { const res = await getMaterialList({ current: 1, size: 200 }); this.materials = res.data.records } catch (e) {} },
    async loadUsers() {
      if (!this.isAdmin) return
      try { const res = await getUserList({ current: 1, size: 200 }); this.users = res.data.records } catch (e) {}
    },
    getMaterialName(id) { const m = this.materials.find(item => item.id === id); return m ? m.materialName : id },
    getUserName(id) {
      if (this.isAdmin) {
        const u = this.users.find(item => item.id === id)
        return u ? `${u.realName || u.username}（ID:${id}）` : `ID:${id}`
      }
      const currentUser = this.$store.getters.user
      if (currentUser && currentUser.id === id) {
        return `${currentUser.realName || currentUser.username}（ID:${id}）`
      }
      return `ID:${id}`
    },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (!this.isAdmin && this.userId) params.userId = this.userId
      const res = await getUsageList(params)
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    async handleReturn(row) {
      try {
        await this.$confirm('确认归还该物资？归还后库存将相应增加。', '提示', { type: 'warning' })
        await returnMaterial(row.id)
        this.$message.success('归还成功')
        this.loadMaterials()
        this.loadData()
      } catch (e) {}
    }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
