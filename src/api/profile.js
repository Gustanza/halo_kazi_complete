// Wired to the real JobSeekerProfileController and its per-section sibling controllers
// (education, work-experience, skills) under /api/v1/jobseeker/*. Identity-secured (see
// jobseeker/service/CurrentJobSeekerProvider.java) but still an in-memory store - data does not
// survive a backend restart. That's an accepted, known limitation.
//
// Personal details, education, experience and skills are each their own REST resource on the
// real backend (no bulk "save the whole profile" endpoint), so this module exposes one function
// per section instead of a single updateProfile(wholeObject) - ProfileView.vue calls each as its
// own immediate action instead of batching everything into one save.
import { getJson, request } from './http'
import { useSession } from '../auth/session'
import * as stub from './_stubs/profile'

const PROFILE_BASE = '/api/v1/jobseeker/profile'
const EDUCATION_BASE = '/api/v1/jobseeker/education'
const EXPERIENCE_BASE = '/api/v1/jobseeker/work-experience'
const SKILL_BASE = '/api/v1/jobseeker/skills'

export function getDashboardSummary() {
  return stub.getDashboardSummary()
}

function toPersonalInfo(p) {
  return {
    dateOfBirth: p.dateOfBirth ?? '',
    gender: p.gender ?? '',
    nationality: p.nationality ?? '',
    location: p.currentLocation ?? '',
    professionalTitle: p.headline ?? '',
    summary: p.professionalSummary ?? '',
  }
}

export async function getProfile() {
  const [profileRes, education, experience, skills] = await Promise.all([
    getJson(PROFILE_BASE, { auth: true }),
    listEducation(),
    listExperience(),
    listSkills(),
  ])
  return { personalInfo: toPersonalInfo(profileRes.data), education, experience, skills }
}

/** Saves the Personal Info tab: name comes from the signed-in session, not this form. */
export async function updateProfile(personalInfo) {
  const session = useSession()
  const fullName = (session.user.value?.fullName ?? '').trim()
  const [firstName, ...rest] = fullName.split(/\s+/).filter(Boolean)
  await request('PUT', `${PROFILE_BASE}/personal-details`, {
    body: {
      firstName: firstName || 'Job',
      lastName: rest.join(' ') || 'Seeker',
      dateOfBirth: personalInfo.dateOfBirth || null,
      gender: personalInfo.gender || null,
      nationality: personalInfo.nationality || null,
      currentLocation: personalInfo.location || null,
      phone: session.user.value?.phone ?? null,
      contactEmail: session.user.value?.email ?? null,
    },
    auth: true,
  })
  const res = await request('PATCH', `${PROFILE_BASE}/headline-summary`, {
    body: { headline: personalInfo.professionalTitle || '', professionalSummary: personalInfo.summary || '' },
    auth: true,
  })
  return toPersonalInfo(res.data)
}

// ---- Education ----

function toEducation(e) {
  return {
    id: e.educationId,
    qualification: e.qualification ?? '',
    institution: e.institutionName ?? '',
    fieldOfStudy: e.fieldOfStudy ?? '',
    startDate: e.startDate ?? '',
    endDate: e.current ? '' : (e.endDate ?? ''),
    currentlyStudying: !!e.current,
  }
}
function educationRequest(item) {
  return {
    institutionName: item.institution || '',
    qualification: item.qualification || '',
    fieldOfStudy: item.fieldOfStudy || null,
    startDate: item.startDate || null,
    endDate: item.currentlyStudying ? null : (item.endDate || null),
    current: !!item.currentlyStudying,
  }
}

export async function listEducation() {
  const res = await getJson(EDUCATION_BASE, { auth: true })
  return res.data.map(toEducation)
}
export async function addEducation(item) {
  const res = await request('POST', EDUCATION_BASE, { body: educationRequest(item), auth: true })
  return toEducation(res.data)
}
export async function updateEducation(id, item) {
  const res = await request('PUT', `${EDUCATION_BASE}/${id}`, { body: educationRequest(item), auth: true })
  return toEducation(res.data)
}
export async function removeEducation(id) {
  await request('DELETE', `${EDUCATION_BASE}/${id}`, { auth: true })
}

// ---- Work experience ----

function toExperience(e) {
  return {
    id: e.workExperienceId,
    jobTitle: e.jobTitle ?? '',
    company: e.companyName ?? '',
    location: e.location ?? '',
    startDate: e.startDate ?? '',
    endDate: e.current ? '' : (e.endDate ?? ''),
    currentlyWorking: !!e.current,
    description: e.description ?? '',
  }
}
function experienceRequest(item) {
  return {
    companyName: item.company || '',
    jobTitle: item.jobTitle || '',
    location: item.location || null,
    description: item.description || null,
    startDate: item.startDate || null,
    endDate: item.currentlyWorking ? null : (item.endDate || null),
    current: !!item.currentlyWorking,
  }
}

export async function listExperience() {
  const res = await getJson(EXPERIENCE_BASE, { auth: true })
  return res.data.map(toExperience)
}
export async function addExperience(item) {
  const res = await request('POST', EXPERIENCE_BASE, { body: experienceRequest(item), auth: true })
  return toExperience(res.data)
}
export async function updateExperience(id, item) {
  const res = await request('PUT', `${EXPERIENCE_BASE}/${id}`, { body: experienceRequest(item), auth: true })
  return toExperience(res.data)
}
export async function removeExperience(id) {
  await request('DELETE', `${EXPERIENCE_BASE}/${id}`, { auth: true })
}

// ---- Skills (add/remove only - the real backend has no "update a skill" endpoint) ----

function toSkill(s) {
  return { id: s.id, name: s.skillName ?? '' }
}

export async function listSkills() {
  const res = await getJson(SKILL_BASE, { auth: true })
  return res.data.map(toSkill)
}
export async function addSkill(name) {
  const res = await request('POST', SKILL_BASE, { body: { skillName: name, proficiency: null, yearsExperience: null }, auth: true })
  return toSkill(res.data)
}
export async function removeSkill(id) {
  await request('DELETE', `${SKILL_BASE}/${id}`, { auth: true })
}
