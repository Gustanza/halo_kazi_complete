// No NotificationController exists yet, and job alerts have no backing table at all in the
// DB design - every function here delegates to the stub adapter.
import { http } from './http'
import * as stub from './_stubs/notifications'

const USE_STUB = true

export function listNotifications(params) {
  return USE_STUB ? stub.listNotifications(params) : http.get('/notifications', { query: params })
}

export function markNotificationRead(id) {
  return USE_STUB ? stub.markNotificationRead(id) : http.post(`/notifications/${id}/read`)
}

export function markAllNotificationsRead() {
  return USE_STUB ? stub.markAllNotificationsRead() : http.post('/notifications/read-all')
}

export function listAlerts() {
  return USE_STUB ? stub.listAlerts() : http.get('/job-alerts')
}

export function createAlert(payload) {
  return USE_STUB ? stub.createAlert(payload) : http.post('/job-alerts', payload)
}

export function toggleAlert(id) {
  return USE_STUB ? stub.toggleAlert(id) : http.post(`/job-alerts/${id}/toggle`)
}

export function deleteAlert(id) {
  return USE_STUB ? stub.deleteAlert(id) : http.delete(`/job-alerts/${id}`)
}
