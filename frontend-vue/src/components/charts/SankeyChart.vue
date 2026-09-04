<template>
  <div class="chart-card full-width">
    <div class="card-title">就业流向桑基图（高校类型 → 行业 → 省份）</div>
    <BaseChart :option="chartOption" height="480px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import BaseChart from './BaseChart.vue'
import { getSankey } from '../../api'
import { useFilters } from '../../composables/useFilters'
import { COLORS } from '../../utils/constants'

const { filterParams } = useFilters()
const chartOption = ref(null)

async function fetchData() {
  try {
    const data = await getSankey(filterParams.value)
    if (!data || !data.nodes || data.nodes.length === 0) {
      chartOption.value = null
      return
    }
    chartOption.value = {
      tooltip: { trigger: 'item', triggerOn: 'mousemove' },
      color: COLORS,
      series: [{
        type: 'sankey',
        layoutIterations: 32,
        emphasis: { focus: 'adjacency' },
        lineStyle: { color: 'gradient', curveness: 0.5 },
        data: data.nodes,
        links: data.links,
        label: { fontSize: 11 }
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
