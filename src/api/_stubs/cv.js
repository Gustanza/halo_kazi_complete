// Stands in for the CV module (no CvController exists yet). `isLastUsed` implements the
// 23 Oct 2026 meeting decision: "save the last used CV so a new CV can start from it"
// (see docs/Halokazi_Progress_Meeting_Minutes_23102026.pdf) - there's no backend column
// for this yet, so it's tracked here until that lands.
import { clone, delay, generateId } from './helpers'

// `preview`/`description` describe the layout CvBuilder.vue/CvTemplate.vue/ViewCv.vue render -
// those three views only have hand-built markup for these 5 layouts, so the template catalog
// is limited to them (consolidated down from an earlier, purely decorative 9-entry list).
const TEMPLATES = [
  { id: 1, name: 'Professional', description: 'Clean and professional design suitable for most careers.', preview: 'professional', scheme: 'light', accent: '#1d4ed8' },
  { id: 2, name: 'Modern', description: 'Modern layout with a stylish and attractive appearance.', preview: 'modern', scheme: 'light', accent: '#ea580c' },
  { id: 3, name: 'Minimal', description: 'Simple and clean design focused on your experience.', preview: 'minimal', scheme: 'light', accent: '#0f172a' },
  { id: 4, name: 'Creative', description: 'Creative layout for designers and creative professionals.', preview: 'creative', scheme: 'light', accent: '#6b21a8' },
  { id: 5, name: 'Simple', description: 'Straightforward and easy-to-read professional CV.', preview: 'simple', scheme: 'light', accent: '#334155' },
]

// Full CV content, not just metadata - this is what CvTemplate.vue's wizard actually collects
// (personal details, education, experience, skills), so My CVs / View CV / "start from last
// used CV" all have real data to show instead of each view inventing its own mock person.
let CVS = [
  {
    id: 1,
    cvName: 'Frontend Developer CV',
    templateId: 1,
    fullName: 'Raymond Mwangi',
    email: 'raymond.mwangi@email.com',
    phone: '+255 624 023 920',
    location: 'Dar es Salaam, Tanzania',
    professionalTitle: 'Frontend Developer',
    headline: 'Frontend Developer',
    summary: 'Frontend developer with 4 years building React and Vue applications.',
    photo: null,
    education: [
      { id: 1, degree: 'Bachelor of Business Administration', institution: 'University of Dar es Salaam', startYear: '2014', endYear: '2017' },
    ],
    experiences: [
      { id: 1, jobTitle: 'Frontend Developer', company: 'ABC Company Ltd', location: 'Dar es Salaam', startDate: '2021', endDate: 'Present', description: 'Build and maintain customer-facing web applications.' },
    ],
    skills: ['React', 'Vue.js', 'Typescript', 'Tailwind CSS'],
    isLastUsed: true,
    createdAt: '2026-09-01T09:00:00+03:00',
    updatedAt: '2026-10-20T09:00:00+03:00',
  },
  {
    id: 2,
    cvName: 'Marketing CV',
    templateId: 3,
    fullName: 'Raymond Mwangi',
    email: 'raymond.mwangi@email.com',
    phone: '+255 624 023 920',
    location: 'Dar es Salaam, Tanzania',
    professionalTitle: 'Digital Marketing Specialist',
    headline: 'Digital Marketing Specialist',
    summary: 'Marketing specialist focused on growth and social media.',
    photo: null,
    education: [
      { id: 1, degree: 'Bachelor of Business Administration', institution: 'University of Dar es Salaam', startYear: '2014', endYear: '2017' },
    ],
    experiences: [
      { id: 1, jobTitle: 'Marketing Officer', company: 'XYZ Company', location: 'Dar es Salaam', startDate: '2018', endDate: '2021', description: 'Assisted in developing marketing strategies and managed customer communications.' },
    ],
    skills: ['Digital Marketing', 'Social Media Management', 'SEO & SEM'],
    isLastUsed: false,
    createdAt: '2026-09-15T09:00:00+03:00',
    updatedAt: '2026-09-15T09:00:00+03:00',
  },
]

export async function listTemplates() {
  await delay(200)
  return clone(TEMPLATES)
}

export async function listCvs() {
  await delay()
  return clone(CVS).sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
}

export async function getCv(id) {
  await delay()
  const cv = CVS.find((c) => c.id === Number(id))
  if (!cv) throw new Error('CV not found')
  return clone(cv)
}

/** The CV most recently marked as last-used, so "create another CV" can start from it. */
export async function getLastUsedCv() {
  await delay(150)
  const cv = CVS.find((c) => c.isLastUsed) ?? CVS[0] ?? null
  return cv ? clone(cv) : null
}

export async function createCv(payload) {
  await delay(500)
  const now = new Date().toISOString()
  const record = {
    id: generateId(),
    cvName: payload.cvName || 'Untitled CV',
    templateId: Number(payload.templateId) || 1,
    fullName: payload.fullName || '',
    email: payload.email || '',
    phone: payload.phone || '',
    location: payload.location || '',
    professionalTitle: payload.professionalTitle || '',
    headline: payload.headline || payload.professionalTitle || '',
    summary: payload.summary || '',
    photo: payload.photo || null,
    education: payload.education || [],
    experiences: payload.experiences || [],
    skills: payload.skills || [],
    isLastUsed: true,
    createdAt: now,
    updatedAt: now,
  }
  CVS = CVS.map((c) => ({ ...c, isLastUsed: false })).concat(record)
  return clone(record)
}

export async function updateCv(id, patch) {
  await delay(400)
  CVS = CVS.map((c) => (c.id === Number(id) ? { ...c, ...patch, updatedAt: new Date().toISOString() } : c))
  return getCv(id)
}

export async function setLastUsedCv(id) {
  await delay(150)
  CVS = CVS.map((c) => ({ ...c, isLastUsed: c.id === Number(id) }))
}

export async function deleteCv(id) {
  await delay(400)
  CVS = CVS.filter((c) => c.id !== Number(id))
}
