// The signed-in user, shared app-wide. The token survives a page reload (localStorage); the backend
// still ends sessions after 10 idle minutes, and any 401 (or ACCOUNT_DISABLED) on an authenticated
// call clears it here and notifies the listener registered with onSessionEnded (main.js redirects).
import { computed, reactive, readonly } from 'vue'
import { configureAuth } from '../api/http'
import * as authApi from '../api/authApi'

const STORAGE_KEY = 'halokazi.session'

// endedReason: why the last session ended without the user logging out - 'expired' | 'disabled' | 'ended' | null
const state = reactive({ token: null, user: null, endedReason: null })
let loggingOut = false
let sessionEndedListener = () => {}

function persist() {
  try {
    if (state.token) localStorage.setItem(STORAGE_KEY, JSON.stringify({ token: state.token, user: state.user }))
    else localStorage.removeItem(STORAGE_KEY)
  } catch {
    // Storage blocked (private mode): the session just won't survive a reload.
  }
}

function restore() {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? 'null')
    if (saved?.token) Object.assign(state, { token: saved.token, user: saved.user })
  } catch {
    // Ignore unreadable storage.
  }
}

function clear() {
  state.token = null
  state.user = null
  persist()
}

/** Store the AuthResponse returned by login or OTP verification. */
function start(authResponse) {
  state.token = authResponse.token
  state.user = authResponse.user
  state.endedReason = null
  persist()
}

/** Re-checks the token with the backend; resolves to the user, or null if the session is gone. */
async function refresh() {
  if (!state.token) return null
  try {
    state.user = await authApi.fetchMe()
    persist()
    return state.user
  } catch {
    return state.token ? state.user : null // a dead session was already cleared; network errors keep the cached user
  }
}

async function logout() {
  loggingOut = true
  try {
    if (state.token) await authApi.logout()
  } catch {
    // The token may already be dead; logging out locally is what matters.
  } finally {
    loggingOut = false
    state.endedReason = null
    clear()
  }
}

/** Where each role lands after signing in. */
function homeFor(user) {
  switch (user?.role) {
    case 'EMPLOYER':
      return '/employer'
    case 'ADMIN':
    case 'SUPER_ADMIN':
      return '/admin'
    default:
      return '/dashboard'
  }
}

const REASONS = { SESSION_EXPIRED: 'expired', ACCOUNT_DISABLED: 'disabled' }

restore()
configureAuth({
  getToken: () => state.token,
  onUnauthorized: (error) => {
    if (!state.token || loggingOut) return
    state.endedReason = REASONS[error.code] ?? 'ended'
    clear()
    sessionEndedListener()
  },
})

export function useSession() {
  return {
    state: readonly(state),
    user: computed(() => state.user),
    isAuthenticated: computed(() => Boolean(state.token)),
    start,
    refresh,
    logout,
    homeFor,
    /** Returns and forgets why the previous session ended ('expired' | 'disabled' | 'ended' | null). */
    takeEndedReason() {
      const reason = state.endedReason
      state.endedReason = null
      return reason
    },
  }
}

/** Called after a session ends on its own (expired, revoked, account disabled) - not after logout. */
export function onSessionEnded(listener) {
  sessionEndedListener = listener
}
