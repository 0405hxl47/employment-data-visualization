<template>
  <div class="major-page">
    <div class="page-section">
      <h2 class="page-title">专业分析</h2>

      <el-alert
        v-if="warningCount > 0"
        :title="`就业率预警：共有 ${warningCount} 个专业就业率低于 60%，需重点关注`"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      />

      <LowRateChart />
    </div>

    <div class="page-section">
      <div class="charts-grid">
        <RoseChart />
        <MajorSalaryBar />
      </div>
    </div>

    <div class="page-section">
      <TopCitiesList />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import RoseChart from '../components/charts/RoseChart.vue'
import MajorSalaryBar from '../components/charts/MajorSalaryBar.vue'
import TopCitiesList from '../components/charts/TopCitiesList.vue'
import LowRateChart from '../components/charts/LowRateChart.vue'
import { getMajorAnalysis } from '../api'

const warningCount = ref(0)

async function fetchWarning() {
  try {
    const data = await getMajorAnalysis({})
    warningCount.value = data && data.low_rate_count != null ? data.low_rate_count : 0
  } catch {
    warningCount.value = 0
  }
}

onMounted(fetchWarning)
</script>
