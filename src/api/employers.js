// No public EmployerController endpoint exists yet - delegates to the stub adapter.
import { http } from './http'
import * as stub from './_stubs/employers'

const USE_STUB = true

export function listTopEmployers(params) {
  return USE_STUB ? stub.listTopEmployers(params) : http.get('/employers/top', { query: params })
}
