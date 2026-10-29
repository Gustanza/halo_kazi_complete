// No CvController exists yet - every function here delegates to the stub adapter.
import { http } from './http'
import * as stub from './_stubs/cv'

const USE_STUB = true

export function listTemplates() {
  return USE_STUB ? stub.listTemplates() : http.get('/cv-templates')
}

export function listCvs() {
  return USE_STUB ? stub.listCvs() : http.get('/cvs')
}

export function getCv(id) {
  return USE_STUB ? stub.getCv(id) : http.get(`/cvs/${id}`)
}

export function getLastUsedCv() {
  return USE_STUB ? stub.getLastUsedCv() : http.get('/cvs/last-used')
}

export function createCv(payload) {
  return USE_STUB ? stub.createCv(payload) : http.post('/cvs', payload)
}

export function updateCv(id, patch) {
  return USE_STUB ? stub.updateCv(id, patch) : http.patch(`/cvs/${id}`, patch)
}

export function setLastUsedCv(id) {
  return USE_STUB ? stub.setLastUsedCv(id) : http.post(`/cvs/${id}/last-used`)
}

export function deleteCv(id) {
  return USE_STUB ? stub.deleteCv(id) : http.delete(`/cvs/${id}`)
}
