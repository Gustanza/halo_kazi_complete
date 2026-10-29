// Thin fetch wrapper for the Halokazi backend. Every backend error has the shape
// { code, message, fieldErrors?, retryAfterSeconds?, details? } - see back_resources/docs/Halokazi_API.pdf.
//
// Two call styles share this one client, so neither side of a frontend merge has to be rewritten:
// - getJson/postJson/request(method, path, { auth }) - used by auth/session.js, authApi.js,
//   accountApi.js, publicApi.js and savedJobsApi.js (auth is opt-in via `auth: true`).
// - the `http.get/post/put/patch/delete` object + `ApiRequestError` - used by the stub-backed
//   api/*.js modules (jobs, cv, applications, profile, notifications, employers) and their
//   views; these always send the token when one is set, since every one of their endpoints is
//   behind login.

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''

let tokenProvider = () => null
let unauthorizedHandler = () => {}

/** Wired up by auth/session.js so this module never imports it (avoids a cycle). */
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

  /** Same lookup, single-field convenience. */
  fieldMessage(field) {
    return this.fieldErrors.find((e) => e.field === field)?.message ?? null
  }
}

/** Alias kept for the stub-backed api/*.js modules, written before this file had a session store. */
export const ApiRequestError = ApiError

function buildUrl(path, params) {
  const query = new URLSearchParams()
  for (const [key, value] of Object.entries(params ?? {})) {
    if (value !== undefined && value !== null && value !== '') query.append(key, value)
  }
  const qs = query.toString()
  return `${BASE_URL}${path}${qs ? `?${qs}` : ''}`
}

/** auth: true sends the session token and treats a 401 (or ACCOUNT_DISABLED) as "logged out". */
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
    if (auth && (response.status === 401 || error.code === 'ACCOUNT_DISABLED')) unauthorizedHandler(error)
    throw error
  }
  return data
}

export const getJson = (path, options) => request('GET', path, options)
export const postJson = (path, body, options) => request('POST', path, { ...options, body })

// `query` (this object's pre-session.js name for `params`) is accepted as an alias so the
// stub-backed api/*.js modules didn't need touching for this.
function withAuth(options) {
  const { query, params, ...rest } = options ?? {}
  return { ...rest, params: params ?? query, auth: true }
}

export const http = {
  get: (path, options) => request('GET', path, withAuth(options)),
  post: (path, body, options) => request('POST', path, { ...withAuth(options), body }),
  put: (path, body, options) => request('PUT', path, { ...withAuth(options), body }),
  patch: (path, body, options) => request('PATCH', path, { ...withAuth(options), body }),
  delete: (path, options) => request('DELETE', path, withAuth(options)),
}
