<template>
  <div class="salary-page">
    <div class="page-section">
      <h2 class="page-title">薪资与深造分析</h2>
      <BoxPlotChart />
    </div>

    <div class="page-section">
      <div class="charts-grid">
        <SalaryUniChart />
        <DegreeSalaryChart />
        <ScatterChart />
        <FurtherEduChart />
      </div>
    </div>
  </div>
</template>

<script setup>
import BoxPlotChart from '../components/charts/BoxPlotChart.vue'
import SalaryUniChart from '../components/charts/SalaryUniChart.vue'
import DegreeSalaryChart from '../components/charts/DegreeSalaryChart.vue'
import ScatterChart from '../components/charts/ScatterChart.vue'
import FurtherEduChart from '../components/charts/FurtherEduChart.vue'
</script>
