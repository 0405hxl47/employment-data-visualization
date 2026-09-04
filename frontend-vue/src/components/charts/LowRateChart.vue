<template>
  <div class="chart-card full-width">
    <div class="card-title">就业率预警专业详情（就业率 &lt; 60%）</div>
    <div v-if="!chartOption && !loading" class="empty-tip">暂无就业率低于 60% 的专业</div>
    <BaseChart v-if="chartOption" :option="chartOption" :height="chartHeight" />
    <div v-if="totalCount > 5" class="view-all-wrap">
      <el-button type="warning" plain @click="dialogVisible = true">
        查看完整列表（共 {{ totalCount }} 条）
      </el-button>
    </div>

    <el-dialog v-model="dialogVisible" title="就业率低于 60% 的专业完整列表" width="820px" top="5vh">
      <el-table :data="allMajors" stripe max-height="60vh" style="width: 100%">
        <el-table-column type="index" label="排名" width="60" />
        <el-table-column prop="university_name" label="学校" min-width="140" />
        <el-table-column prop="major_name" label="专业" min-width="160" />
        <el-table-column label="平均就业率" width="120" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.avg_rate < 40 ? '#c62828' : row.avg_rate < 50 ? '#e53935' : '#ff7043', fontWeight: 'bold' }">
              {{ row.avg_rate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="数据量" width="90" align="center" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getMajorAnalysis } from '../../api'

const chartOption = ref(null)
const loading = ref(false)
const displayCount = ref(0)
const totalCount = ref(0)
const allMajors = ref([])
const dialogVisible = ref(false)

const chartHeight = computed(() => {
  const h = Math.max(200, displayCount.value * 40 + 80)
  return h + 'px'
})

function buildChart(majors) {
  const sorted = [...majors].sort((a, b) => a.avg_rate - b.avg_rate)
  displayCount.value = sorted.length

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0]
        const item = sorted[p.dataIndex]
        return `<strong>${item.university_name}</strong><br/>` +
          `专业: ${item.major_name}<br/>` +
          `平均就业率: <span style="color:#e53935;font-weight:bold">${item.avg_rate}%</span><br/>` +
          `数据量: ${item.count} 条记录`
      }
    },
    grid: { left: 220, right: 60, top: 30, bottom: 30 },
    xAxis: {
      type: 'value',
      name: '就业率 (%)',
      min: 0,
      max: 100,
      axisLabel: { formatter: '{value}%' },
      splitLine: { lineStyle: { type: 'dashed' } }
    },
    yAxis: {
      type: 'category',
      data: sorted.map(d => d.university_name + ' - ' + d.major_name),
      axisLabel: {
        fontSize: 12,
        width: 200,
        overflow: 'truncate'
      }
    },
    series: [
      {
        name: '就业率',
        type: 'bar',
        data: sorted.map(d => ({
          value: d.avg_rate,
          itemStyle: {
            color: d.avg_rate < 40 ? '#c62828' : d.avg_rate < 50 ? '#e53935' : '#ff7043'
          }
        })),
        barMaxWidth: 24,
        itemStyle: { borderRadius: [0, 4, 4, 0] },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}%',
          fontSize: 12,
          color: '#e53935',
          fontWeight: 'bold'
        },
        markLine: {
          silent: true,
          data: [{ xAxis: 60, label: { formatter: '60% 警戒线', position: 'end' } }],
          lineStyle: { color: '#e53935', type: 'dashed', width: 2 }
        }
      }
    ]
  }
}

async function fetchData() {
  loading.value = true
  try {
    const data = await getMajorAnalysis({})
    const majors = data && data.low_rate_majors
    const all = data && data.low_rate_all
    totalCount.value = data && data.low_rate_count != null ? data.low_rate_count : 0

    if (all && all.length > 0) {
      allMajors.value = [...all].sort((a, b) => a.avg_rate - b.avg_rate)
    }

    if (!majors || majors.length === 0) {
      chartOption.value = null
      displayCount.value = 0
      return
    }

    chartOption.value = buildChart(majors)
  } catch {
    chartOption.value = null
    displayCount.value = 0
    totalCount.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #999;
  font-size: 14px;
}
.view-all-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0 4px;
}
</style>
