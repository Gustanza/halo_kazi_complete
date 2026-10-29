// Stands in for the vacancy module (no VacancyController exists yet). Field names follow
// the `vacancy` table in back_resources/db/halokazi.dbml so swapping to the real endpoint
// later is a reshape, not a redesign.
import { clone, delay } from './helpers'

const JOBS = [
  { id: 1, title: 'Frontend Developer', company: 'Google', companyInitials: 'G', companyColor: '#ea4335', location: 'Dar es Salaam, Tanzania', category: 'Technology', employmentType: 'Full time', salaryMin: 1500000, salaryMax: 2500000, salaryCurrency: 'TZS', postedAt: '2026-10-27T09:00:00+03:00', description: 'We are looking for a skilled Frontend Developer to join our team and help build amazing products for millions of users.', tags: ['React', 'Typescript'], remote: true },
  { id: 2, title: 'Full Stack Developer', company: 'TechSolution Ltd', companyInitials: '⬡', companyColor: '#7138e8', location: 'Dar es Salaam, Tanzania', category: 'Technology', employmentType: 'Full time', salaryMin: 1200000, salaryMax: 2000000, salaryCurrency: 'TZS', postedAt: '2026-10-24T09:00:00+03:00', description: 'We are looking for a talented Full Stack Developer with experience in modern web technologies to join our growing team.', tags: ['Typescript'], remote: true },
  { id: 3, title: 'Customer Care Agent', company: 'Vodacom Tanzania', companyInitials: 'VOD', companyColor: '#e21b2d', location: 'Dar es Salaam, Tanzania', category: 'Customer Service', employmentType: 'Full time', salaryMin: 600000, salaryMax: 900000, salaryCurrency: 'TZS', postedAt: '2026-10-23T09:00:00+03:00', description: 'Join our customer care team and help us deliver world class support to our valued customers.', tags: ['React', 'Typescript'], remote: false },
  { id: 4, title: 'Data Analyst', company: 'Smart Finance Ltd', companyInitials: 'S', companyColor: '#16864a', location: 'Dar es Salaam, Tanzania', category: 'Finance', employmentType: 'Full time', salaryMin: 900000, salaryMax: 1400000, salaryCurrency: 'TZS', postedAt: '2026-10-28T09:00:00+03:00', description: 'Analyze business data and prepare reports that support better decisions.', tags: ['Excel', 'SQL'], remote: false },
  { id: 5, title: 'UI/UX Designer', company: 'Creative Hub', companyInitials: 'C', companyColor: '#8543e8', location: 'Arusha, Tanzania', category: 'Design', employmentType: 'Part time', salaryMin: 700000, salaryMax: 1000000, salaryCurrency: 'TZS', postedAt: '2026-10-26T09:00:00+03:00', description: 'Create intuitive digital experiences, wireframes, and prototypes for web products.', tags: ['Figma', 'Prototyping'], remote: true },
  { id: 6, title: 'Network Support Officer', company: 'Connect Services', companyInitials: 'N', companyColor: '#3858a8', location: 'Mwanza, Tanzania', category: 'Technology', employmentType: 'Full time', salaryMin: 650000, salaryMax: 950000, salaryCurrency: 'TZS', postedAt: '2026-10-23T09:00:00+03:00', description: 'Troubleshoot network issues and support day-to-day IT infrastructure.', tags: ['Networking', 'IT Support'], remote: false },
  { id: 7, title: 'Digital Marketing Specialist', company: 'Halotel PLC', companyInitials: 'HLT', companyColor: '#FD5C20', location: 'Dar es Salaam, Tanzania', category: 'Marketing', employmentType: 'Full time', salaryMin: 800000, salaryMax: 1300000, salaryCurrency: 'TZS', postedAt: '2026-10-27T09:00:00+03:00', description: 'Plan and run digital campaigns across social, search and email channels.', tags: ['SEO', 'Social Media'], remote: false },
  { id: 8, title: 'Relationship Manager', company: 'NMB Bank PLC', companyInitials: 'NMB', companyColor: '#F6861F', location: 'Dar es Salaam, Tanzania', category: 'Finance', employmentType: 'Full time', salaryMin: 1000000, salaryMax: 1800000, salaryCurrency: 'TZS', postedAt: '2026-10-25T09:00:00+03:00', description: 'Manage a portfolio of corporate clients and grow the branch deposit base.', tags: ['Banking', 'Sales'], remote: false },
]

let savedIds = [2]

export async function listJobs({ search = '', location = '', category = '', employmentType = '', savedOnly = false, page = 1, pageSize = 5 } = {}) {
  await delay()
  const q = search.trim().toLowerCase()
  const loc = location.trim().toLowerCase()
  let matches = JOBS.filter((job) => {
    const matchesSearch =
      !q ||
      job.title.toLowerCase().includes(q) ||
      job.company.toLowerCase().includes(q) ||
      job.description.toLowerCase().includes(q) ||
      job.tags.some((tag) => tag.toLowerCase().includes(q))
    const matchesLocation = !loc || job.location.toLowerCase().includes(loc)
    const matchesCategory = !category || category === 'All Categories' || job.category === category
    const matchesType =
      !employmentType ||
      employmentType === 'All Jobs' ||
      (employmentType === 'Remote' && job.remote) ||
      job.employmentType === employmentType
    const matchesSaved = !savedOnly || savedIds.includes(job.id)
    return matchesSearch && matchesLocation && matchesCategory && matchesType && matchesSaved
  })
  const total = matches.length
  const start = (page - 1) * pageSize
  matches = matches.slice(start, start + pageSize)
  return { items: clone(matches.map(withSaved)), total }
}

export async function getJob(id) {
  await delay()
  const job = JOBS.find((j) => j.id === Number(id))
  if (!job) throw new Error('Job not found')
  return clone(withSaved(job))
}

export async function listCategories() {
  await delay(150)
  return [...new Set(JOBS.map((j) => j.category))]
}

export async function listSavedJobIds() {
  await delay(150)
  return [...savedIds]
}

export async function saveJob(id) {
  await delay(150)
  if (!savedIds.includes(Number(id))) savedIds.push(Number(id))
}

export async function unsaveJob(id) {
  await delay(150)
  savedIds = savedIds.filter((savedId) => savedId !== Number(id))
}

function withSaved(job) {
  return { ...job, saved: savedIds.includes(job.id) }
}
