<template>
  <el-card>
    <div class="apple-hero-section"><h1 class="apple-page-title">我有话说</h1><p class="apple-caption">意见反馈与建议</p></div>
    <el-form :model="form" label-position="top" style="max-width:600px">
      <el-form-item label="反馈类型">
        <el-radio-group v-model="form.type">
          <el-radio-button label="建议">建议</el-radio-button>
          <el-radio-button label="投诉">投诉</el-radio-button>
          <el-radio-button label="表扬">表扬</el-radio-button>
          <el-radio-button label="其他">其他</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="反馈内容"><el-input v-model="form.content" type="textarea" :rows="6" placeholder="请详细描述您的意见或建议" /></el-form-item>
      <el-form-item label="联系方式"><el-input v-model="form.contact" placeholder="选填，方便我们联系您" /></el-form-item>
      <el-form-item><el-button type="primary" @click="handleSubmit">提交反馈</el-button></el-form-item>
    </el-form>

    <div v-if="myFeedbacks.length > 0" style="margin-top:32px">
      <div class="section-title">我的反馈记录</div>
      <el-table :data="myFeedbacks" stripe>
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="回复" min-width="150">
          <template slot-scope="{row}">{{ row.reply || '暂无回复' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
      </el-table>
    </div>
  </el-card>
</template>

<script>
import { createFeedback, getFeedbackList } from '@/api/feedback'
export default {
  name: 'Feedback',
  data() { return { form: { type: '建议', content: '', contact: '' }, myFeedbacks: [] } },
  created() { this.loadMyFeedbacks() },
  methods: {
    async handleSubmit() {
      if (!this.form.content) { this.$message.warning('请填写反馈内容'); return }
      try {
        await createFeedback(this.form)
        this.$message.success('感谢您的反馈，我们会认真处理！')
        this.form = { type: '建议', content: '', contact: '' }
        this.loadMyFeedbacks()
      } catch (e) {}
    },
    async loadMyFeedbacks() {
      try {
        const res = await getFeedbackList({ current: 1, size: 50 })
        const userId = this.$store.getters.user ? this.$store.getters.user.id : null
        this.myFeedbacks = (res.data.records || []).filter(r => r.userId === userId)
      } catch (e) {}
    }
  }
}
</script>

<style scoped>.apple-hero-section { margin-bottom: 16px; } .section-title { font-size: 17px; font-weight: 600; margin-bottom: 12px; }</style>
