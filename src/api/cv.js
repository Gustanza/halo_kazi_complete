// Wired to the real CvController (/api/v1/jobseeker/cvs). The backend is job-seeker-only and
// identity-secured (see jobseeker/service/CurrentJobSeekerProvider.java), but still an in-memory
// store - CVs do not survive a backend restart. That's an accepted, known limitation.
//
// Every mutating endpoint requires an `If-Match: <version>` header (optimistic concurrency); this
// module tracks each CV's current version internally so no view needs to manage that itself.
import { getJson, request } from './http'

const BASE = '/api/v1/jobseeker/cvs'

// The real template codes (CLASSIC/MODERN/PROFESSIONAL/MINIMAL/GRADUATE) have no visual metadata
// of their own on the wire - this reuses the 5 hand-built CV layouts CvTemplate.vue/ViewCv.vue
// already render, picking the closest match per code.
const PREVIEW_BY_CODE = {
  CLASSIC: { preview: 'simple', scheme: 'light', accent: '#334155' },
  MODERN: { preview: 'modern', scheme: 'light', accent: '#ea580c' },
  PROFESSIONAL: { preview: 'professional', scheme: 'light', accent: '#1d4ed8' },
  MINIMAL: { preview: 'minimal', scheme: 'light', accent: '#0f172a' },
  GRADUATE: { preview: 'creative', scheme: 'light', accent: '#6b21a8' },
}
const DEFAULT_PREVIEW = { preview: 'simple', scheme: 'light', accent: '#334155' }

// cvId -> current numeric version, needed for If-Match on the next mutating call.
const versionCache = new Map()

// Local-only temp ids (Date.now(), ~13 digits) must never be sent as if they were real backend
// ids; real ids from this in-memory prototype stay small for the lifetime of a server run.
const REAL_ID_CEILING = 1_000_000
function realId(id) {
  const n = Number(id)
  return Number.isFinite(n) && n > 0 && n < REAL_ID_CEILING ? n : null
}

function isPresent(value) {
  return String(value ?? '').trim().toLowerCase() === 'present'
}
/** "2021" -> "2021-01-01" (the form only ever collects a year, backend wants a LocalDate). */
function toYearDate(year) {
  const y = String(year ?? '').trim()
  return /^\d{4}$/.test(y) ? `${y}-01-01` : null
}
function yearFromDate(date) {
  return date ? String(date).slice(0, 4) : ''
}

let templatesCache = null
async function loadTemplates() {
  if (!templatesCache) {
    const res = await getJson(`${BASE}/templates`, { auth: true })
    templatesCache = res.data.map((t) => ({
      id: t.templateId,
      code: t.code,
      name: t.nameEn,
      description: t.descriptionEn,
      ...(PREVIEW_BY_CODE[t.code] ?? DEFAULT_PREVIEW),
    }))
  }
  return templatesCache
}

export async function listTemplates() {
  return loadTemplates()
}

async function templateIdToCode(templateId) {
  const list = await loadTemplates()
  return list.find((t) => t.id === Number(templateId))?.code
}

async function currentVersion(cvId) {
  if (versionCache.has(cvId)) return versionCache.get(cvId)
  const detail = await fetchDetail(cvId)
  return detail.version
}

async function fetchDetail(cvId) {
  const res = await getJson(`${BASE}/${cvId}`, { auth: true })
  versionCache.set(res.data.cvId, res.data.version)
  return res.data
}

/** PUT/POST one CV section, sending the If-Match the server needs, and caches the new version. */
async function mutate(cvId, path, body, method = 'PUT') {
  const version = await currentVersion(cvId)
  const res = await request(method, `${BASE}/${cvId}${path}`, {
    body,
    auth: true,
    headers: { 'If-Match': String(version) },
  })
  versionCache.set(res.data.cvId, res.data.version)
  return res.data
}

/** Adapts a real CvDetailDto into the flat shape CvTemplate.vue/ViewCv.vue/MyCvs.vue already render. */
function toFlat(detail) {
  const info = detail.personalInfo ?? {}
  return {
    id: detail.cvId,
    cvName: detail.cvName,
    templateId: detail.templateId,
    templateCode: detail.templateCode,
    fullName: [info.firstName, info.lastName].filter(Boolean).join(' '),
    email: info.contactEmail ?? '',
    phone: info.phone ?? '',
    location: info.currentLocation ?? '',
    professionalTitle: detail.headline || info.headline || '',
    headline: detail.headline || '',
    summary: detail.summaryOverride || info.professionalSummary || '',
    photo: info.profilePictureUrl ?? null,
    education: (detail.educations ?? []).map((e) => ({
      id: e.id,
      degree: e.qualification ?? '',
      institution: e.institutionName ?? '',
      startYear: yearFromDate(e.startDate),
      endYear: e.current ? 'Present' : yearFromDate(e.endDate),
    })),
    experiences: (detail.experiences ?? []).map((e) => ({
      id: e.id,
      jobTitle: e.jobTitle ?? '',
      company: e.companyName ?? '',
      location: e.location ?? '',
      startDate: yearFromDate(e.startDate),
      endDate: e.current ? 'Present' : yearFromDate(e.endDate),
      description: e.description ?? '',
    })),
    skills: (detail.skills ?? []).map((s) => s.skillName),
    // CvDetailDto.isDefault() serializes to "default" (Jackson strips the "is" prefix).
    isLastUsed: !!detail.default,
    createdAt: detail.createdAt,
    updatedAt: detail.updatedAt,
  }
}

