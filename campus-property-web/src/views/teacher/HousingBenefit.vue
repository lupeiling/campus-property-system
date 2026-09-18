<template>
  <div>
    <div class="apple-hero-section">
      <h1 class="apple-page-title">住房待遇</h1>
      <p class="apple-caption">按职称等级查看住房待遇标准</p>
    </div>
    <el-card v-if="myBenefit" style="margin-bottom:20px;border-left:4px solid #0071e3">
      <div style="font-size:16px;font-weight:600;margin-bottom:12px;color:#1d1d1f">我的住房待遇</div>
      <el-descriptions :column="2" border size="medium">
        <el-descriptions-item label="职称等级">{{ myBenefit.title }}</el-descriptions-item>
        <el-descriptions-item label="住房类型">{{ myBenefit.benefit.housingType }}</el-descriptions-item>
        <el-descriptions-item label="公寓名称">{{ myBenefit.benefit.apartmentName }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ myBenefit.benefit.location }}</el-descriptions-item>
        <el-descriptions-item label="建筑面积">{{ myBenefit.benefit.area }} ㎡</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card>
      <div slot="header" style="font-weight:600;font-size:16px">各职称等级住房待遇标准</div>
      <el-table :data="benefitList" stripe border>
        <el-table-column prop="titleLevel" label="职称等级" width="110" />
        <el-table-column prop="housingType" label="住房类型" width="140" />
        <el-table-column label="公寓名称" width="120">
          <template slot-scope="{row}">{{ row.apartmentName }}（{{ row.location }}）</template>
        </el-table-column>
        <el-table-column prop="area" label="建筑面积（㎡）" width="130" />
      </el-table>
    </el-card>
  </div>
</template>
<script>
import { getTeacherHousingBenefit, getAllHousingBenefits } from '@/api/newModules'
export default {
  name: 'TeacherHousingBenefit',
  data() {
    return { myBenefit: null, benefitList: [] }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const [benefitRes, listRes] = await Promise.all([getTeacherHousingBenefit(), getAllHousingBenefits()])
        this.myBenefit = benefitRes.data
        this.benefitList = listRes.data
      } catch (e) { this.$message.error('加载失败') }
    }
  }
}
</script>
<style scoped>
.apple-hero-section { margin-bottom: 16px; }
</style>
