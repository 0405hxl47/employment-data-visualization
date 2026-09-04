<template>
  <div class="chart-card">
    <div class="card-title">专业就业人数分布</div>
    <BaseChart :option="chartOption" height="380px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getTreemap } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getTreemap(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    chartOption.value = {
      tooltip: { formatter: '{b}: {c}人' },
      color: COLORS,
      series: [{
        type: 'treemap',
        roam: false,
        width: '95%',
        height: '90%',
        label: {
          show: true,
          formatter: '{b}\n{c}人',
          fontSize: 12
        },
        breadcrumb: { show: false },
        itemStyle: { borderColor: '#fff', borderWidth: 2, gapWidth: 2 },
        data: data
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
