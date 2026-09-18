<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">食堂管理</h1><p class="apple-caption">管理食堂信息</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>食堂列表</span><el-button v-if="isAdmin" type="primary" size="small" @click="showAddDialog">新增食堂</el-button></div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" /><el-table-column prop="canteenName" label="食堂名称" width="150" /><el-table-column prop="location" label="位置" width="150" /><el-table-column prop="floors" label="楼层数" width="80" /><el-table-column prop="description" label="描述" />
      <el-table-column v-if="isAdmin" label="操作" width="180">
        <template slot-scope="{row}"><el-button size="small" @click="handleEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="名称"><el-input v-model="editForm.canteenName" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="editForm.location" /></el-form-item>
        <el-form-item label="楼层数"><el-input-number v-model="editForm.floors" :min="1" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getCanteenList, addCanteen, updateCanteen, deleteCanteen } from '@/api/canteen'
export default {
  name: 'CanteenManage',
  computed: { isAdmin() { return this.$store.getters.isAdmin } },
  data() { return { tableData: [], current: 1, size: 10, total: 0, showDialog: false, dialogTitle: '新增食堂', editForm: { canteenName: '', location: '', floors: 2, description: '' } } },
  created() { this.loadData() },
  methods: {
    async loadData() { const res = await getCanteenList({ current: this.current, size: this.size }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.dialogTitle = '新增食堂'; this.editForm = { canteenName: '', location: '', floors: 2, description: '' }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑食堂'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增食堂') await addCanteen(this.editForm); else await updateCanteen(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteCanteen(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
