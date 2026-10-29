// Dashboard summary numbers only. Personal info/education/experience/skills (the former
// contents of this file) are real now - see src/api/profile.js, which calls
// JobSeekerProfileController and its per-section sibling controllers directly.
import { delay } from './helpers'
import { listCvs } from '../cv'
import { listApplications } from '../applications'
import { listSavedJobIds, listJobs } from '../jobs'
import { getAlertPreferences } from '../notifications'

export async function getDashboardSummary() {
  await delay()
  const [cvs, applications, savedIds, alertPreferences, recommended] = await Promise.all([
    listCvs(),
    listApplications({ pageSize: 1000 }),
    listSavedJobIds(),
    getAlertPreferences().catch(() => ({ receiveJobAlerts: false })),
    listJobs({ pageSize: 3 }),
  ])
  return {
    stats: [
      { key: 'applications', value: applications.total, label: 'Applications', to: '/dashboard/applications', color: '#2F8FF5' },
      { key: 'saved', value: savedIds.length, label: 'Saved Jobs', to: '/dashboard/saved-jobs', color: '#2FB573' },
      { key: 'alerts', value: alertPreferences.receiveJobAlerts ? 1 : 0, label: 'Job Alerts Active', to: '/dashboard/job-alerts', color: '#FD5C20' },
      { key: 'cvs', value: cvs.length, label: 'CVs Created', to: '/dashboard/my-cvs', color: '#B064F7' },
    ],
    recommendedJobs: recommended.items,
  }
}
