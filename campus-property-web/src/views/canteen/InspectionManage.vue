<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">卫生检查</h1><p class="apple-caption">食堂卫生安全检查记录</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center"><span>卫生检查记录</span><el-button type="primary" size="small" @click="showAddDialog">新增检查</el-button></div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="canteenId" label="食堂ID" width="80" />
      <el-table-column prop="inspectDate" label="检查日期" width="120" />
      <el-table-column prop="score" label="评分" width="80" />
      <el-table-column prop="hygieneStatus" label="卫生状况" width="100">
        <template slot-scope="{row}"><el-tag :type="['danger','success',''][row.hygieneStatus]">{{ ['不合格','合格','优秀'][row.hygieneStatus] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="issues" label="问题" show-overflow-tooltip />
      <el-table-column prop="rectification" label="整改措施" show-overflow-tooltip />
      <el-table-column label="操作" width="100">
        <template slot-scope="{row}">
          <el-button size="small" type="primary" @click="showDetailDialog(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />
    <el-dialog title="新增检查" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="食堂"><el-select v-model="editForm.canteenId" style="width:100%"><el-option v-for="c in canteens" :key="c.id" :label="c.canteenName" :value="c.id" /></el-select></el-form-item>
        <el-form-item label="检查日期"><el-date-picker v-model="editForm.inspectDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
        <el-form-item label="评分"><el-input-number v-model="editForm.score" :min="0" :max="100" /></el-form-item>
        <el-form-item label="卫生状况"><el-select v-model="editForm.hygieneStatus" style="width:100%"><el-option label="不合格" :value="0" /><el-option label="合格" :value="1" /><el-option label="优秀" :value="2" /></el-select></el-form-item>
        <el-form-item label="问题"><el-input v-model="editForm.issues" type="textarea" /></el-form-item>
        <el-form-item label="整改措施"><el-input v-model="editForm.rectification" type="textarea" /></el-form-item>
        <el-form-item label="检查图片">
          <el-upload action="" :http-request="handleUpload" :show-file-list="false" accept="image/*">
            <el-button size="small" type="primary">上传图片</el-button>
          </el-upload>
          <div v-if="editForm.images" style="margin-top:8px"><el-image :src="editForm.images" style="width:120px;height:80px" fit="cover" /><el-button type="text" style="color:#ff3b30;margin-left:8px" @click="editForm.images = ''">删除</el-button></div>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSave">确定</el-button></span>
    </el-dialog>
    <el-dialog title="检查详情" :visible.sync="detailDialogVisible" width="520px">
      <div v-if="detailRow">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="食堂ID">{{ detailRow.canteenId }}</el-descriptions-item>
          <el-descriptions-item label="检查日期">{{ detailRow.inspectDate }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ detailRow.score }}</el-descriptions-item>
          <el-descriptions-item label="卫生状况">
            <el-tag :type="['danger','success',''][detailRow.hygieneStatus]">{{ ['不合格','合格','优秀'][detailRow.hygieneStatus] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="问题">{{ detailRow.issues || '无' }}</el-descriptions-item>
          <el-descriptions-item label="整改措施">{{ detailRow.rectification || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.images" style="margin-top:16px">
          <p style="font-weight:600;margin-bottom:8px">检查图片</p>
          <el-image :src="detailRow.images" style="max-width:100%;max-height:400px;border-radius:8px" fit="contain" :preview-src-list="[detailRow.images]" />
        </div>
        <div v-else style="margin-top:16px;color:rgba(0,0,0,0.3)">暂无检查图片</div>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import { getCanteenList, getInspectionList, addInspection } from '@/api/canteen'
import { uploadImage } from '@/api/upload'
export default {
  name: 'InspectionManage',
  data() {
    return {
      tableData: [], canteens: [], current: 1, size: 10, total: 0, showDialog: false,
      editForm: { canteenId: null, inspectDate: '', score: 80, hygieneStatus: 1, issues: '', rectification: '', images: '' },
      detailDialogVisible: false, detailRow: null
    }
  },
  created() { this.loadCanteens(); this.loadData() },
  methods: {
    async loadCanteens() { const res = await getCanteenList({ current: 1, size: 100 }); this.canteens = res.data.records },
    async loadData() { const res = await getInspectionList({ current: this.current, size: this.size }); this.tableData = res.data.records; this.total = res.data.total },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.editForm = { canteenId: null, inspectDate: '', score: 80, hygieneStatus: 1, issues: '', rectification: '', images: '' }; this.showDialog = true },
    async handleUpload(opt) { try { const res = await uploadImage(opt.file); this.editForm.images = res.data; this.$message.success('上传成功') } catch (e) { this.$message.error('上传失败') } },
    async handleSave() { try { await addInspection(this.editForm); this.$message.success('添加成功'); this.showDialog = false; this.loadData() } catch (e) {} },
    showDetailDialog(row) { this.detailRow = row; this.detailDialogVisible = true }
  }
}
</script>
<style scoped>.apple-hero-section { margin-bottom: 16px; }</style>
