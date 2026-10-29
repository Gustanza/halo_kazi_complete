// Thin fetch wrapper for the Halokazi backend. Every backend error has the shape
// { code, message, fieldErrors?, retryAfterSeconds?, details? } - see back_resources/docs/Halokazi_API.pdf.

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

let tokenProvider = () => null
let unauthorizedHandler = () => {}

/** Wired up by the session store so this module never imports it (avoids a cycle). */
export function configureAuth({ getToken, onUnauthorized }) {
  tokenProvider = getToken
  unauthorizedHandler = onUnauthorized
}

export class ApiError extends Error {
  constructor(status, body) {
    super(body?.message || `Request failed (${status})`)
    this.status = status
    this.code = body?.code ?? (status === 0 ? 'NETWORK_ERROR' : 'UNKNOWN_ERROR')
    this.fieldErrors = body?.fieldErrors ?? []
    this.retryAfterSeconds = body?.retryAfterSeconds ?? null
    this.details = body?.details ?? {}
  }

  /** { phone: 'message', ... } for showing errors next to form fields. */
  fieldMap() {
    return Object.fromEntries(this.fieldErrors.map((e) => [e.field, e.message]))
  }
}

function buildUrl(path, params) {
  const query = new URLSearchParams()
  for (const [key, value] of Object.entries(params ?? {})) {
    if (value !== undefined && value !== null && value !== '') query.append(key, value)
  }
  const qs = query.toString()
  return `${BASE_URL}${path}${qs ? `?${qs}` : ''}`
}

/** auth: true sends the session token and treats a 401 as "logged out". */
export async function request(method, path, { params, body, signal, auth = false } = {}) {
  const headers = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  const token = auth ? tokenProvider() : null
  if (token) headers.Authorization = `Bearer ${token}`

  let response
  try {
    response = await fetch(buildUrl(path, params), {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
      signal,
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    throw new ApiError(0, { message: 'Could not reach the server. Is the backend running?' })
  }

  if (response.status === 204) return null
  const data = await response.json().catch(() => null)
  if (!response.ok) {
    const error = new ApiError(response.status, data)
    // The session is over: missing/expired token (401) or the account was suspended meanwhile (403).
    if (auth && (response.status === 401 || error.code === 'ACCOUNT_DISABLED')) unauthorizedHandler(error)
    throw error
  }
  return data
}

export const getJson = (path, options) => request('GET', path, options)
export const postJson = (path, body, options) => request('POST', path, { ...options, body })
