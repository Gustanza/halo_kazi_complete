// No JobSeekerProfile controller exists yet - every function here delegates to the stub adapter.
import { http } from './http'
import * as stub from './_stubs/profile'

const USE_STUB = true

export function getDashboardSummary() {
  return USE_STUB ? stub.getDashboardSummary() : http.get('/profile/dashboard-summary')
}

export function getProfile() {
  return USE_STUB ? stub.getProfile() : http.get('/profile')
}

export function updateProfile(patch) {
  return USE_STUB ? stub.updateProfile(patch) : http.patch('/profile', patch)
}

export function getSettings() {
  return USE_STUB ? stub.getSettings() : http.get('/profile/settings')
}

export function updateSettings(patch) {
  return USE_STUB ? stub.updateSettings(patch) : http.patch('/profile/settings', patch)
}

export function changePassword(payload) {
  return USE_STUB ? stub.changePassword(payload) : http.post('/profile/change-password', payload)
}
