<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">采购管理</h1><p class="apple-caption">物资采购申请与审批</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>采购管理</span><el-button type="primary" size="small" @click="showApplyDialog">申请采购</el-button></div>
    <el-table :data="tableData" stripe>
      <el-table-column type="expand">
        <template slot-scope="{row}">
          <el-table :data="row.items" size="mini" style="margin:8px 0 8px 48px;width:calc(100% - 56px)">
            <el-table-column label="物资ID" width="80">
              <template slot-scope="{row:item}">{{ item.materialId }}</template>
            </el-table-column>
            <el-table-column label="物资名称" min-width="140">
              <template slot-scope="{row:item}">{{ getMaterialName(item.materialId) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="unitPrice" label="单价" width="100" />
            <el-table-column prop="subtotal" label="小计" width="100" />
            <el-table-column prop="remark" label="备注" min-width="100" />
          </el-table>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="purchaseNo" label="采购单号" width="180" />
      <el-table-column prop="applicantName" label="申请人" width="100" />
      <el-table-column prop="title" label="标题" width="130" />
      <el-table-column prop="totalAmount" label="总金额" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="{row}"><el-tag :type="['warning','success','danger','primary',''][row.status]">{{ ['待审批','已审批','已拒绝','采购中','已完成'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" min-width="160" />
      <el-table-column label="操作" width="280">
        <template slot-scope="{row}">
          <el-button size="mini" @click="viewDetail(row)">查看明细</el-button>
          <el-button v-if="isAdmin && row.status === 0" size="mini" type="success" @click="handleApprove(row, 1)">审批</el-button>
          <el-button v-if="isAdmin && row.status === 0" size="mini" type="danger" @click="handleApprove(row, 2)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog title="申请采购" :visible.sync="showDialog" width="600px">
      <el-form :model="applyForm" label-position="top">
        <el-form-item label="标题"><el-input v-model="applyForm.title" /></el-form-item>
        <el-form-item label="采购明细">
          <div v-for="(item, idx) in applyForm.items" :key="idx" style="display:flex;gap:5px;margin-bottom:5px;align-items:center">
            <el-select v-model="item.materialId" placeholder="选择物资" filterable style="width:160px">
              <el-option v-for="m in materials" :key="m.id" :label="m.materialName" :value="m.id" />
            </el-select>
            <el-input-number v-model="item.quantity" :min="1" placeholder="数量" style="width:120px" />
            <el-input-number v-model="item.unitPrice" :min="0" :precision="2" placeholder="单价" style="width:120px" />
            <el-input v-model="item.remark" placeholder="备注" style="width:100px" />
            <el-button type="danger" icon="el-icon-delete" circle @click="applyForm.items.splice(idx, 1)" />
          </div>
          <el-button type="primary" size="small" @click="applyForm.items.push({ materialId: null, quantity: 1, unitPrice: 0, remark: '' })">添加明细</el-button>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleApply">提交</el-button></span>
    </el-dialog>
    <el-dialog title="采购明细" :visible.sync="showDetailDialog" width="700px">
      <div v-if="detailRow" style="margin-bottom:16px">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="采购单号">{{ detailRow.purchaseNo }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ detailRow.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="总金额">{{ detailRow.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ ['待审批','已审批','已拒绝','采购中','已完成'][detailRow.status] }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-table :data="detailItems" stripe>
        <el-table-column prop="materialId" label="物资ID" width="80" />
        <el-table-column label="物资名称" min-width="140">
          <template slot-scope="{row}">{{ getMaterialName(row.materialId) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="subtotal" label="小计" width="100" />
        <el-table-column prop="remark" label="备注" min-width="100" />
      </el-table>
    </el-dialog>
  </el-card>
</template>
<script>
import { getPurchaseList, applyPurchase, getPurchaseItems, approvePurchase, getMaterialList } from '@/api/material'
export default {
  name: 'PurchaseManage',
  data() {
    return {
      tableData: [], materials: [], current: 1, size: 10, total: 0,
      showDialog: false,
      applyForm: { title: '', items: [{ materialId: null, quantity: 1, unitPrice: 0, remark: '' }] },
      showDetailDialog: false, detailItems: [], detailRow: null
    }
  },
  computed: { isAdmin() { return this.$store.getters.isAdmin } },
  created() { this.loadMaterials(); this.loadData() },
  methods: {
    async loadMaterials() { try { const res = await getMaterialList({ current: 1, size: 200 }); this.materials = res.data.records } catch (e) {} },
    getMaterialName(id) { const m = this.materials.find(item => item.id === id); return m ? m.materialName : id },
    async loadData() {
      const res = await getPurchaseList({ current: this.current, size: this.size })
      this.tableData = res.data.records
      this.total = res.data.total
      for (const row of this.tableData) {
        if (!row.items) {
          try {
            const itemRes = await getPurchaseItems(row.id)
            this.$set(row, 'items', itemRes.data || [])
          } catch (e) {
            this.$set(row, 'items', [])
          }
        }
      }
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    showApplyDialog() { this.applyForm = { title: '', items: [{ materialId: null, quantity: 1, unitPrice: 0, remark: '' }] }; this.showDialog = true },
    async handleApply() { try { await applyPurchase(this.applyForm); this.$message.success('提交成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    async viewDetail(row) {
      try {
        const res = await getPurchaseItems(row.id)
        this.detailItems = res.data || []
        this.detailRow = row
        this.showDetailDialog = true
      } catch (e) {
        this.$message.error('获取明细失败')
      }
    },
    async handleApprove(row, status) {
      try {
        const remark = status === 1 ? '审批通过' : '审批拒绝'
        await approvePurchase(row.id, { status, remark })
        this.$message.success('操作成功'); this.loadData()
      } catch (e) {}
    }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
