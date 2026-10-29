<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Eye, EyeOff, Lock, Mail } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import { useAuthStore } from '../../../stores/auth'
import { ApiRequestError } from '../../../api/http'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

// Job seekers log in with phone number; employers log in with email.
const loginMethod = ref('phone')
const phone = ref('')
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const submitting = ref(false)
const formError = ref('')

function selectMethod(method) {
  loginMethod.value = method
  formError.value = ''
}

async function handleSubmit() {
  formError.value = ''
  submitting.value = true
  try {
    const identifier = loginMethod.value === 'phone' ? phone.value : email.value
    const result = await auth.login({ identifier, password: password.value })
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : null
    if (result.user.role === 'EMPLOYER') {
      router.push(redirect || '/employer')
    } else {
      router.push(redirect || '/dashboard')
    }
  } catch (error) {
    formError.value = error instanceof ApiRequestError
      ? error.message
      : 'Something went wrong. Please try again.'
  } finally {
    submitting.value = false
  }
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
    <p class="mt-1 text-sm" style="color: var(--color-text-muted)">Job seekers log in with their Halotel number; employers log in with email.</p>

    <div class="mt-5 grid grid-cols-2 gap-1 rounded-lg p-1" style="background-color: var(--color-bg)">
      <button
        type="button"
        class="rounded-md py-2 text-sm font-semibold transition-colors"
        :style="loginMethod === 'phone' ? 'background-color: var(--color-surface); color: var(--color-text); box-shadow: 0 1px 2px rgba(0,0,0,0.08)' : 'color: var(--color-text-muted)'"
        @click="selectMethod('phone')"
      >
        Login with phone number
      </button>
      <button
        type="button"
        class="rounded-md py-2 text-sm font-semibold transition-colors"
        :style="loginMethod === 'email' ? 'background-color: var(--color-surface); color: var(--color-text); box-shadow: 0 1px 2px rgba(0,0,0,0.08)' : 'color: var(--color-text-muted)'"
        @click="selectMethod('email')"
      >
        Login with email
      </button>
    </div>

    <form class="mt-5 space-y-6" @submit.prevent="handleSubmit">
      <div v-if="loginMethod === 'phone'">
        <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Halotel number</label>
        <div class="flex overflow-hidden rounded-lg border transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
          <span class="flex items-center gap-1 border-r px-3 text-sm font-medium" style="border-color: var(--color-border); color: var(--color-text)">+255</span>
          <input
            v-model="phone"
            type="tel"
            inputmode="numeric"
            autocomplete="tel-national"
            required
            maxlength="9"
            placeholder="7XX XXX XXX"
            class="w-full bg-transparent px-3 py-2.5 text-sm focus:outline-none"
            style="color: var(--color-text)"
            @input="phone = phone.replace(/\D/g, '').slice(0, 9)"
          />
        </div>
      </div>

      <div v-else>
        <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Email address</label>
        <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
          <Mail :size="16" style="color: var(--color-text-muted)" />
          <input
            v-model="email"
            type="email"
            required
            autocomplete="email"
            placeholder="Enter your company email"
            class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
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

      <p v-if="formError" class="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600 dark:bg-red-500/10">{{ formError }}</p>

      <button type="submit" class="btn-primary w-full" :disabled="submitting">
        {{ submitting ? 'Logging in…' : 'Login' }}
      </button>
    </form>

    <p class="mt-3 text-center text-sm">
      <RouterLink to="/forgot-password" class="font-semibold text-primary">Forgot your password?</RouterLink>
    </p>

    <p class="mt-3 text-center text-sm" style="color: var(--color-text-muted)">
      Don't have an account? <RouterLink to="/register" class="font-semibold text-primary">Register here</RouterLink>
    </p>
  </AuthSplitLayout>
</template>
