// Stands in for the application module (no ApplicationController exists yet). `status`
// uses the real backend enum (SUBMITTED | IN_REVIEW | SHORTLISTED | REJECTED) - there is
// no "Interview" stage in the documented application lifecycle.
import { clone, delay, generateId } from './helpers'
import { getJob } from './jobs'

let APPLICATIONS = [
  { id: 101, jobId: 1, cvId: 1, status: 'SHORTLISTED', coverLetter: 'I would love to bring my frontend experience to your team.', submittedAt: '2026-10-10T09:00:00+03:00', statusChangedAt: '2026-10-18T09:00:00+03:00', statusRemarks: 'Great portfolio - moving to interview stage.' },
  { id: 102, jobId: 3, cvId: 1, status: 'IN_REVIEW', coverLetter: 'Customer care has always been my passion.', submittedAt: '2026-10-15T09:00:00+03:00', statusChangedAt: '2026-10-16T09:00:00+03:00', statusRemarks: '' },
  { id: 103, jobId: 7, cvId: 2, status: 'SUBMITTED', coverLetter: 'Excited about the marketing role at Halotel.', submittedAt: '2026-10-20T09:00:00+03:00', statusChangedAt: '2026-10-20T09:00:00+03:00', statusRemarks: '' },
  { id: 104, jobId: 6, cvId: 1, status: 'REJECTED', coverLetter: 'I have 3 years of network support experience.', submittedAt: '2026-10-05T09:00:00+03:00', statusChangedAt: '2026-10-12T09:00:00+03:00', statusRemarks: 'Position filled internally.' },
]

async function hydrate(app) {
  const job = await getJob(app.jobId).catch(() => null)
  return {
    ...clone(app),
    jobTitle: job?.title ?? 'Unknown role',
    company: job?.company ?? 'Unknown company',
    companyInitials: job?.companyInitials ?? '?',
    companyColor: job?.companyColor ?? '#64748B',
    location: job?.location ?? '',
  }
}

export async function listApplications({ status = '', search = '', page = 1, pageSize = 10 } = {}) {
  await delay()
  const hydrated = await Promise.all(APPLICATIONS.map(hydrate))
  const q = search.trim().toLowerCase()
  let matches = hydrated.filter((app) => {
    const matchesStatus = !status || status === 'All statuses' || app.status === status
    const matchesSearch = !q || app.jobTitle.toLowerCase().includes(q) || app.company.toLowerCase().includes(q)
    return matchesStatus && matchesSearch
  })
  const total = matches.length
  const start = (page - 1) * pageSize
  matches = matches.slice(start, start + pageSize)
  return { items: matches, total }
}

export async function getApplication(id) {
  await delay()
  const app = APPLICATIONS.find((a) => a.id === Number(id))
  if (!app) throw new Error('Application not found')
  return hydrate(app)
}

export async function createApplication({ jobId, cvId, coverLetter }) {
  await delay(600)
  const now = new Date().toISOString()
  const record = {
    id: generateId(),
    jobId: Number(jobId),
    cvId: Number(cvId),
    status: 'SUBMITTED',
    coverLetter,
    submittedAt: now,
    statusChangedAt: now,
    statusRemarks: '',
  }
  APPLICATIONS = [record, ...APPLICATIONS]
  return hydrate(record)
}

export async function hasAppliedTo(jobId) {
  await delay(100)
  return APPLICATIONS.some((a) => a.jobId === Number(jobId) && a.status !== 'REJECTED')
}
