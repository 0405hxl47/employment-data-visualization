<template>
  <div class="chart-card full-width">
    <div class="card-title">就业趋势变化（薪资 & 就业率）</div>
    <BaseChart :option="chartOption" height="400px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getTrend } from '../../api'
import { useFilters } from '../../composables/useFilters'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getTrend(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const years = data.map(d => String(d.graduation_year) + '届')
    const salaries = data.map(d => d.avg_salary)
    const rates = data.map(d => d.avg_rate)
    const counts = data.map(d => d.record_count)

    chartOption.value = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' },
        formatter: (params) => {
          const idx = params[0].dataIndex
          let html = `<strong>${params[0].axisValue}</strong><br/>`
          params.forEach(p => {
            const marker = p.marker
            if (p.seriesName === '平均薪资') {
              html += `${marker} ${p.seriesName}: ¥${p.value}<br/>`
            } else {
              html += `${marker} ${p.seriesName}: ${p.value}%<br/>`
            }
          })
          html += `数据量: ${counts[idx]} 条记录`
          return html
        }
      },
      legend: { data: ['平均薪资', '平均就业率'], top: 5 },
      grid: { left: 60, right: 60, bottom: 30, top: 50 },
      xAxis: {
        type: 'category',
        data: years,
        axisLabel: { fontSize: 12 }
      },
      yAxis: [
        {
          type: 'value',
          name: '薪资 (元/月)',
          position: 'left',
          axisLabel: { formatter: '¥{value}' }
        },
        {
          type: 'value',
          name: '就业率 (%)',
          position: 'right',
          min: 0,
          max: 100,
          axisLabel: { formatter: '{value}%' }
        }
      ],
      series: [
        {
          name: '平均薪资',
          type: 'line',
          data: salaries,
          yAxisIndex: 0,
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: { color: '#3949ab' },
          label: { show: true, formatter: '¥{c}', fontSize: 11 }
        },
        {
          name: '平均就业率',
          type: 'line',
          data: rates,
          yAxisIndex: 1,
          smooth: true,
          symbol: 'diamond',
          symbolSize: 8,
          itemStyle: { color: '#e53935' },
          label: { show: true, formatter: '{c}%', fontSize: 11 }
        }
      ]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
