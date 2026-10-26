<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Eye, EyeOff, Lock } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'

const router = useRouter()

const phone = ref('')
const password = ref('')
const showPassword = ref(false)

function handleSubmit() {
  // No backend yet, so this just simulates a successful login and hands off
  // to the job-seeker dashboard. Per the TO-BE flow, verification (OTP)
  // happens once at registration, not again on every login.
  router.push('/dashboard')
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
    <p class="mt-1 text-sm" style="color: var(--color-text-muted)">Use your Halotel phone number and password to log in.</p>

    <form class="mt-7 space-y-6" @submit.prevent="handleSubmit">
      <div>
        <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Halotel number</label>
        <div class="flex overflow-hidden rounded-lg border transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
          <span class="flex items-center gap-1 border-r px-3 text-sm font-medium" style="border-color: var(--color-border); color: var(--color-text)">+255</span>
          <input
            v-model="phone"
            type="tel"
            required
            placeholder="7XX XXX XXX"
            class="w-full bg-transparent px-3 py-2.5 text-sm focus:outline-none"
            style="color: var(--color-text)"
          />
        </div>
      </div>

      <div>
        <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Password</label>
        <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
          <Lock :size="16" style="color: var(--color-text-muted)" />
          <input
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
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

      <button type="submit" class="btn-primary w-full">Login</button>
    </form>

    <p class="mt-6 text-center text-sm" style="color: var(--color-text-muted)">
      Don't have an account? <RouterLink to="/register" class="font-semibold text-primary">Register here</RouterLink>
    </p>
  </AuthSplitLayout>
</template>
