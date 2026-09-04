<template>
  <div class="chart-card full-width">
    <div class="card-title">全国就业分布地图</div>
    <BaseChart :option="chartOption" height="560px" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import * as echarts from 'echarts'
import BaseChart from './BaseChart.vue'
import { getProvince } from '../../api'
import { useFilters } from '../../composables/useFilters'

const { filterParams } = useFilters()
const chartOption = ref(null)
const mapRegistered = ref(false)

async function registerMap() {
  if (mapRegistered.value) return
  try {
    const resp = await fetch('/data/china.json')
    const geoJson = await resp.json()
    echarts.registerMap('china', geoJson)
    mapRegistered.value = true
  } catch (e) {
    console.error('Failed to load china.json:', e)
  }
}

async function fetchData() {
  await registerMap()
  if (!mapRegistered.value) return

  try {
    const data = await getProvince(filterParams.value)
    if (!data || data.length === 0) {
      chartOption.value = null
      return
    }
    const maxVal = Math.max(...data.map(d => d.count))
    chartOption.value = {
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          if (params.data) {
            return `${params.name}<br/>就业人数: ${params.data.count || params.value}<br/>平均薪资: ¥${params.data.avg_salary || '-'}`
          }
          return params.name
        }
      },
      visualMap: {
        min: 0,
        max: maxVal || 1000,
        left: 20,
        bottom: 20,
        text: ['高', '低'],
        inRange: { color: ['#e3f2fd', '#1565c0', '#0d47a1'] },
        calculable: true
      },
      series: [{
        type: 'map',
        map: 'china',
        roam: true,
        label: { show: true, fontSize: 9, color: '#333' },
        emphasis: {
          label: { show: true, fontSize: 12, fontWeight: 'bold' },
          itemStyle: { areaColor: '#ffab00' }
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        data: data.map(d => ({
          name: d.province,
          value: d.count,
          count: d.count,
          avg_salary: d.avg_salary
        }))
      }]
    }
  } catch {
    chartOption.value = null
  }
}

watch(filterParams, fetchData, { immediate: true, deep: true })
</script>
