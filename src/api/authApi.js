// Authentication endpoints - back_resources/docs/Halokazi_API.pdf, section 2.
import { getJson, postJson } from './http'

/** { role: 'job_seeker'|'employer', fullName, phone, email, password, repeatPassword } */
export const register = (form) => postJson('/api/auth/register', form)

/** Verifying also logs the user in: returns { token, expiresAt, idleTimeoutSeconds, user }. */
export const verifyOtp = (phone, code) => postJson('/api/auth/otp/verify', { phone, code })

export const resendOtp = (phone) => postJson('/api/auth/otp/resend', { phone })

/** identifier: phone (any format) or email. */
export const login = (identifier, password) => postJson('/api/auth/login', { identifier, password })

/** AUTH-05: texts a reset code to the account's phone. Same answer whether or not the account exists. */
export const forgotPassword = (identifier) => postJson('/api/auth/password/forgot', { identifier })

/** Ends every session of the account; the user then logs in with the new password. */
export const resetPassword = (identifier, code, newPassword, confirmPassword) =>
  postJson('/api/auth/password/reset', { identifier, code, newPassword, confirmPassword })

export const fetchMe = () => getJson('/api/auth/me', { auth: true })

export const logout = () => postJson('/api/auth/logout', undefined, { auth: true })
