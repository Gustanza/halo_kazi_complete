<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ShieldCheck, MessageSquareText } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import { useAuthStore } from '../../../stores/auth'
import { ApiRequestError } from '../../../api/http'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const phone = String(route.query.phone || '')
const role = String(route.query.role || 'job_seeker')
const name = String(route.query.name || '')

const code = ref('')
const verified = ref(false)
const resendCooldown = ref(Number(route.query.resendAvailableInSeconds) || 30)
const shake = ref(false)
const submitting = ref(false)
const resending = ref(false)
const formError = ref('')
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

const maskedPhone = computed(() => {
  if (phone.length < 4) return phone
  return `+255 ${phone.slice(0, -4).replace(/./g, '•')}${phone.slice(-4)}`
})

async function resend() {
  if (resendCooldown.value > 0 || resending.value) return
  resending.value = true
  formError.value = ''
  try {
    const result = await auth.resendOtp({ phone })
    resendCooldown.value = result.resendAvailableInSeconds
  } catch (error) {
    formError.value = error instanceof ApiRequestError ? error.message : 'Could not resend the code.'
  } finally {
    resending.value = false
  }
}

async function handleVerify() {
  formError.value = ''
  if (!code.value.trim()) {
    shake.value = true
    window.setTimeout(() => (shake.value = false), 400)
    return
  }
  submitting.value = true
  try {
    const result = await auth.verifyOtp({ phone, code: code.value.trim() })
    if (result.user.role === 'EMPLOYER') {
      // Employer accounts also need Admin verification (TIN/licence review) before the
      // employer portal opens, so show the pending-review state instead of a dashboard.
      verified.value = true
    } else {
      router.push('/dashboard')
    }
  } catch (error) {
    shake.value = true
    window.setTimeout(() => (shake.value = false), 400)
    formError.value = error instanceof ApiRequestError ? error.message : 'Verification failed. Please try again.'
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

          <p v-if="formError" class="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600 dark:bg-red-500/10">{{ formError }}</p>

          <button type="submit" class="btn-primary w-full" :disabled="submitting">
            {{ submitting ? 'Verifying…' : 'Verify & Continue' }}
          </button>
        </form>

        <button
          type="button"
          class="mt-4 w-full text-center text-sm font-medium transition-colors"
          :style="resendCooldown > 0 || resending ? 'color: var(--color-text-muted)' : 'color: #FD5C20'"
          :disabled="resendCooldown > 0 || resending"
          @click="resend"
        >
          {{ resendCooldown > 0 ? `Resend code in ${resendCooldown}s` : resending ? 'Resending…' : 'Resend code' }}
        </button>
      </div>

      <div v-else key="done">
        <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary text-white">
          <ShieldCheck :size="20" />
        </div>
        <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Account verified{{ name ? `, ${name}` : '' }}!</h2>
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
