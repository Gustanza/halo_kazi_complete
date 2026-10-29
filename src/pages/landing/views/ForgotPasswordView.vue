<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { KeyRound } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import { useAuthStore } from '../../../stores/auth'
import { ApiRequestError } from '../../../api/http'

const router = useRouter()
const auth = useAuthStore()

const identifier = ref('')
const submitting = ref(false)
const formError = ref('')
const sent = ref(false)

async function handleSubmit() {
  formError.value = ''
  submitting.value = true
  try {
    await auth.forgotPassword({ identifier: identifier.value })
    sent.value = true
  } catch (error) {
    formError.value = error instanceof ApiRequestError ? error.message : 'Something went wrong. Please try again.'
  } finally {
    submitting.value = false
  }
}

function continueToReset() {
  router.push({ path: '/reset-password', query: { identifier: identifier.value } })
}
</script>

<template>
  <AuthSplitLayout
    heading-prefix="Reset your"
    heading-highlight="password"
    subtitle="Enter the phone number or email on your account and we'll send you a code to reset your password"
    :show-illustration="false"
  >
    <template v-if="!sent">
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
        <KeyRound :size="20" />
      </div>
      <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Forgot password</h2>
      <p class="mt-1 text-sm" style="color: var(--color-text-muted)">
        We'll send a reset code to your phone number or email.
      </p>

      <form class="mt-7 space-y-6" @submit.prevent="handleSubmit">
        <div>
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Phone number or email</label>
          <input
            v-model="identifier"
            type="text"
            required
            placeholder="7XX XXX XXX or you@example.com"
            class="w-full rounded-lg border bg-transparent px-3 py-2.5 text-sm focus:border-primary focus:outline-none"
            style="border-color: var(--color-border); color: var(--color-text)"
          />
        </div>

        <p v-if="formError" class="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600 dark:bg-red-500/10">{{ formError }}</p>

        <button type="submit" class="btn-primary w-full" :disabled="submitting">
          {{ submitting ? 'Sending…' : 'Send reset code' }}
        </button>
      </form>

      <p class="mt-6 text-center text-sm" style="color: var(--color-text-muted)">
        Remembered it? <RouterLink to="/login" class="font-semibold text-primary">Back to login</RouterLink>
      </p>
    </template>

    <template v-else>
      <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary text-white">
        <KeyRound :size="20" />
      </div>
      <h2 class="mt-4 text-xl font-bold" style="color: var(--color-text)">Check your messages</h2>
      <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
        If an account exists for <span class="font-medium" style="color: var(--color-text)">{{ identifier }}</span>,
        a reset code is on its way. Enter it on the next screen along with your new password.
      </p>
      <button type="button" class="btn-primary mt-6" @click="continueToReset">Enter reset code</button>
    </template>
  </AuthSplitLayout>
</template>
