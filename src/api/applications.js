// No ApplicationController exists yet - every function here delegates to the stub adapter.
import { http } from './http'
import * as stub from './_stubs/applications'

const USE_STUB = true

export function listApplications(params) {
  return USE_STUB ? stub.listApplications(params) : http.get('/applications', { query: params })
}

export function getApplication(id) {
  return USE_STUB ? stub.getApplication(id) : http.get(`/applications/${id}`)
}

export function createApplication(payload) {
  return USE_STUB ? stub.createApplication(payload) : http.post('/applications', payload)
}

export function hasAppliedTo(jobId) {
  return USE_STUB ? stub.hasAppliedTo(jobId) : http.get(`/vacancies/${jobId}/has-applied`)
}
