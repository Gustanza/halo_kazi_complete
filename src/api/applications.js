// Real calls to ApplicationController (/api/v1/applications) - back_resources/backend
// src/main/java/tz/co/halotel/halokazi/application/controller/ApplicationController.java.
// Every response is wrapped as { success, message, data, timestamp } (common/api/ApiResponse.java);
// the list endpoint's `data` is further wrapped as { content, pageNumber, pageSize, totalElements,
// totalPages, last } (common/api/PageResponse.java).
import { getJson, postJson } from './http'

const BASE = '/api/v1/applications'

function toSummary(dto) {
  return {
    id: dto.applicationId,
    jobId: dto.vacancyId,
    status: dto.status,
    jobTitle: dto.jobTitle,
    company: dto.companyName,
    companyInitials: dto.companyInitials,
    companyColor: dto.brandColor,
    location: dto.location,
    submittedAt: dto.submittedAt,
    statusChangedAt: dto.lastEditedAt,
    statusRemarks: '', // only present on the detail endpoint
  }
}

// The real endpoint has no server-side status/search filtering - fetch everything once
// (a job seeker's own applications are never a huge list) and keep the existing tabs+search
// UX working by filtering/paginating client-side, same as the stub this replaces did.
export async function listApplications({ status = '', search = '', page = 1, pageSize = 10 } = {}) {
  const response = await getJson(BASE, { params: { page: 0, size: 200 }, auth: true })
  const all = response.data.content.map(toSummary)

  const q = search.trim().toLowerCase()
  let matches = all.filter((app) => {
    const matchesStatus = !status || app.status === status
    const matchesSearch =
      !q || app.jobTitle.toLowerCase().includes(q) || app.company.toLowerCase().includes(q)
    return matchesStatus && matchesSearch
  })
  const total = matches.length
  const start = (page - 1) * pageSize
  matches = matches.slice(start, start + pageSize)
  return { items: matches, total }
}

export async function getApplication(id) {
  const response = await getJson(`${BASE}/${id}`, { auth: true })
  const dto = response.data
  return {
    ...toSummary(dto),
    coverLetter: dto.coverLetter,
    statusRemarks: dto.statusRemarks ?? '',
  }
}

// cvId is never forwarded: real CV persistence doesn't exist yet (My CVs/CV Builder is
// still browser-side-only with fake ids), and the backend rejects an unrecognized cvId.
// Omitting it makes the backend auto-resolve/auto-create its own lightweight CV record.
export async function createApplication({ jobId, coverLetter, requirementsConfirmed }) {
  const response = await postJson(
    BASE,
    { vacancyId: Number(jobId), coverLetter, requirementsConfirmed: Boolean(requirementsConfirmed) },
    { auth: true },
  )
  return toSummary(response.data)
}

export async function hasAppliedTo(jobId) {
  const response = await getJson(`${BASE}/check/${jobId}`, { auth: true })
  return response.data.hasApplied
}
