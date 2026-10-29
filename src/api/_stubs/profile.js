// Stands in for the job-seeker profile module (JobSeekerProfile is a bare JPA entity today,
// with no controller). Dashboard summary numbers come from the other stub modules so they
// stay consistent with My CVs / Applications / Saved Jobs / Job Alerts once real data lands.
//
// `PROFILE` is the one source of truth for personal/education/experience/skills data -
// ProfileView.vue edits it directly, and Settings.vue's "Account Information" display reads
// the overlapping fields (dateOfBirth, gender, location, about) from here too, merged with
// the auth store's user (fullName/email/phone) - previously these were two separate mock
// objects with different fake names ("Raymond Mwangi" vs "John Mwangi").
import { delay } from './helpers'
import { listCvs } from './cv'
import { listApplications } from './applications'
import { listSavedJobIds, listJobs } from './jobs'
import { listAlerts } from './notifications'

function emptyProfile() {
  return {
    personalInfo: {
      dateOfBirth: '',
      gender: '',
      nationality: 'Tanzania',
      location: '',
      professionalTitle: '',
      summary: '',
    },
    education: [],
    experience: [],
    skills: [],
  }
}

let PROFILE = emptyProfile()

let SETTINGS = {
  notifications: { email: true, applications: true, jobAlerts: true, newsletter: false },
  privacy: { profileVisible: true, showEmail: false, showPhone: false },
  preferences: { language: 'English', region: 'Tanzania', appearance: 'Light' },
}

export async function getProfile() {
  await delay()
  return JSON.parse(JSON.stringify(PROFILE))
}

export async function updateProfile(patch) {
  await delay(400)
  PROFILE = { ...PROFILE, ...patch }
  return JSON.parse(JSON.stringify(PROFILE))
}

export async function getSettings() {
  await delay(200)
  return JSON.parse(JSON.stringify(SETTINGS))
}

export async function updateSettings(patch) {
  await delay(300)
  SETTINGS = {
    notifications: { ...SETTINGS.notifications, ...patch.notifications },
    privacy: { ...SETTINGS.privacy, ...patch.privacy },
    preferences: { ...SETTINGS.preferences, ...patch.preferences },
  }
  return JSON.parse(JSON.stringify(SETTINGS))
}

/** AuthController has no change-password endpoint yet. */
export async function changePassword({ currentPassword, newPassword }) {
  await delay(500)
  if (!currentPassword || newPassword.length < 8) {
    throw new Error('Password must contain at least 8 characters.')
  }
  return { message: 'Password updated.' }
}

export async function getDashboardSummary() {
  await delay()
  const [cvs, applications, savedIds, alerts, recommended] = await Promise.all([
    listCvs(),
    listApplications({ pageSize: 1000 }),
    listSavedJobIds(),
    listAlerts(),
    listJobs({ pageSize: 3 }),
  ])
  return {
    stats: [
      { key: 'applications', value: applications.total, label: 'Applications', to: '/dashboard/applications', color: '#2F8FF5' },
      { key: 'saved', value: savedIds.length, label: 'Saved Jobs', to: '/dashboard/saved-jobs', color: '#2FB573' },
      { key: 'alerts', value: alerts.filter((a) => a.active).length, label: 'Job Alerts Active', to: '/dashboard/job-alerts', color: '#FD5C20' },
      { key: 'cvs', value: cvs.length, label: 'CVs Created', to: '/dashboard/my-cvs', color: '#B064F7' },
    ],
    recommendedJobs: recommended.items,
  }
}
