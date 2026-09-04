<template>
  <div class="industry-page">
    <div class="page-section">
      <h2 class="page-title">行业与岗位分析</h2>
      <div class="charts-grid">
        <IndustryBarChart mode="count" />
        <IndustryBarChart mode="salary" />
        <SunburstChart />
        <WordCloudChart />
      </div>
    </div>
  </div>
</template>

<script setup>
import IndustryBarChart from '../components/charts/IndustryBarChart.vue'
import SunburstChart from '../components/charts/SunburstChart.vue'
import WordCloudChart from '../components/charts/WordCloudChart.vue'
</script>
