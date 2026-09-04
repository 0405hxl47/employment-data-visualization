import { reactive, computed, ref } from 'vue'
import { getFilters } from '../api'

const state = reactive({
  universityType: '',
  universityName: '',
  degree: '',
  graduationYear: '',
  keyword: ''
})

const options = reactive({
  universityTypes: [],
  universities: [],
  degrees: [],
  years: [],
  loaded: false
})

// All universities before filtering by type
const allUniversities = ref([])

const filterParams = computed(() => {
  const params = {}
  if (state.universityType) params.university_type = state.universityType
  if (state.universityName) params.university_name = state.universityName
  if (state.degree) params.degree = state.degree
  if (state.graduationYear) params.graduation_year = state.graduationYear
  if (state.keyword) params.keyword = state.keyword
  return params
})

// Filtered universities based on selected type
const filteredUniversities = computed(() => {
  if (!state.universityType) return allUniversities.value
  if (state.universityType === '双一流') {
    return allUniversities.value.filter(
      u => u.type === '985' || u.type === '211'
    )
  }
  return allUniversities.value.filter(u => u.type === state.universityType)
})

async function loadOptions() {
  if (options.loaded) return
  try {
    const data = await getFilters()
    const rawTypes = data.university_types || []
    if (!rawTypes.includes('双一流')) {
      rawTypes.push('双一流')
    }
    options.universityTypes = rawTypes
    options.degrees = data.degree || []
    options.years = data.graduation_years || []
    const details = data.universities_detail || []
    allUniversities.value = details.map(u => ({
      name: u.university_name,
      type: u.university_type,
      province: u.province,
      city: u.city
    }))
    options.loaded = true
  } catch (e) {
    console.error('Failed to load filter options:', e)
  }
}

function resetFilters() {
  state.universityType = ''
  state.universityName = ''
  state.degree = ''
  state.graduationYear = ''
  state.keyword = ''
}

function onTypeChange() {
  // When type changes, clear selected university if it's no longer in the filtered list
  if (state.universityName) {
    const still = filteredUniversities.value.some(
      u => u.name === state.universityName
    )
    if (!still) state.universityName = ''
  }
}

export function useFilters() {
  loadOptions()
  return {
    state,
    options,
    filterParams,
    filteredUniversities,
    resetFilters,
    onTypeChange
  }
}
