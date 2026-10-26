<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ShieldCheck, MessageSquareText } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'

const route = useRoute()
const router = useRouter()

const phone = String(route.query.phone || '')
const role = String(route.query.role || 'job_seeker')
const name = String(route.query.name || '')

const code = ref('')
const verified = ref(false)
const resendCooldown = ref(30)
const shake = ref(false)
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

function resend() {
  if (resendCooldown.value > 0) return
  resendCooldown.value = 30
}

function handleVerify() {
  // No SMS backend yet - any non-empty code "verifies" the account here, matching
  // the TO-BE flow's requirement that an account must pass System verification
  // before it can log in, while the real OTP gateway is not yet wired up.
  if (!code.value.trim()) {
    shake.value = true
    window.setTimeout(() => (shake.value = false), 400)
    return
  }
  if (role === 'employer') {
    verified.value = true
    return
  }
  router.push('/dashboard')
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

          <button type="submit" class="btn-primary w-full">Verify &amp; Continue</button>
        </form>

        <button
          type="button"
          class="mt-4 w-full text-center text-sm font-medium transition-colors"
          :style="resendCooldown > 0 ? 'color: var(--color-text-muted)' : 'color: #FD5C20'"
          :disabled="resendCooldown > 0"
          @click="resend"
        >
          {{ resendCooldown > 0 ? `Resend code in ${resendCooldown}s` : 'Resend code' }}
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
