// AuthController has no forgot/reset-password endpoints yet (only register/login/otp/logout/me),
// even though the DB design already has a password_reset_token table. Stand in until it ships.
import { delay } from './helpers'

export async function forgotPassword(_payload) {
  await delay(500)
  return { message: 'If an account exists for that phone or email, a reset code has been sent.' }
}

export async function resetPassword(_payload) {
  await delay(500)
  return { message: 'Your password has been reset. You can now log in.' }
}
