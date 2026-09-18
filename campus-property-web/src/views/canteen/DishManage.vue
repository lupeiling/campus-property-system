<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">菜品管理</h1><p class="apple-caption">管理食堂菜品</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>菜品列表</span><el-button type="primary" size="small" @click="showAddDialog">新增菜品</el-button></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-model="canteenId" placeholder="选择食堂" clearable @change="loadData"><el-option v-for="c in canteens" :key="c.id" :label="c.canteenName" :value="c.id" /></el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" /><el-table-column prop="dishName" label="菜品名称" width="150" /><el-table-column prop="category" label="分类" width="100" /><el-table-column prop="price" label="价格" width="80" /><el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80"><template slot-scope="{row}"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="180"><template slot-scope="{row}"><el-button size="small" @click="handleEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button></template></el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="食堂"><el-select v-model="editForm.canteenId" style="width:100%"><el-option v-for="c in canteens" :key="c.id" :label="c.canteenName" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="菜品名称"><el-input v-model="editForm.dishName" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="editForm.category" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="editForm.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getCanteenList, getDishList, addDish, updateDish, deleteDish } from '@/api/canteen'
export default {
  name: 'DishManage',
  data() { return { tableData: [], canteens: [], canteenId: null, current: 1, size: 10, total: 0, showDialog: false, dialogTitle: '新增菜品', editForm: { canteenId: null, dishName: '', category: '', price: 0, description: '', status: 1 } } },
  created() { this.loadCanteens(); this.loadData() },
  methods: {
    async loadCanteens() { const res = await getCanteenList({ current: 1, size: 100 }); this.canteens = res.data.records },
    async loadData() { const res = await getDishList({ current: this.current, size: this.size, canteenId: this.canteenId }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.dialogTitle = '新增菜品'; this.editForm = { canteenId: null, dishName: '', category: '', price: 0, description: '', status: 1 }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑菜品'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增菜品') await addDish(this.editForm); else await updateDish(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteDish(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
