// Thin fetch wrapper around the Halokazi backend. No axios dependency - the app only
// needs JSON in/out, a bearer token, and one place to normalize errors.

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
const TOKEN_KEY = 'halokazi-token'

let token = null
try {
  token = localStorage.getItem(TOKEN_KEY)
} catch {
  // localStorage unavailable (private browsing, etc.)
}

/** Shape of every error this module throws. `code`/`fieldErrors` mirror the backend's ApiError. */
export class ApiRequestError extends Error {
  constructor({ status, code, message, fieldErrors, retryAfterSeconds, details }) {
    super(message)
    this.name = 'ApiRequestError'
    this.status = status
    this.code = code
    this.fieldErrors = fieldErrors || []
    this.retryAfterSeconds = retryAfterSeconds ?? null
    this.details = details || {}
  }

  /** Convenience lookup for showing a message under a specific form field. */
  fieldMessage(field) {
    return this.fieldErrors.find((e) => e.field === field)?.message ?? null
  }
}

/** Called by the auth store on login/logout; keeps the in-memory token and storage in sync. */
export function setAuthToken(nextToken) {
  token = nextToken
  try {
    if (nextToken) localStorage.setItem(TOKEN_KEY, nextToken)
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    // ignore persistence failures
  }
}

export function getAuthToken() {
  return token
}

/** Set once by the auth store so a 401 can trigger logout + redirect without a circular import. */
let unauthorizedHandler = null
export function onUnauthorized(handler) {
  unauthorizedHandler = handler
}

async function request(path, { method = 'GET', body, query, skipAuth = false } = {}) {
  const url = new URL(BASE_URL.replace(/\/$/, '') + path, window.location.origin)
  if (query) {
    for (const [key, value] of Object.entries(query)) {
      if (value === undefined || value === null || value === '') continue
      url.searchParams.set(key, value)
    }
  }

  const headers = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token && !skipAuth) headers.Authorization = `Bearer ${token}`

  let response
  try {
    response = await fetch(url, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiRequestError({
      status: 0,
      code: 'NETWORK_ERROR',
      message: 'Could not reach the server. Check your connection and try again.',
    })
  }

  if (response.status === 204) return null

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    if (response.status === 401 && !skipAuth) unauthorizedHandler?.()
    throw new ApiRequestError({
      status: response.status,
      code: data?.code ?? 'UNKNOWN_ERROR',
      message: data?.message ?? 'Something went wrong. Please try again.',
      fieldErrors: data?.fieldErrors,
      retryAfterSeconds: data?.retryAfterSeconds,
      details: data?.details,
    })
  }

  return data
}

export const http = {
  get: (path, options) => request(path, { ...options, method: 'GET' }),
  post: (path, body, options) => request(path, { ...options, method: 'POST', body }),
  put: (path, body, options) => request(path, { ...options, method: 'PUT', body }),
  patch: (path, body, options) => request(path, { ...options, method: 'PATCH', body }),
  delete: (path, options) => request(path, { ...options, method: 'DELETE' }),
}
