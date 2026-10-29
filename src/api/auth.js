// Real calls to the backend's AuthController (/api/auth/**). This is the one module wired
// directly to the backend from day one - every other api/* module goes through a stub
// adapter until its backend module exists (see src/api/_stubs).
import { http } from './http'
import * as stub from './_stubs/auth'

// AuthController doesn't implement forgot/reset-password yet - flip to false once it does.
const USE_STUB_FOR_PASSWORD_RESET = true

/** @param {{ role: 'job_seeker'|'employer', fullName: string, phone: string, email: string, password: string, repeatPassword: string }} payload */
export function register(payload) {
  return http.post('/auth/register', {
    role: payload.role.toUpperCase(),
    fullName: payload.fullName,
    phone: payload.phone,
    email: payload.email,
    password: payload.password,
    repeatPassword: payload.repeatPassword,
  })
}

/** @param {{ phone: string, code: string }} payload */
export function verifyOtp(payload) {
  return http.post('/auth/otp/verify', payload)
}

/** @param {{ phone: string }} payload */
export function resendOtp(payload) {
  return http.post('/auth/otp/resend', payload)
}

/** @param {{ identifier: string, password: string }} payload */
export function login(payload) {
  return http.post('/auth/login', payload)
}

export function logout() {
  return http.post('/auth/logout')
}

export function me() {
  return http.get('/auth/me')
}

/** @param {{ identifier: string }} payload */
export function forgotPassword(payload) {
  return USE_STUB_FOR_PASSWORD_RESET
    ? stub.forgotPassword(payload)
    : http.post('/auth/password/forgot', payload)
}

/** @param {{ identifier: string, code: string, password: string, repeatPassword: string }} payload */
export function resetPassword(payload) {
  return USE_STUB_FOR_PASSWORD_RESET
    ? stub.resetPassword(payload)
    : http.post('/auth/password/reset', payload)
}
