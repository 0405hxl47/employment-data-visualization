<template>
  <div class="chart-card">
    <div class="card-title">就业率 vs 薪资散点图</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getScatter } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { DEGREE_COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getScatter(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    // Group by degree
    const groups = {}
    data.forEach(d => {
      const deg = d.degree || '未知'
      if (!groups[deg]) groups[deg] = []
      groups[deg].push(d)
    })

    const series = Object.entries(groups).map(([degree, items]) => ({
      name: degree,
      type: 'scatter',
      symbolSize: 8,
      itemStyle: {
        color: DEGREE_COLORS[degree] || '#3949ab',
        opacity: 0.7
      },
      data: items.map(d => ({
        value: [d.employment_rate, d.salary_avg],
        university: d.university_name,
        major: d.major_name,
        rate: d.employment_rate,
        salary: d.salary_avg
      }))
    }))

    chartOption.value = {
      tooltip: {
        formatter: (params) => {
          const d = params.data
          return `${d.university} - ${d.major}<br/>
            就业率: ${d.rate}%<br/>
            平均薪资: ¥${d.salary}`
        }
      },
      legend: { top: 5, data: Object.keys(groups) },
      grid: { left: 60, right: 30, top: 40, bottom: 40 },
      xAxis: {
        type: 'value',
        name: '就业率 (%)',
        min: 0,
        max: 100,
        axisLabel: { formatter: '{value}%' }
      },
      yAxis: {
        type: 'value',
        name: '平均薪资 (元)',
        axisLabel: { formatter: '¥{value}' }
      },
      series
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
