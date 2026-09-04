import axios from 'axios'

const api = axios.create({
  baseURL: '',
  timeout: 30000
})

api.interceptors.response.use(
  res => res.data,
  err => {
    console.error('API Error:', err)
    return Promise.reject(err)
  }
)

export const getFilters = () => api.get('/api/filters')

export const getEmployment = (params) => api.get('/api/employment', { params })

export const getOverview = () => api.get('/api/stats/overview')

export const getSalaryByUniversity = (params) => api.get('/api/stats/salary_by_university', { params })

export const getEmploymentType = (params) => api.get('/api/stats/employment_type', { params })

export const getIndustry = (params) => api.get('/api/stats/industry', { params })

export const getProvince = (params) => api.get('/api/stats/province', { params })

export const getSalaryByDegree = (params) => api.get('/api/stats/salary_by_degree', { params })

export const getSankey = (params) => api.get('/api/stats/sankey', { params })

export const getTreemap = (params) => api.get('/api/stats/treemap', { params })

export const getSunburst = (params) => api.get('/api/stats/sunburst', { params })

export const getCity = (params) => api.get('/api/stats/city', { params })

export const getSalaryBox = (params) => api.get('/api/stats/salary_box', { params })

export const getScatter = (params) => api.get('/api/stats/scatter', { params })

export const getFurtherEducation = (params) => api.get('/api/stats/further_education', { params })

export const getProvinceByUniversity = (params) => api.get('/api/stats/province_by_university', { params })

export const getWordcloud = (params) => api.get('/api/stats/wordcloud', { params })

export const getTrend = (params) => api.get('/api/stats/trend', { params })

export const getMajorAnalysis = (params) => api.get('/api/stats/major_analysis', { params })

export const exportExcel = (params) =>
  api.get('/api/employment/export', { params, responseType: 'blob' })

export default api