export async function listCvs() {
  const [res, templates] = await Promise.all([
    getJson(BASE, { auth: true, params: { page: 0, size: 50 } }),
    loadTemplates(),
  ])
  const codeToId = Object.fromEntries(templates.map((t) => [t.code, t.id]))
  // CvSummaryDto carries no personal-info/section data - this list is metadata-only, matching
  // what My CVs actually renders per row (title, template, dates, default badge).
  return res.data.content.map((s) => ({
    id: s.cvId,
    cvName: s.cvName,
    templateId: codeToId[s.templateCode] ?? null,
    templateCode: s.templateCode,
    fullName: '',
    email: '',
    phone: '',
    location: '',
    professionalTitle: '',
    summary: '',
    photo: null,
    education: [],
    experiences: [],
    skills: [],
    isLastUsed: !!s.default,
    createdAt: s.createdAt,
    updatedAt: s.updatedAt,
  }))
}

export async function getCv(id) {
  return toFlat(await fetchDetail(Number(id)))
}

/** The CV currently marked default, so "create another CV" can start from it. */
export async function getLastUsedCv() {
  const items = await listCvs()
  const chosen = items.find((c) => c.isLastUsed) ?? items[0]
  return chosen ? getCv(chosen.id) : null
}

function experienceItems(list) {
  return (list ?? []).map((e) => ({
    id: realId(e.id),
    companyName: e.company || '',
    jobTitle: e.jobTitle || '',
    employmentTypeName: null,
    location: e.location || '',
    description: e.description || '',
    startDate: toYearDate(e.startDate),
    endDate: isPresent(e.endDate) ? null : toYearDate(e.endDate),
    current: isPresent(e.endDate),
    sortOrder: 0,
  }))
}
function educationItems(list) {
  return (list ?? []).map((e) => ({
    id: realId(e.id),
    educationLevelName: null,
    institutionName: e.institution || '',
    qualification: e.degree || '',
    fieldOfStudy: null,
    grade: null,
    startDate: toYearDate(e.startYear),
    endDate: isPresent(e.endYear) ? null : toYearDate(e.endYear),
    current: isPresent(e.endYear),
    sortOrder: 0,
  }))
}
function skillItems(list) {
  return (list ?? []).map((name, index) => ({
    id: null,
    skillName: name,
    proficiency: null,
    yearsExperience: null,
    sortOrder: index,
  }))
}

/** Creates the draft, then applies every section from `payload` in one go. */
export async function createCv(payload) {
  const templateCode = payload.templateId ? await templateIdToCode(payload.templateId) : undefined
  const created = await request('POST', BASE, {
    body: { title: payload.cvName || undefined, templateCode },
    auth: true,
  })
  versionCache.set(created.data.cvId, created.data.version)
  const detail = await applySections(created.data.cvId, payload)
  await mutate(detail.cvId, '/default', undefined, 'PUT')
  return getCv(detail.cvId)
}

export async function updateCv(id, patch) {
  const detail = await applySections(Number(id), patch)
  return toFlat(detail)
}

/** Only pushes the sections present in `payload`, in a fixed order. */
async function applySections(cvId, payload) {
  let latest = null
  if (payload.fullName !== undefined || payload.email !== undefined || payload.phone !== undefined
    || payload.location !== undefined || payload.professionalTitle !== undefined || payload.summary !== undefined) {
    const [firstName, ...rest] = String(payload.fullName || '').trim().split(/\s+/)
    latest = await mutate(cvId, '/personal-info', {
      firstName: firstName || '',
      lastName: rest.join(' '),
      contactEmail: payload.email || '',
      phone: payload.phone || '',
      headline: payload.professionalTitle || payload.headline || '',
      professionalSummary: payload.summary || '',
      currentLocation: payload.location || '',
    })
  }
  if (payload.experiences !== undefined) {
    latest = await mutate(cvId, '/experience', experienceItems(payload.experiences))
  }
  if (payload.education !== undefined) {
    latest = await mutate(cvId, '/education', educationItems(payload.education))
  }
  if (payload.skills !== undefined) {
    latest = await mutate(cvId, '/skills', skillItems(payload.skills))
  }
  return latest ?? fetchDetail(cvId)
}

export async function setLastUsedCv(id) {
  await mutate(Number(id), '/default', undefined, 'PUT')
}

export async function deleteCv(id) {
  const cvId = Number(id)
  const version = await currentVersion(cvId)
  await request('DELETE', `${BASE}/${cvId}`, { auth: true, headers: { 'If-Match': String(version) } })
  versionCache.delete(cvId)
}

/** Downloads the rendered PDF and returns a Blob (caller turns it into an <a download> click). */
export async function downloadCv(id) {
  const response = await request('GET', `${BASE}/${id}/download`, { auth: true, raw: true })
  return response.blob()
}
