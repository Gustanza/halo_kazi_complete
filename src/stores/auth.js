// Singleton reactive auth store - same module-singleton pattern as composables/useTheme.js.
// No Pinia: the app is small enough that one reactive object shared by import is simpler.
import { computed, ref } from 'vue'
import * as authApi from '../api/auth'
import { ApiRequestError, getAuthToken, onUnauthorized, setAuthToken } from '../api/http'

const user = ref(null)
const initializing = ref(true)

const isAuthenticated = computed(() => !!user.value)
const isEmployer = computed(() => user.value?.role === 'EMPLOYER')
const isJobSeeker = computed(() => user.value?.role === 'JOB_SEEKER')

function setSession(authResponse) {
  setAuthToken(authResponse.token)
  user.value = authResponse.user
}

function clearSession() {
  setAuthToken(null)
  user.value = null
}

onUnauthorized(() => {
  clearSession()
})

async function register(payload) {
  return authApi.register(payload)
}

async function verifyOtp(payload) {
  const result = await authApi.verifyOtp(payload)
  setSession(result)
  return result
}

async function resendOtp(payload) {
  return authApi.resendOtp(payload)
}

async function login(payload) {
  const result = await authApi.login(payload)
  setSession(result)
  return result
}

async function forgotPassword(payload) {
  return authApi.forgotPassword(payload)
}

async function resetPassword(payload) {
  return authApi.resetPassword(payload)
}

async function logout() {
  try {
    if (getAuthToken()) await authApi.logout()
  } catch {
    // best-effort - clear local session regardless of whether the server call succeeded
  } finally {
    clearSession()
  }
}

/** Called once on app boot so a stored token resolves to a user before routes render. */
async function restoreSession() {
  if (!getAuthToken()) {
    initializing.value = false
    return
  }
  try {
    user.value = await authApi.me()
  } catch (error) {
    if (!(error instanceof ApiRequestError) || error.status !== 401) {
      // Non-auth failure (e.g. network down) - keep the token, let the next request retry.
    } else {
      clearSession()
    }
  } finally {
    initializing.value = false
  }
}

export function useAuthStore() {
  return {
    user,
    initializing,
    isAuthenticated,
    isEmployer,
    isJobSeeker,
    register,
    verifyOtp,
    resendOtp,
    login,
    logout,
    restoreSession,
    forgotPassword,
    resetPassword,
  }
}
