<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">{{ isTeacher ? '住房报修' : '宿舍报修' }}</h1><p class="apple-caption">{{ isTeacher ? '住房维修申请与处理' : '宿舍维修申请与处理' }}</p></div>
    <div slot="header" style="display:flex;justify-content:space-between;align-items:center">
      <span>维修列表</span>
      <el-button v-if="!isStaff" type="primary" size="small" @click="showAddDialog">申请维修</el-button>
    </div>
    <div style="margin-bottom:15px;display:flex;gap:10px">
      <el-select v-if="!isStaffOnly" v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData">
        <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已拒绝" :value="3" />
      </el-select>
      <el-select v-if="isStaffOnly" v-model="statusFilter" placeholder="状态筛选" clearable @change="loadData">
        <el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" />
      </el-select>
    </div>
    <el-table :data="tableData" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column label="房间" min-width="140">
        <template slot-scope="{row}">{{ getRoomFullName(row.roomId) }}</template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90">
        <template slot-scope="{row}"><el-tag :type="['warning','primary','success','danger'][row.status]">{{ ['待处理','处理中','已完成','已拒绝'][row.status] }}</el-tag></template>
      </el-table-column>
      <el-table-column label="维修人员" width="100">
        <template slot-scope="{row}">{{ row.repairPerson || '未指派' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button size="small" type="primary" @click="showDetailDialog(row)">查看详情</el-button>
          <el-button v-if="isAdmin && row.status === 0" size="small" type="warning" @click="showAssignDialog(row)">派单</el-button>
          <el-button v-if="isStaffOnly && row.status === 1" size="small" type="success" @click="showHandleDialog(row)">处理</el-button>
          <el-button v-if="!isStaff && row.status === 2 && !row.evaluated" size="small" type="warning" @click="showEvalDialog(row)">评价</el-button>
          <el-tag v-if="!isStaff && row.status === 2 && row.evaluated" size="small" type="success">已评价</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;text-align:right" :current-page="current" :page-size="size" :total="total" layout="total, prev, pager, next" @current-change="handlePageChange" />

    <el-dialog :title="isTeacher ? '申请住房维修' : '申请维修'" :visible.sync="showDialog" width="500px">
      <el-form :model="editForm" label-position="top">
        <template v-if="isTeacher">
          <el-form-item label="住房区域">
            <el-select v-model="editForm.areaZone" placeholder="请选择区域" style="width:100%" @change="onAreaChange">
              <el-option label="东区" value="东区" /><el-option label="南区" value="南区" />
              <el-option label="西区" value="西区" /><el-option label="北区" value="北区" />
            </el-select>
          </el-form-item>
          <el-form-item label="楼栋号">
            <el-select v-model="editForm.buildingNo" placeholder="请先选择区域，再选择楼栋" style="width:100%" :disabled="!editForm.areaZone">
              <el-option v-for="b in teacherBuildings" :key="b" :label="b" :value="b" />
            </el-select>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="选择宿舍楼">
            <el-select v-model="editForm.buildingId" placeholder="请选择宿舍楼" filterable style="width:100%" @change="onBuildingChange">
              <el-option v-for="b in buildings" :key="b.id" :label="b.buildingName + ' (' + b.buildingNo + ')'" :value="b.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="选择房间">
            <el-select v-model="editForm.roomId" placeholder="请先选择宿舍楼" filterable style="width:100%" :disabled="!editForm.buildingId">
              <el-option v-for="r in filteredRooms" :key="r.id" :label="r.roomNo + ' (容量:' + r.capacity + '人)'" :value="r.id" />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="维修图片">
          <el-upload action="" :http-request="handleUpload" :show-file-list="false" accept="image/*">
            <el-button size="small" type="primary">上传图片</el-button>
          </el-upload>
          <div v-if="editForm.images" style="margin-top:8px">
            <el-image :src="editForm.images" style="width:120px;height:80px" fit="cover" />
            <el-button type="text" style="color:#ff3b30;margin-left:8px" @click="editForm.images = ''">删除</el-button>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="handleSubmit">提交</el-button></span>
    </el-dialog>

    <el-dialog title="派单" :visible.sync="assignDialogVisible" width="440px">
      <el-form :model="assignForm" label-position="top">
        <el-form-item label="指派维修人员">
          <el-select v-model="assignForm.repairPerson" placeholder="请选择维修人员" filterable style="width:100%">
            <el-option v-for="s in staffUsers" :key="s.id" :label="s.realName || s.username" :value="s.realName || s.username" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="assignDialogVisible = false">取消</el-button><el-button type="primary" @click="handleAssign">确认派单</el-button></span>
    </el-dialog>

    <el-dialog title="处理维修" :visible.sync="handleDialogVisible" width="500px">
      <el-form :model="handleForm" label-position="top">
        <el-form-item label="状态">
          <el-select v-model="handleForm.status" style="width:100%">
            <el-option label="已完成" :value="2" /><el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果"><el-input v-model="handleForm.repairResult" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="handleDialogVisible = false">取消</el-button><el-button type="primary" @click="handleHandleSubmit">确定</el-button></span>
    </el-dialog>

    <el-dialog title="维修详情" :visible.sync="detailDialogVisible" width="560px">
      <div v-if="detailRow">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="标题">{{ detailRow.title }}</el-descriptions-item>
          <el-descriptions-item label="房间">{{ getRoomFullName(detailRow.roomId) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="['warning','primary','success','danger'][detailRow.status]">{{ ['待处理','处理中','已完成','已拒绝'][detailRow.status] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="描述">{{ detailRow.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="维修人员">{{ detailRow.repairPerson || '未指派' }}</el-descriptions-item>
          <el-descriptions-item label="处理结果">{{ detailRow.repairResult || '无' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailRow.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="isStaff" label="申请人">
            {{ getApplicantInfo(detailRow.userId) }}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.images" style="margin-top:16px">
          <p style="font-weight:600;margin-bottom:8px">维修图片</p>
          <el-image :src="detailRow.images" style="max-width:100%;max-height:400px;border-radius:8px" fit="contain" :preview-src-list="[detailRow.images]" />
        </div>
      </div>
    </el-dialog>

    <el-dialog title="评价维修" :visible.sync="evalDialogVisible" width="440px">
      <el-form :model="evalForm" label-position="top">
        <el-form-item label="评分"><el-rate v-model="evalForm.score" show-text :texts="['很差','较差','一般','满意','非常满意']" /></el-form-item>
        <el-form-item label="评价内容"><el-input v-model="evalForm.content" type="textarea" :rows="3" placeholder="请输入您的评价" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button @click="evalDialogVisible = false">取消</el-button><el-button type="primary" @click="submitEval">提交评价</el-button></span>
    </el-dialog>
  </el-card>
</template>

<script>
import { getDormRepairList, addDormRepair, handleDormRepair, getRoomList, getBuildingList } from '@/api/dorm'
import { uploadImage } from '@/api/upload'
import { getUserList } from '@/api/user'
import request from '@/utils/request'

export default {
  name: 'DormRepairView',
  data() {
    return {
      tableData: [], buildings: [], rooms: [], staffUsers: [], allUsers: [],
      current: 1, size: 10, total: 0, statusFilter: null,
      showDialog: false, editForm: { buildingId: null, roomId: null, title: '', description: '', images: '' },
      assignDialogVisible: false, currentRow: null, assignForm: { repairPerson: '' },
      handleDialogVisible: false, handleForm: { status: 2, repairResult: '' },
      detailDialogVisible: false, detailRow: null,
      evalDialogVisible: false, evalForm: { score: 5, content: '' }
    }
  },
  computed: {
    isAdmin() { return this.$store.getters.role === 'ADMIN' },
    isStaff() { return this.$store.getters.isStaff },
    isStaffOnly() { return this.$store.getters.role === 'STAFF' },
    isTeacher() { return this.$store.getters.role === 'TEACHER' },
    filteredRooms() {
      if (!this.editForm.buildingId) return []
      return this.rooms.filter(r => r.buildingId === this.editForm.buildingId)
    },
    teacherBuildings() {
      const map = {
        '东区': ['东区1栋', '东区2栋', '东区3栋', '东区4栋', '东区5栋', '东区6栋', '东区7栋', '东区8栋', '东区9栋', '东区10栋'],
        '南区': ['南区1栋', '南区2栋', '南区3栋', '南区4栋', '南区5栋', '南区6栋', '南区7栋', '南区8栋', '南区9栋', '南区10栋'],
        '西区': ['西区1栋', '西区2栋', '西区3栋', '西区4栋', '西区5栋', '西区6栋', '西区7栋', '西区8栋', '西区9栋', '西区10栋'],
        '北区': ['北区1栋', '北区2栋', '北区3栋', '北区4栋', '北区5栋', '北区6栋', '北区7栋', '北区8栋', '北区9栋', '北区10栋']
      }
      return map[this.editForm.areaZone] || []
    }
  },
  created() {
    this.loadBuildings(); this.loadRooms(); this.loadData()
    if (this.isAdmin) { this.loadUsers() }
  },
  methods: {
    async loadBuildings() { const res = await getBuildingList({ current: 1, size: 200 }); this.buildings = res.data.records },
    async loadRooms() { const res = await getRoomList({ current: 1, size: 200 }); this.rooms = res.data.records },
    async loadUsers() {
      try {
        const res = await getUserList({ current: 1, size: 200 })
        this.allUsers = res.data.records
        this.staffUsers = res.data.records.filter(u => u.role === 'STAFF')
      } catch (e) {}
    },
    onBuildingChange() { this.editForm.roomId = null },
    onAreaChange() { this.editForm.buildingNo = '' },
    getBuildingName(id) { const b = this.buildings.find(item => item.id === id); return b ? b.buildingName : '' },
    getRoomFullName(roomId) {
      const r = this.rooms.find(item => item.id === roomId)
      if (!r) return roomId
      const bName = this.getBuildingName(r.buildingId)
      return bName ? bName + ' - ' + r.roomNo : r.roomNo
    },
    getApplicantInfo(userId) {
      const u = this.allUsers.find(item => item.id === userId)
      if (!u) return userId
      return (u.realName || u.username) + ' (' + u.username + ', ' + { ADMIN: '管理员', STAFF: '物业人员', TEACHER: '教师', STUDENT: '学生' }[u.role] + ')'
    },
    async loadData() {
      const params = { current: this.current, size: this.size }
      if (this.isStaffOnly) {
        const user = this.$store.getters.user
        if (user) params.repairPerson = user.realName || user.username
        if (!this.statusFilter) {
          params.statusList = '1,2'
        } else {
          params.status = this.statusFilter
        }
      } else {
        if (this.statusFilter !== null && this.statusFilter !== '') params.status = this.statusFilter
        const userId = this.$store.getters.user ? this.$store.getters.user.id : null
        if (userId && !this.isStaff) params.userId = userId
      }
      const res = await getDormRepairList(params)
      this.tableData = res.data.records; this.total = res.data.total
    },
    handlePageChange(page) { this.current = page; this.loadData() },
    showAddDialog() { this.editForm = { buildingId: null, roomId: null, areaZone: '', buildingNo: '', title: '', description: '', images: '' }; this.showDialog = true },
    async handleUpload(opt) { try { const res = await uploadImage(opt.file); this.editForm.images = res.data; this.$message.success('上传成功') } catch (e) { this.$message.error('上传失败') } },
    async handleSubmit() {
      try {
        const data = { ...this.editForm }
        if (this.isTeacher && data.areaZone && data.buildingNo) {
          data.description = `[住房位置：${data.areaZone} ${data.buildingNo}] ` + (data.description || '')
          delete data.areaZone
          delete data.buildingNo
        }
        await addDormRepair(data); this.$message.success('提交成功'); this.showDialog = false; this.loadData()
      } catch (e) {} },
    showAssignDialog(row) { this.currentRow = row; this.assignForm = { repairPerson: '' }; this.assignDialogVisible = true },
    async handleAssign() {
      try {
        await handleDormRepair(this.currentRow.id, { status: 1, repairPerson: this.assignForm.repairPerson, repairResult: '' })
        this.$message.success('派单成功'); this.assignDialogVisible = false; this.loadData()
      } catch (e) {}
    },
    showHandleDialog(row) { this.currentRow = row; this.handleForm = { status: 2, repairResult: '' }; this.handleDialogVisible = true },
    async handleHandleSubmit() {
      try {
        await handleDormRepair(this.currentRow.id, this.handleForm)
        this.$message.success('处理成功'); this.handleDialogVisible = false; this.loadData()
      } catch (e) {}
    },
    showDetailDialog(row) { this.detailRow = row; this.detailDialogVisible = true },
    showEvalDialog(row) { this.currentRow = row; this.evalForm = { score: 5, content: '' }; this.evalDialogVisible = true },
    async submitEval() {
      if (!this.currentRow) return
      try {
        await request({ url: '/dorm/repair/' + this.currentRow.id + '/evaluate', method: 'put' })
        this.$message.success('评价提交成功！')
        this.currentRow.evaluated = 1
        this.evalDialogVisible = false
      } catch (e) {}
    }
  }
}
</script>

<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
