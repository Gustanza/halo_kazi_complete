<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Eye, EyeOff, Lock, UserRound } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import FormAlert from '../../../components/common/FormAlert.vue'
import { login, resendOtp } from '../../../api/authApi'
import { useSession } from '../../../auth/session'
import { formatWait } from '../../../auth/formatting'

const route = useRoute()
const router = useRouter()
const session = useSession()

const identifier = ref('') // phone number (any format) or email - AUTH-04
const password = ref('')
const showPassword = ref(false)
const submitting = ref(false)
const error = ref('')
const unverifiedPhone = ref('') // set when the account exists but never finished OTP verification

// Why the previous session ended, when that is what brought the user here.
const ENDED_MESSAGES = {
  expired: 'Your session expired after a period of inactivity. Please log in again.',
  disabled: 'Your account is no longer active. Contact support if you think this is a mistake.',
  ended: 'You were signed out. Please log in again.',
}
const endedMessage = ENDED_MESSAGES[session.takeEndedReason()] ?? ''

/** Only same-site paths, so ?redirect= can't send people elsewhere. */
function redirectTarget() {
  const target = route.query.redirect
  return typeof target === 'string' && target.startsWith('/') && !target.startsWith('//') ? target : null
}

async function handleSubmit() {
  error.value = ''
  unverifiedPhone.value = ''
  submitting.value = true
  try {
    // OTP happens once at registration; login is phone or email + password (AUTH-04).
    const auth = await login(identifier.value, password.value)
    session.start(auth)
    router.replace(redirectTarget() ?? session.homeFor(auth.user))
  } catch (e) {
    if (e.code === 'PHONE_NOT_VERIFIED') {
      unverifiedPhone.value = e.details.phone
      error.value = 'Your phone number is not verified yet.'
    } else if (e.code === 'ACCOUNT_LOCKED') {
      error.value = `Too many failed attempts. Try again in ${formatWait(e.retryAfterSeconds)}.`
    } else {
      error.value = e.message
    }
  } finally {
    submitting.value = false
  }
}

/** Sends a fresh code (the original one has long expired) and continues on the OTP screen. */
async function verifyNow() {
  let wait
  try {
    wait = (await resendOtp(unverifiedPhone.value)).resendAvailableInSeconds
  } catch (e) {
    wait = e.retryAfterSeconds ?? undefined // rate-limited: the last code may still be valid
  }
  router.push({ name: 'verify-otp', query: { phone: unverifiedPhone.value, resend: wait } })
}
</script>

<template>
  <AuthSplitLayout
    heading-prefix="Welcome back to"
    heading-highlight="Halokazi!"
    subtitle="Log in to your account to continue searching for jobs, using the CV Builder, and managing your applications"
    :show-illustration="false"
  >
    <h2 class="text-xl font-bold" style="color: var(--color-text)">Log in to your account</h2>
    <p class="mt-1 text-sm" style="color: var(--color-text-muted)">Use your phone number or email and your password to log in.</p>

    <FormAlert v-if="endedMessage && !error" tone="info" class="mt-5">{{ endedMessage }}</FormAlert>

    <form class="mt-7 space-y-6" @submit.prevent="handleSubmit">
      <FormAlert v-if="error">
        {{ error }}
        <button v-if="unverifiedPhone" type="button" class="ml-1 font-semibold underline" @click="verifyNow">
          Verify it now
        </button>
      </FormAlert>

      <div>
        <label for="login-identifier" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">
          Phone number or email
        </label>
        <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
          <UserRound :size="16" style="color: var(--color-text-muted)" />
          <input
            id="login-identifier"
            v-model="identifier"
            type="text"
            inputmode="email"
            autocomplete="username"
            required
            placeholder="07XX XXX XXX or you@example.com"
            class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
            style="color: var(--color-text)"
          />
        </div>
      </div>

      <div>
        <div class="mb-1.5 flex items-center justify-between">
          <label for="login-password" class="block text-xs font-medium" style="color: var(--color-text-muted)">Password</label>
          <RouterLink
            :to="{ name: 'forgot-password', query: identifier ? { identifier } : {} }"
            class="text-xs font-semibold text-primary"
          >
            Forgot password?
          </RouterLink>
        </div>
        <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
          <Lock :size="16" style="color: var(--color-text-muted)" />
          <input
            id="login-password"
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            autocomplete="current-password"
            required
            placeholder="Enter your password"
            class="w-full bg-transparent py-2.5 text-sm focus:outline-none"
            style="color: var(--color-text)"
          />
          <button type="button" class="px-3" style="color: var(--color-text-muted)" @click="showPassword = !showPassword">
            <EyeOff v-if="showPassword" :size="17" />
            <Eye v-else :size="17" />
          </button>
        </div>
      </div>

      <button type="submit" class="btn-primary w-full disabled:opacity-70" :disabled="submitting">
        {{ submitting ? 'Logging in…' : 'Login' }}
      </button>
    </form>

    <p class="mt-6 text-center text-sm" style="color: var(--color-text-muted)">
      Don't have an account? <RouterLink to="/register" class="font-semibold text-primary">Register here</RouterLink>
    </p>
  </AuthSplitLayout>
</template>
