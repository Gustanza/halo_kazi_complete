// Jobs/vacancies for the authenticated dashboard - wired to the same real endpoints the public
// landing page already uses: PublicJobController (/api/public/jobs, /api/public/lookups) via
// publicApi.js, and SavedJobController (/api/me/saved-jobs) via savedJobsApi.js. There is a
// separate VacancyController (/api/v1/vacancies) and a fake in-memory JobSeekerSavedJobController
// (/api/v1/jobseeker/saved-jobs) in the backend, but neither is used here - the public endpoints
// are real, already proven, and sufficient.
import * as publicApi from './publicApi'
import * as savedJobsApi from './savedJobsApi'
import { brandColor } from '../pages/landing/data/mappers'

/** Adapts a real JobSummary/JobDetail into the flat shape the dashboard views render. */
function toFlatJob(job, savedIds) {
  return {
    id: job.id,
    title: job.title,
    company: job.company?.name ?? '',
    companyInitials: job.company?.initials ?? '',
    companyColor: brandColor(job.company?.name ?? job.id),
    location: job.region?.nameEn ?? '',
    category: job.category?.nameEn ?? '',
    employmentType: job.employmentType?.nameEn ?? '',
    salaryMin: job.salary?.min ?? null,
    salaryMax: job.salary?.max ?? null,
    salaryCurrency: job.salary?.currency ?? null,
    postedAt: job.publishedAt,
    // JobSummary (list items) carries no description, only JobDetail does; the real schema has
    // no concept of tags or a "remote" flag at all.
    description: job.description ?? '',
    tags: [],
    remote: false,
    saved: savedIds.has(job.id),
  }
}

async function getSavedIdSet() {
  try {
    const { vacancyIds } = await savedJobsApi.getSavedJobIds()
    return new Set(vacancyIds)
  } catch {
    return new Set()
  }
}

/** @param {{search?, location?, category?, employmentType?, savedOnly?, page?, pageSize?}} params */
export async function listJobs({
  search = '',
  location = '',
  category = '',
  employmentType = '',
  savedOnly = false,
  page = 1,
  pageSize = 10,
} = {}) {
  const savedIds = await getSavedIdSet()

  if (savedOnly) {
    // The real search endpoint has no "saved only" filter - load each saved job's detail directly.
    const jobs = await Promise.all([...savedIds].map((id) => publicApi.getJob(id).catch(() => null)))
    let items = jobs.filter(Boolean).map((job) => toFlatJob(job, savedIds))
    const q = search.trim().toLowerCase()
    if (q) {
      items = items.filter(
        (job) => job.title.toLowerCase().includes(q) || job.company.toLowerCase().includes(q),
      )
    }
    const total = items.length
    const start = (page - 1) * pageSize
    return { items: items.slice(start, start + pageSize), total }
  }

  const result = await publicApi.searchJobs(
    { q: search, region: location, category, type: employmentType },
    { page, size: pageSize },
  )
  return { items: result.items.map((job) => toFlatJob(job, savedIds)), total: result.totalItems }
}

export async function getJob(id) {
  const [job, savedIds] = await Promise.all([publicApi.getJob(id), getSavedIdSet()])
  return toFlatJob(job, savedIds)
}

/** {code, nameEn}[] - the real job categories, for populating the category filter. */
export async function listCategories() {
  const { categories } = await publicApi.getLookups()
  return categories
}

export async function listSavedJobIds() {
  const { vacancyIds } = await savedJobsApi.getSavedJobIds()
  return vacancyIds
}

export const saveJob = (id) => savedJobsApi.saveJob(id)
export const unsaveJob = (id) => savedJobsApi.unsaveJob(id)
