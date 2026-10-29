// Notifications: alert/controller/NotificationController.java (/api/v1/notifications), real
// and JPA-backed. Job alerts: alert/controller/JobAlertController.java (/api/v1/job-alerts) -
// a single preferences object (receiveJobAlerts + category/region/employment-type ids) plus a
// matched-vacancies list, not a list of named alert rules (there is no such model on the real
// backend - a fake duplicate at /api/v1/jobseeker/alerts backed by an in-memory store exists,
// but is not used here).
import { getJson, request } from './http'

/** { items: [{id,title,body,readAt,createdAt}], total, unreadCount } */
export async function listNotifications({ page = 1, pageSize = 20 } = {}) {
  const [page0, unread] = await Promise.all([
    getJson('/api/v1/notifications', { params: { page: page - 1, size: pageSize }, auth: true }),
    getJson('/api/v1/notifications/unread-count', { auth: true }),
  ])
  const content = page0.data.content
  return {
    items: content.map((n) => ({
      id: n.id,
      title: n.title,
      body: n.message,
      // NotificationDto's boolean getter isRead() serializes as "read", not "isRead".
      readAt: n.read ? (n.readAt ?? new Date().toISOString()) : null,
      createdAt: n.createdAt,
    })),
    total: page0.data.totalElements,
    unreadCount: unread.data.unreadCount,
  }
}

export function markNotificationRead(id) {
  return request('PATCH', `/api/v1/notifications/${id}/read`, { auth: true })
}

export function markAllNotificationsRead() {
  return request('PATCH', '/api/v1/notifications/read-all', { auth: true })
}

/** { receiveJobAlerts, categoryIds, regionIds, employmentTypeIds } */
export async function getAlertPreferences() {
  const res = await getJson('/api/v1/job-alerts/preferences', { auth: true })
  return res.data
}

export async function updateAlertPreferences(preferences) {
  const res = await request('PUT', '/api/v1/job-alerts/preferences', { body: preferences, auth: true })
  return res.data
}

/** Vacancies currently matching the job seeker's saved alert preferences. */
export async function getMatchedVacancies() {
  const res = await getJson('/api/v1/job-alerts/matches', { auth: true })
  return res.data
}

/** { id, code, nameEn, nameSw }[] - for building the alert preference checkboxes. */
export async function getJobCategories() {
  const res = await getJson('/api/v1/vacancies/meta/categories', { auth: true })
  return res.data
}

export async function getEmploymentTypes() {
  const res = await getJson('/api/v1/vacancies/meta/employment-types', { auth: true })
  return res.data
}

export async function getRegions() {
  const res = await getJson('/api/v1/vacancies/meta/regions', { auth: true })
  return res.data
}
