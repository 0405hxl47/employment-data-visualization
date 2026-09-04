<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="header-brand">
        <h1 class="header-title">高校毕业生就业数据分析平台</h1>
      </div>
      <nav class="header-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="nav-item--active"
        >
          {{ item.label }}
        </router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
const navItems = [
  { path: '/overview', label: '首页总览' },
  { path: '/industry', label: '行业与岗位分析' },
  { path: '/major', label: '专业分析' },
  { path: '/geography', label: '地域流向分析' },
  { path: '/salary', label: '薪资与深造分析' }
]
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #1a237e 0%, #283593 50%, #3949ab 100%);
  color: #fff;
  box-shadow: 0 2px 12px rgba(26, 35, 126, 0.3);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.header-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
}

.header-nav {
  display: flex;
  gap: 4px;
}

.nav-item {
  color: rgba(255, 255, 255, 0.78);
  text-decoration: none;
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 4px 4px 0 0;
  border-bottom: 3px solid transparent;
  transition: all 0.2s;
  white-space: nowrap;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.nav-item--active {
  color: #fff;
  border-bottom-color: #ffab00;
  background: rgba(255, 255, 255, 0.1);
}

@media (max-width: 900px) {
  .header-inner {
    flex-direction: column;
    height: auto;
    padding: 12px;
    gap: 8px;
  }

  .header-nav {
    flex-wrap: wrap;
    justify-content: center;
  }

  .nav-item {
    padding: 6px 12px;
    font-size: 13px;
  }
}
</style>
