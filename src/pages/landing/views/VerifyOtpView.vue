<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ShieldCheck, MessageSquareText } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import FormAlert from '../../../components/common/FormAlert.vue'
import { resendOtp, verifyOtp } from '../../../api/authApi'
import { useSession } from '../../../auth/session'
import { formatWait, maskPhone } from '../../../auth/formatting'

const route = useRoute()
const router = useRouter()
const session = useSession()

// Set by Register/Login: phone is the normalised +255 number, resend the seconds until "Resend" unlocks.
const phone = String(route.query.phone || '')
const name = String(route.query.name || '')
const initialCooldown = Number.parseInt(route.query.resend, 10)

const code = ref('')
const verified = ref(false)
const resendCooldown = ref(Number.isFinite(initialCooldown) ? initialCooldown : 30)
const shake = ref(false)
const submitting = ref(false)
const error = ref('')
const notice = ref('')
let timer = null

onMounted(() => {
  // No phone to verify means this screen was opened directly - send the person
  // back to register instead of showing a dead-end form.
  if (!phone) {
    router.replace('/register')
    return
  }
  timer = window.setInterval(() => {
    if (resendCooldown.value > 0) resendCooldown.value -= 1
  }, 1000)
})

onUnmounted(() => {
  if (timer) window.clearInterval(timer)
})

const maskedPhone = computed(() => maskPhone(phone))
const resendLabel = computed(() =>
  resendCooldown.value > 0 ? `Resend code in ${formatWait(resendCooldown.value)}` : 'Resend code',
)

function shakeInput() {
  shake.value = true
  window.setTimeout(() => (shake.value = false), 400)
}

async function resend() {
  if (resendCooldown.value > 0) return
  error.value = ''
  notice.value = ''
  try {
    const result = await resendOtp(phone)
    resendCooldown.value = result.resendAvailableInSeconds
    code.value = ''
    notice.value = 'A new code is on its way. The previous code no longer works.'
  } catch (e) {
    if (e.retryAfterSeconds) resendCooldown.value = e.retryAfterSeconds
    error.value = e.message
  }
}

async function handleVerify() {
  error.value = ''
  notice.value = ''
  if (!/^\d{6}$/.test(code.value.trim())) {
    error.value = 'Enter the 6-digit code from the SMS.'
    shakeInput()
    return
  }

  submitting.value = true
  try {
    // A verified phone activates the account and signs the user in (AUTH-02).
    const auth = await verifyOtp(phone, code.value.trim())
    session.start(auth)
    if (auth.user.role === 'EMPLOYER') {
      verified.value = true // employers continue with business verification, not straight to a dashboard
    } else {
      router.replace(session.homeFor(auth.user))
    }
  } catch (e) {
    shakeInput()
    if (e.code === 'OTP_INVALID') {
      const left = e.details.remainingAttempts
      error.value = left ? `Incorrect code. ${left} attempt${left === 1 ? '' : 's'} left.` : e.message
    } else {
      error.value = e.message // OTP_EXPIRED / OTP_ATTEMPTS_EXCEEDED already say "request a new code"
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <AuthSplitLayout
    heading-prefix="Verify your"
    heading-highlight="phone number"
    subtitle="We use a one-time code to confirm it's really you before your Halokazi account goes live"
  >
    <Transition name="fade-swap" mode="out-in">
      <div v-if="!verified" key="form">
        <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
          <MessageSquareText :size="20" />
        </div>
        <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Enter verification code</h2>
        <p class="mt-1 text-sm" style="color: var(--color-text-muted)">
          We've sent a 6-digit code via SMS to <span class="font-medium" style="color: var(--color-text)">{{ maskedPhone }}</span>.
        </p>

        <form class="mt-7 space-y-6" @submit.prevent="handleVerify">
          <FormAlert v-if="error">{{ error }}</FormAlert>
          <FormAlert v-else-if="notice" tone="info">{{ notice }}</FormAlert>

          <div>
            <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Verification code</label>
            <input
              v-model="code"
              type="text"
              inputmode="numeric"
              maxlength="6"
              required
              placeholder="000000"
              class="w-full rounded-lg border bg-transparent px-3 py-3 text-center text-lg font-semibold tracking-[0.5em] transition-colors focus:border-primary focus:outline-none"
              :class="shake ? 'animate-shake' : ''"
              style="border-color: var(--color-border); color: var(--color-text)"
            />
          </div>

          <button type="submit" class="btn-primary w-full disabled:opacity-70" :disabled="submitting">
            {{ submitting ? 'Verifying…' : 'Verify & Continue' }}
          </button>
        </form>

        <button
          type="button"
          class="mt-4 w-full text-center text-sm font-medium transition-colors"
          :style="resendCooldown > 0 ? 'color: var(--color-text-muted)' : 'color: #FD5C20'"
          :disabled="resendCooldown > 0"
          @click="resend"
        >
          {{ resendLabel }}
        </button>
      </div>

      <div v-else key="done">
        <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary text-white">
          <ShieldCheck :size="20" />
        </div>
        <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">
          Account verified{{ (session.user.value?.fullName ?? name) ? `, ${session.user.value?.fullName ?? name}` : '' }}!
        </h2>
        <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
          Your employer account has passed automatic verification. The next step in the documented process is attaching
          your TIN and Business Licence for Admin review — that part of the employer portal isn't built yet, so we'll
          email you the moment it opens.
        </p>
        <RouterLink to="/" class="btn-primary mt-6 inline-flex">Back to Home</RouterLink>
      </div>
    </Transition>
  </AuthSplitLayout>
</template>

<style scoped>
.fade-swap-enter-active {
  transition: opacity 200ms var(--ease-out), transform 200ms var(--ease-out);
}
.fade-swap-leave-active {
  transition: opacity 120ms ease;
}
.fade-swap-enter-from {
  opacity: 0;
  transform: translateY(6px);
}
.fade-swap-leave-to {
  opacity: 0;
}

@keyframes shake {
  10%,
  90% {
    transform: translateX(-2px);
  }
  20%,
  80% {
    transform: translateX(4px);
  }
  30%,
  50%,
  70% {
    transform: translateX(-6px);
  }
  40%,
  60% {
    transform: translateX(6px);
  }
}
.animate-shake {
  animation: shake 400ms ease;
  border-color: #ef4444 !important;
}
</style>
