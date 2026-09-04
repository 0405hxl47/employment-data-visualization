<template>
  <div class="overview-page">
    <div class="page-section">
      <h2 class="page-title">数据概览</h2>
      <div class="stat-cards">
        <StatCard label="数据总量" :value="stats.total_records" suffix="条" color="#3949ab" />
        <StatCard label="覆盖高校" :value="stats.university_count" suffix="所" color="#e53935" />
        <StatCard label="专业数量" :value="stats.major_count" suffix="个" color="#43a047" />
        <StatCard label="平均实际薪资" :value="'¥' + (stats.avg_salary || 0)" color="#fb8c00" />
        <StatCard label="平均就业率" :value="stats.avg_employment_rate + '%'" color="#8e24aa" />
      </div>
    </div>

    <div class="page-section">
      <div class="charts-grid">
        <DonutChart />
        <TreemapChart />
      </div>
    </div>

    <div class="page-section">
      <SankeyChart />
    </div>

    <div class="page-section">
      <TrendLineChart />
    </div>

    <div class="page-section">
      <DataTable />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import StatCard from '../components/StatCard.vue'
import DonutChart from '../components/charts/DonutChart.vue'
import TreemapChart from '../components/charts/TreemapChart.vue'
import SankeyChart from '../components/charts/SankeyChart.vue'
import TrendLineChart from '../components/charts/TrendLineChart.vue'
import DataTable from '../components/DataTable.vue'
import { getOverview } from '../api'

const stats = ref({
  total_records: 0,
  university_count: 0,
  major_count: 0,
  avg_salary: 0,
  avg_employment_rate: 0
})

onMounted(async () => {
  try {
    const data = await getOverview()
    if (data) {
      stats.value = {
        total_records: data.total_records || 0,
        university_count: data.university_count || 0,
        major_count: data.major_count || 0,
        avg_salary: data.avg_salary || 0,
        avg_employment_rate: data.avg_employment_rate || 0
      }
    }
  } catch (e) {
    console.error('Failed to load overview stats:', e)
  }
})
</script>

<style scoped>
.stat-cards {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

@media (max-width: 900px) {
  .stat-cards {
    flex-direction: column;
  }
}
</style>
