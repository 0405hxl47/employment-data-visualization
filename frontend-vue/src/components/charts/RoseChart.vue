<template>
  <div class="chart-card">
    <div class="card-title">行业就业分布（南丁格尔玫瑰图）</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getMajorAnalysis } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getMajorAnalysis(filterParams.value)
    const distribution = data && data.industry_distribution
    if (!distribution || distribution.length === 0) {
      chartOption.value = null
      return
    }
    chartOption.value = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: 10,
        top: 20,
        bottom: 20,
        textStyle: { fontSize: 11 }
      },
      color: COLORS,
      series: [{
        type: 'pie',
        roseType: 'area',
        radius: ['15%', '70%'],
        center: ['40%', '50%'],
        label: {
          show: true,
          formatter: '{b}\n{d}%',
          fontSize: 11
        },
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        data: distribution.map(d => ({
          name: d.industry || d.name,
          value: d.count || d.value
        }))
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
