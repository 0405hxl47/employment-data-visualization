import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/overview' },
  {
    path: '/overview',
    name: 'Overview',
    component: () => import('../views/OverviewPage.vue'),
    meta: { title: '首页总览' }
  },
  {
    path: '/industry',
    name: 'Industry',
    component: () => import('../views/IndustryPage.vue'),
    meta: { title: '行业与岗位分析' }
  },
  {
    path: '/major',
    name: 'Major',
    component: () => import('../views/MajorPage.vue'),
    meta: { title: '专业分析' }
  },
  {
    path: '/geography',
    name: 'Geography',
    component: () => import('../views/GeographyPage.vue'),
    meta: { title: '地域流向分析' }
  },
  {
    path: '/salary',
    name: 'Salary',
    component: () => import('../views/SalaryPage.vue'),
    meta: { title: '薪资与深造分析' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 高校毕业生就业数据分析平台`
  }
})

export default router
