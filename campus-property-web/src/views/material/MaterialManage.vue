<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">物资管理</h1><p class="apple-caption">管理物资库存信息</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>物资库存管理</span><div style="display:flex;gap:8px"><el-dropdown v-if="isAdmin" @command="handleExport"><el-button size="small">导出<i class="el-icon-arrow-down el-icon--right"></i></el-button><el-dropdown-menu slot="dropdown"><el-dropdown-item command="excel">导出 Excel</el-dropdown-item><el-dropdown-item command="pdf">导出 PDF</el-dropdown-item></el-dropdown-menu></el-dropdown><el-button v-if="isAdmin" type="primary" size="small" @click="showAddDialog">新增物资</el-button></div></div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-input v-model="keyword" placeholder="搜索物资名称/编号" style="width:250px" clearable @keyup.enter.native="loadData" />
      <el-select v-model="categoryId" placeholder="分类筛选" clearable @change="loadData">
        <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="materialNo" label="编号" width="100" />
      <el-table-column prop="materialName" label="名称" width="150" />
      <el-table-column prop="specification" label="规格" min-width="120" />
      <el-table-column prop="unit" label="单位" width="60" />
      <el-table-column prop="stockQuantity" label="库存" width="80">
        <template slot-scope="{row}"><span :style="{color: row.stockQuantity <= row.minQuantity ? 'red' : ''}">{{ row.stockQuantity }}</span></template>
      </el-table-column>
      <el-table-column prop="minQuantity" label="最低库存" width="90" />
      <el-table-column prop="unitPrice" label="单价" width="80" />
      <el-table-column v-if="isAdmin" label="操作" width="180">
        <template slot-scope="{row}">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column v-if="isStaffOnly" label="操作" width="200">
        <template slot-scope="{row}">
          <el-button size="small" type="success" @click="showUseDialog(row)">领用</el-button>
          <el-button size="small" type="warning" @click="showPurchaseDialog(row)">采购</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog v-if="isAdmin" :title="dialogTitle" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="编号"><el-input v-model="editForm.materialNo" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="editForm.materialName" /></el-form-item>
        <el-form-item label="分类"><el-select v-model="editForm.categoryId" style="width:100%"><el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="规格"><el-input v-model="editForm.specification" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="editForm.unit" /></el-form-item>
        <el-form-item label="库存数量"><el-input-number v-model="editForm.stockQuantity" :min="0" /></el-form-item>
        <el-form-item label="最低库存"><el-input-number v-model="editForm.minQuantity" :min="0" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="editForm.unitPrice" :min="0" :precision="2" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>

    <el-dialog title="领用物资" :visible.sync="useDialogVisible" width="440px">
      <el-form :model="useForm" label-position="top">
        <el-form-item label="物资名称"><el-input :value="useForm.materialName" disabled /></el-form-item>
        <el-form-item label="领用数量"><el-input-number v-model="useForm.quantity" :min="1" :max="useForm.maxQuantity" /></el-form-item>
        <el-form-item label="用途"><el-input v-model="useForm.purpose" type="textarea" :rows="2" placeholder="请填写领用用途" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="useDialogVisible = false">取消</el-button><el-button type="primary" @click="handleUse">确认领用</el-button></span>
    </el-dialog>

    <el-dialog title="申请采购" :visible.sync="purchaseDialogVisible" width="500px">
      <el-form :model="purchaseForm" label-position="top">
        <el-form-item label="物资名称"><el-input :value="purchaseForm.materialName" disabled /></el-form-item>
        <el-form-item label="采购数量"><el-input-number v-model="purchaseForm.quantity" :min="1" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="purchaseForm.unitPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="总金额"><el-input :value="totalAmount" disabled /></el-form-item>
        <el-form-item label="用途"><el-input v-model="purchaseForm.purpose" type="textarea" :rows="2" placeholder="请填写采购用途" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="purchaseDialogVisible = false">取消</el-button><el-button type="primary" @click="handlePurchase">提交采购</el-button></span>
    </el-dialog>
  </el-card>
</template>
<script>
import { getMaterialList, addMaterial, updateMaterial, deleteMaterial, getCategoryList, applyPurchase, useMaterial } from '@/api/material'
import { exportMaterial } from '@/api/export'
export default {
  name: 'MaterialManage',
  data() {
    return {
      tableData: [], categories: [], keyword: '', categoryId: null,
      current: 1, size: 10, total: 0,
      showDialog: false, dialogTitle: '新增物资',
      editForm: { materialNo: '', materialName: '', categoryId: null, specification: '', unit: '', stockQuantity: 0, minQuantity: 0, unitPrice: 0 },
      useDialogVisible: false, useForm: { materialId: null, materialName: '', quantity: 1, maxQuantity: 0, purpose: '' },
      purchaseDialogVisible: false, purchaseForm: { materialId: null, materialName: '', quantity: 1, unitPrice: 0, purpose: '' }
    }
  },
  computed: {
    isAdmin() { return this.$store.getters.isAdmin },
    isStaffOnly() { return this.$store.getters.role === 'STAFF' },
    totalAmount() { return (this.purchaseForm.quantity * this.purchaseForm.unitPrice).toFixed(2) }
  },
  created() { this.loadCategories(); this.loadData() },
  methods: {
    async loadCategories() { const res = await getCategoryList(); this.categories = res.data },
    async loadData() { const res = await getMaterialList({ current: this.current, size: this.size, keyword: this.keyword, categoryId: this.categoryId }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.dialogTitle = '新增物资'; this.editForm = { materialNo: '', materialName: '', categoryId: null, specification: '', unit: '', stockQuantity: 0, minQuantity: 0, unitPrice: 0 }; this.showDialog = true },
    handleEdit(row) { this.dialogTitle = '编辑物资'; this.editForm = { ...row }; this.showDialog = true },
    async handleSave() { try { if (this.dialogTitle === '新增物资') await addMaterial(this.editForm); else await updateMaterial(this.editForm); this.$message.success('操作成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async handleDelete(row) { try { await this.$confirm('确认删除？', '提示', { type: 'warning' }); await deleteMaterial(row.id); this.$message.success('删除成功'); this.loadData() } catch (e) {} },
    showUseDialog(row) { this.useForm = { materialId: row.id, materialName: row.materialName, quantity: 1, maxQuantity: row.stockQuantity, purpose: '' }; this.useDialogVisible = true },
    async handleUse() {
      try {
        await useMaterial({ materialId: this.useForm.materialId, quantity: this.useForm.quantity, purpose: this.useForm.purpose })
        this.$message.success('领用成功'); this.useDialogVisible = false; this.loadData()
      } catch (e) {}
    },
    showPurchaseDialog(row) { this.purchaseForm = { materialId: row.id, materialName: row.materialName, quantity: 1, unitPrice: row.unitPrice, purpose: '' }; this.purchaseDialogVisible = true },
    async handlePurchase() {
      try {
        await applyPurchase({
          title: '采购申请 - ' + this.purchaseForm.materialName,
          items: [{ materialId: this.purchaseForm.materialId, quantity: this.purchaseForm.quantity, unitPrice: this.purchaseForm.unitPrice, remark: this.purchaseForm.purpose }]
        })
        this.$message.success('采购申请已提交'); this.purchaseDialogVisible = false
      } catch (e) {}
    },
    async handleExport(format) {
      try {
        const res = await exportMaterial(format)
        const blob = new Blob([res], { type: format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '物资库存清单.' + (format === 'pdf' ? 'pdf' : 'xlsx')
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (e) { this.$message.error('导出失败') }
    }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
