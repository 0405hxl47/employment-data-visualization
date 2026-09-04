<template>
  <div class="stat-card" :style="{ borderTopColor: color }">
    <div class="stat-label">{{ label }}</div>
    <div class="stat-value" :style="{ color }">{{ displayValue }}</div>
    <div class="stat-suffix" v-if="suffix">{{ suffix }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: { type: String, required: true },
  value: { type: [Number, String], default: 0 },
  suffix: { type: String, default: '' },
  color: { type: String, default: '#3949ab' }
})

const displayValue = computed(() => {
  if (typeof props.value === 'number') {
    return props.value.toLocaleString()
  }
  return props.value
})
</script>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px 24px;
  border-top: 3px solid #3949ab;
  flex: 1;
  min-width: 160px;
  text-align: center;
}

.stat-label {
  font-size: 13px;
  color: #888;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-suffix {
  font-size: 12px;
  color: #aaa;
  margin-top: 4px;
}
</style>
