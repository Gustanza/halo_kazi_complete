// No VacancyController exists yet - every function here delegates to the stub adapter.
// Flip USE_STUB to false and point `http` at the real endpoints once that module ships.
import { http } from './http'
import * as stub from './_stubs/jobs'

const USE_STUB = true

export function listJobs(params) {
  return USE_STUB ? stub.listJobs(params) : http.get('/vacancies', { query: params })
}

export function getJob(id) {
  return USE_STUB ? stub.getJob(id) : http.get(`/vacancies/${id}`)
}

export function listCategories() {
  return USE_STUB ? stub.listCategories() : http.get('/job-categories')
}

export function listSavedJobIds() {
  return USE_STUB ? stub.listSavedJobIds() : http.get('/saved-vacancies/ids')
}

export function saveJob(id) {
  return USE_STUB ? stub.saveJob(id) : http.post(`/saved-vacancies/${id}`)
}

export function unsaveJob(id) {
  return USE_STUB ? stub.unsaveJob(id) : http.delete(`/saved-vacancies/${id}`)
}
