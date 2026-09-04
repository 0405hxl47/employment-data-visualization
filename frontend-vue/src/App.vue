<template>
  <div class="app">
    <AppHeader />
    <div class="app-container">
      <BreadcrumbNav />
      <FilterBar />
      <router-view />
    </div>
  </div>
</template>

<script setup>
import AppHeader from './components/AppHeader.vue'
import BreadcrumbNav from './components/BreadcrumbNav.vue'
import FilterBar from './components/FilterBar.vue'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background: #f0f2f5;
  color: #333;
}

.app-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 24px 32px;
}

.page-section {
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a237e;
  margin-bottom: 16px;
  padding-left: 12px;
  border-left: 4px solid #3949ab;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.chart-card .card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.full-width {
  grid-column: 1 / -1;
}

.chart-wrapper {
  height: 380px;
}

.chart-wrapper.tall {
  height: 480px;
}

.chart-wrapper.xtall {
  height: 560px;
}

.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 14px;
}

@media (max-width: 900px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .app-container {
    padding: 12px 12px 24px;
  }
}
</style>
