<script setup>
import { computed, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Eye, EyeOff, KeyRound, Lock, ShieldCheck, UserRound } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import FormAlert from '../../../components/common/FormAlert.vue'
import { forgotPassword, resetPassword } from '../../../api/authApi'
import { formatWait } from '../../../auth/formatting'

// AUTH-05: request a code by SMS, then set a new password with it.
const route = useRoute()

const step = ref('request') // 'request' | 'reset' | 'done'
const identifier = ref(typeof route.query.identifier === 'string' ? route.query.identifier : '')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const submitting = ref(false)
const error = ref('')
const notice = ref('')
const fieldErrors = ref({})
const resendCooldown = ref(0)
let timer = null

function startCooldown(seconds) {
  resendCooldown.value = seconds
  window.clearInterval(timer)
  timer = window.setInterval(() => {
    if (resendCooldown.value > 0) resendCooldown.value -= 1
    else window.clearInterval(timer)
  }, 1000)
}
onUnmounted(() => window.clearInterval(timer))

const resendLabel = computed(() =>
  resendCooldown.value > 0 ? `Resend code in ${formatWait(resendCooldown.value)}` : 'Resend code',
)

async function sendCode() {
  error.value = ''
  notice.value = ''
  submitting.value = true
  try {
    const result = await forgotPassword(identifier.value.trim())
    notice.value = result.message
    startCooldown(result.resendAvailableInSeconds)
    step.value = 'reset'
  } catch (e) {
    if (e.retryAfterSeconds) {
      // Rate-limited: a code was already sent recently and may still be valid.
      startCooldown(e.retryAfterSeconds)
      step.value = 'reset'
    }
    error.value = e.message
  } finally {
    submitting.value = false
  }
}

async function resend() {
  if (resendCooldown.value > 0) return
  await sendCode()
  if (!error.value) notice.value = 'A new code is on its way. The previous code no longer works.'
}

async function submitReset() {
  error.value = ''
  notice.value = ''
  fieldErrors.value = {}
  if (newPassword.value !== confirmPassword.value) {
    fieldErrors.value = { confirmPassword: 'Passwords do not match.' }
    return
  }
  submitting.value = true
  try {
    await resetPassword(identifier.value.trim(), code.value.trim(), newPassword.value, confirmPassword.value)
    step.value = 'done'
  } catch (e) {
    fieldErrors.value = e.fieldMap?.() ?? {}
    if (e.code === 'OTP_INVALID' && e.details.remainingAttempts) {
      const left = e.details.remainingAttempts
      error.value = `Incorrect code. ${left} attempt${left === 1 ? '' : 's'} left.`
    } else {
      error.value = e.code === 'VALIDATION_FAILED' ? 'Please correct the highlighted fields.' : e.message
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <AuthSplitLayout
    heading-prefix="Reset your"
    heading-highlight="password"
    subtitle="We'll send a one-time code by SMS to the phone number on your account"
    :show-illustration="false"
  >
    <!-- Step 1: who are you -->
    <div v-if="step === 'request'">
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
        <KeyRound :size="20" />
      </div>
      <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Forgot your password?</h2>
      <p class="mt-1 text-sm" style="color: var(--color-text-muted)">
        Enter the phone number or email you registered with.
      </p>

      <form class="mt-7 space-y-6" @submit.prevent="sendCode">
        <FormAlert v-if="error">{{ error }}</FormAlert>
        <div>
          <label for="reset-identifier" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">
            Phone number or email
          </label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <UserRound :size="16" style="color: var(--color-text-muted)" />
            <input
              id="reset-identifier"
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
        <button type="submit" class="btn-primary w-full disabled:opacity-70" :disabled="submitting">
          {{ submitting ? 'Sending code…' : 'Send reset code' }}
        </button>
      </form>
    </div>

    <!-- Step 2: code + new password -->
    <div v-else-if="step === 'reset'">
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
        <Lock :size="20" />
      </div>
      <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Choose a new password</h2>
      <p class="mt-1 text-sm" style="color: var(--color-text-muted)">
        Enter the 6-digit code we sent by SMS for <span class="font-medium" style="color: var(--color-text)">{{ identifier }}</span>.
        <button type="button" class="ml-1 font-semibold text-primary" @click="step = 'request'; error = ''">Change</button>
      </p>

      <form class="mt-7 space-y-5" @submit.prevent="submitReset">
        <FormAlert v-if="error">{{ error }}</FormAlert>
        <FormAlert v-else-if="notice" tone="info">{{ notice }}</FormAlert>

        <div>
          <label for="reset-code" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Reset code</label>
          <input
            id="reset-code"
            v-model="code"
            type="text"
            inputmode="numeric"
            autocomplete="one-time-code"
            maxlength="6"
            required
            placeholder="000000"
            class="w-full rounded-lg border bg-transparent px-3 py-3 text-center text-lg font-semibold tracking-[0.5em] transition-colors focus:border-primary focus:outline-none"
            style="border-color: var(--color-border); color: var(--color-text)"
          />
          <p v-if="fieldErrors.code" class="mt-1 text-xs text-red-500">{{ fieldErrors.code }}</p>
        </div>

        <div>
          <label for="reset-new" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">New password</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <Lock :size="16" style="color: var(--color-text-muted)" />
            <input
              id="reset-new"
              v-model="newPassword"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="new-password"
              required
              placeholder="Enter a new password"
              class="w-full bg-transparent py-2.5 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
            <button type="button" class="px-3" style="color: var(--color-text-muted)" @click="showPassword = !showPassword">
              <EyeOff v-if="showPassword" :size="17" />
              <Eye v-else :size="17" />
            </button>
          </div>
          <p v-if="fieldErrors.newPassword" class="mt-1 text-xs text-red-500">{{ fieldErrors.newPassword }}</p>
          <p v-else class="mt-1 text-xs" style="color: var(--color-text-muted)">
            At least 8 characters, with an uppercase letter, a lowercase letter, a number and a special character.
          </p>
        </div>

        <div>
          <label for="reset-confirm" class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Repeat new password</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <Lock :size="16" style="color: var(--color-text-muted)" />
            <input
              id="reset-confirm"
              v-model="confirmPassword"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="new-password"
              required
              placeholder="Repeat the new password"
              class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
          </div>
          <p v-if="fieldErrors.confirmPassword" class="mt-1 text-xs text-red-500">{{ fieldErrors.confirmPassword }}</p>
        </div>

        <button type="submit" class="btn-primary w-full disabled:opacity-70" :disabled="submitting">
          {{ submitting ? 'Saving…' : 'Reset password' }}
        </button>
      </form>

      <button
        type="button"
        class="mt-4 w-full text-center text-sm font-medium transition-colors"
        :style="resendCooldown > 0 ? 'color: var(--color-text-muted)' : 'color: #FD5C20'"
        :disabled="resendCooldown > 0 || submitting"
        @click="resend"
      >
        {{ resendLabel }}
      </button>
    </div>

    <!-- Step 3: done -->
    <div v-else>
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary text-white">
        <ShieldCheck :size="20" />
      </div>
      <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Password changed</h2>
      <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
        Your new password is set, and any device that was signed in to your account has been signed out.
      </p>
      <RouterLink to="/login" class="btn-primary mt-6 inline-flex">Log in</RouterLink>
    </div>

    <p v-if="step !== 'done'" class="mt-6 text-center text-sm" style="color: var(--color-text-muted)">
      Remembered it? <RouterLink to="/login" class="font-semibold text-primary">Back to login</RouterLink>
    </p>
  </AuthSplitLayout>
</template>
