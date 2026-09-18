<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">设施管理</h1><p class="apple-caption">管理校园公共设施</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>设施信息管理</span><el-button v-if="isAdmin" type="primary" size="small" @click="showAddDialog">新增设施</el-button></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-input v-model="keyword" placeholder="搜索设施名称/编号" style="width:250px" clearable @keyup.enter.native="loadData" />
      <el-select v-model="categoryFilter" placeholder="分类筛选" clearable @change="loadData">
        <el-option label="电梯" value="电梯" /><el-option label="空调" value="空调" /><el-option label="照明" value="照明" /><el-option label="消防" value="消防" /><el-option label="监控" value="监控" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="facilityNo" label="编号" width="100" />
      <el-table-column prop="facilityName" label="名称" width="150" />
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column prop="location" label="位置" width="120" />
      <el-table-column prop="status" label="状态" width="90">
        <template slot-scope="{row}"><el-tag :type="['danger','success','warning'][row.status]">{{ ['不可用','正常','维修中'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column v-if="isAdmin" label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog v-if="isAdmin" :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="编号"><el-input v-model="editForm.facilityNo" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="editForm.facilityName" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="editForm.category" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="editForm.location" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="editForm.status" style="width:100%"><el-option label="不可用" :value="0" /><el-option label="正常" :value="1" /><el-option label="维修中" :value="2" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getFacilityList, addFacility, updateFacility, deleteFacility } from '@/api/facility'
export default {
  name: 'FacilityManage',
  data() {
    return {
      tableData: [], current: 1, size: 10, total: 0, keyword: '', categoryFilter: '',
      showDialog: false, dialogTitle: '新增设施',
      editForm: { facilityNo: '', facilityName: '', category: '', location: '', status: 1, description: '' }
    }
  },
  computed: { isAdmin() { return this.$store.getters.isAdmin } },
  created() { this.loadData() },
  methods: {
    async loadData() { const res = await getFacilityList({ current: this.current, size: this.size, keyword: this.keyword, category: this.categoryFilter }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.dialogTitle = '新增设施'; this.editForm = { facilityNo: '', facilityName: '', category: '', location: '', status: 1, description: '' }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑设施'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增设施') await addFacility(this.editForm); else await updateFacility(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteFacility(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
