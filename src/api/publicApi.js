// Public site data: no login needed. Endpoint details are in back_resources/docs/Halokazi_API.pdf (section 3).
import { getJson } from './http'

/** filters: { q, region, type, category, experience, salary, posted }; empty values are left out. */
export const searchJobs = (filters, { page = 1, size = 10, signal } = {}) =>
  getJson('/api/public/jobs', { params: { ...filters, page, size }, signal })

export const getJob = (id) => getJson(`/api/public/jobs/${encodeURIComponent(id)}`)

export const getLookups = () => getJson('/api/public/lookups')

export const getTopEmployers = (limit = 5) => getJson('/api/public/employers/top', { params: { limit } })

export const getStats = () => getJson('/api/public/stats')

export const getCvTemplates = () => getJson('/api/public/cv-templates')
