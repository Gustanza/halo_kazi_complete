// Notifications stand in for the `notification`/`notification_template` tables (no
// NotificationController exists yet). Job alerts have no backing table in the DB design at
// all (back_resources/db/halokazi.dbml has no job_alert table) - this stub is the only
// place that feature exists until a real module is designed for it.
import { clone, delay, generateId } from './helpers'

let NOTIFICATIONS = [
  { id: 1, title: 'Application shortlisted', body: 'Your application for Frontend Developer at Google was shortlisted.', readAt: null, createdAt: '2026-10-18T09:00:00+03:00' },
  { id: 2, title: 'New matching job', body: 'A new Digital Marketing Specialist role at Halotel PLC matches your profile.', readAt: null, createdAt: '2026-10-27T09:00:00+03:00' },
  { id: 3, title: 'Profile reminder', body: 'Complete your profile to increase your chances of getting hired.', readAt: '2026-10-10T09:00:00+03:00', createdAt: '2026-10-09T09:00:00+03:00' },
]

let ALERTS = [
  { id: 1, keyword: 'Frontend Developer', location: 'Dar es Salaam', active: true },
  { id: 2, keyword: 'Marketing', location: 'Any location', active: true },
]

export async function listNotifications({ page = 1, pageSize = 20 } = {}) {
  await delay()
  const sorted = [...NOTIFICATIONS].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  const start = (page - 1) * pageSize
  return {
    items: clone(sorted.slice(start, start + pageSize)),
    total: sorted.length,
    unreadCount: sorted.filter((n) => !n.readAt).length,
  }
}

export async function markNotificationRead(id) {
  await delay(150)
  NOTIFICATIONS = NOTIFICATIONS.map((n) => (n.id === Number(id) ? { ...n, readAt: new Date().toISOString() } : n))
}

export async function markAllNotificationsRead() {
  await delay(200)
  const now = new Date().toISOString()
  NOTIFICATIONS = NOTIFICATIONS.map((n) => (n.readAt ? n : { ...n, readAt: now }))
}

export async function listAlerts() {
  await delay(200)
  return clone(ALERTS)
}

export async function createAlert({ keyword, location }) {
  await delay(300)
  const record = { id: generateId(), keyword, location, active: true }
  ALERTS = [...ALERTS, record]
  return clone(record)
}

export async function toggleAlert(id) {
  await delay(150)
  ALERTS = ALERTS.map((a) => (a.id === Number(id) ? { ...a, active: !a.active } : a))
}

export async function deleteAlert(id) {
  await delay(200)
  ALERTS = ALERTS.filter((a) => a.id !== Number(id))
}
