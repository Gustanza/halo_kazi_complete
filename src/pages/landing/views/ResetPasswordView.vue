<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Eye, EyeOff, Lock, ShieldCheck } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import { useAuthStore } from '../../../stores/auth'
import { ApiRequestError } from '../../../api/http'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const identifier = ref(String(route.query.identifier || ''))
const code = ref('')
const password = ref('')
const repeatPassword = ref('')
const showPassword = ref(false)
const submitting = ref(false)
const formError = ref('')
const done = ref(false)

onMounted(() => {
  if (!identifier.value) router.replace('/forgot-password')
})

async function handleSubmit() {
  formError.value = ''
  submitting.value = true
  try {
    await auth.resetPassword({
      identifier: identifier.value,
      code: code.value.trim(),
      password: password.value,
      repeatPassword: repeatPassword.value,
    })
    done.value = true
  } catch (error) {
    formError.value = error instanceof ApiRequestError ? error.message : 'Something went wrong. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <AuthSplitLayout
    heading-prefix="Set a new"
    heading-highlight="password"
    subtitle="Enter the code we sent you and choose a new password for your account"
    :show-illustration="false"
  >
    <template v-if="!done">
      <h2 class="text-xl font-bold" style="color: var(--color-text)">Reset password</h2>
      <p class="mt-1 text-sm" style="color: var(--color-text-muted)">For {{ identifier }}</p>

      <form class="mt-7 space-y-5" @submit.prevent="handleSubmit">
        <div>
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Reset code</label>
          <input
            v-model="code"
            type="text"
            inputmode="numeric"
            maxlength="6"
            required
            placeholder="000000"
            class="w-full rounded-lg border bg-transparent px-3 py-3 text-center text-lg font-semibold tracking-[0.5em] focus:border-primary focus:outline-none"
            style="border-color: var(--color-border); color: var(--color-text)"
          />
        </div>

        <div>
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">New password</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <Lock :size="16" style="color: var(--color-text-muted)" />
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              required
              placeholder="Enter your new password"
              class="w-full bg-transparent py-2.5 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
            <button type="button" class="px-3" style="color: var(--color-text-muted)" @click="showPassword = !showPassword">
              <EyeOff v-if="showPassword" :size="17" />
              <Eye v-else :size="17" />
            </button>
          </div>
        </div>

        <div>
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Repeat password</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <Lock :size="16" style="color: var(--color-text-muted)" />
            <input
              v-model="repeatPassword"
              :type="showPassword ? 'text' : 'password'"
              required
              placeholder="Repeat your new password"
              class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
          </div>
        </div>

        <p v-if="formError" class="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600 dark:bg-red-500/10">{{ formError }}</p>

        <button type="submit" class="btn-primary w-full" :disabled="submitting">
          {{ submitting ? 'Saving…' : 'Reset password' }}
        </button>
      </form>
    </template>

    <template v-else>
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary text-white">
        <ShieldCheck :size="20" />
      </div>
      <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Password reset</h2>
      <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
        Your password has been changed. You can now log in with your new password.
      </p>
      <RouterLink to="/login" class="btn-primary mt-6 inline-flex">Back to login</RouterLink>
    </template>
  </AuthSplitLayout>
</template>
