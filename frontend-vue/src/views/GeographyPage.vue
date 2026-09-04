<template>
  <div class="geography-page">
    <div class="page-section">
      <h2 class="page-title">地域流向分析</h2>
      <ChinaMapChart />
    </div>

    <div class="page-section">
      <div class="charts-grid">
        <ProvinceBarChart />
        <CityBarChart />
      </div>
    </div>

    <div class="page-section">
      <ProvinceUniChart />
    </div>
  </div>
</template>

<script setup>
import ChinaMapChart from '../components/charts/ChinaMapChart.vue'
import ProvinceBarChart from '../components/charts/ProvinceBarChart.vue'
import CityBarChart from '../components/charts/CityBarChart.vue'
import ProvinceUniChart from '../components/charts/ProvinceUniChart.vue'
</script>
