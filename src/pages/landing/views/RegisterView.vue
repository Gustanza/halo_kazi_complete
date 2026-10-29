<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Eye, EyeOff, Mail, Lock, User, Search, Building2, Check } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'
import FormAlert from '../../../components/common/FormAlert.vue'
import { register, resendOtp } from '../../../api/authApi'

const router = useRouter()

const fullName = ref('')
const phone = ref('')
const email = ref('')
const password = ref('')
const repeatPassword = ref('')
const showPassword = ref(false)
const role = ref('job_seeker') // 'job_seeker' | 'employer'

const roleOptions = [
  { value: 'job_seeker', icon: Search, label: 'Job Seeker', description: "I'm looking for a job" },
  { value: 'employer', icon: Building2, label: 'Employer', description: "I'm looking to hire talent" },
]

const isEmployer = computed(() => role.value === 'employer')
const nameLabel = computed(() => (isEmployer.value ? 'Company name' : 'Full name'))
const namePlaceholder = computed(() => (isEmployer.value ? 'Enter your company name' : 'Enter your full name'))
const emailLabel = computed(() => (isEmployer.value ? 'Company email' : 'Email'))

const submitting = ref(false)
const error = ref('')
const fieldErrors = ref({}) // backend messages per field: { phone: '...', password: '...' }
const pendingPhone = ref('') // the number is registered but was never verified

function selectRole(value) {
  role.value = value
}

function goToVerify(normalizedPhone, resendIn) {
  router.push({
    name: 'verify-otp',
    query: { phone: normalizedPhone, role: role.value, name: fullName.value, resend: resendIn },
  })
}

async function handleSubmit() {
  error.value = ''
  pendingPhone.value = ''
  fieldErrors.value = {}
  if (password.value !== repeatPassword.value) {
    fieldErrors.value = { repeatPassword: 'Passwords do not match.' }
    return
  }

  submitting.value = true
  try {
    // Every new account must pass SMS (OTP) verification before it can log in (AUTH-02).
    // Job seekers register with phone number alone; email is only collected for employers.
    const result = await register({
      role: role.value,
      fullName: fullName.value,
      phone: phone.value,
      email: isEmployer.value ? email.value : '',
      password: password.value,
      repeatPassword: repeatPassword.value,
    })
    goToVerify(result.phone, result.resendAvailableInSeconds)
  } catch (e) {
    if (e.code === 'SMS_DELIVERY_FAILED') {
      // The account exists but the SMS didn't go out: continue on the code screen, where "Resend" works.
      goToVerify(phone.value, 0)
      return
    }
    fieldErrors.value = e.fieldMap?.() ?? {}
    if (e.code === 'PHONE_PENDING_VERIFICATION') pendingPhone.value = e.details.phone
    error.value = e.code === 'VALIDATION_FAILED' ? 'Please correct the highlighted fields.' : e.message
  } finally {
    submitting.value = false
  }
}

/** The account exists but was never verified: send a fresh code and continue there. */
async function verifyPending() {
  let wait
  try {
    wait = (await resendOtp(pendingPhone.value)).resendAvailableInSeconds
  } catch (e) {
    wait = e.retryAfterSeconds ?? undefined
  }
  goToVerify(pendingPhone.value, wait)
}
</script>

<template>
  <AuthSplitLayout
    heading-prefix="Register with"
    heading-highlight="Halokazi"
    subtitle="Create your account and start your job journey with new opportunity"
  >
    <h2 class="text-xl font-bold" style="color: var(--color-text)">Register new account</h2>
    <p class="mt-1 text-sm" style="color: var(--color-text-muted)">Use your Halotel number to register with Halokazi.</p>

    <form class="mt-7 space-y-6" @submit.prevent="handleSubmit">
      <FormAlert v-if="error">
        {{ error }}
        <button v-if="pendingPhone" type="button" class="ml-1 font-semibold underline" @click="verifyPending">
          Verify this number
        </button>
      </FormAlert>

      <div>
        <label class="mb-2 block text-xs font-medium" style="color: var(--color-text-muted)">Register as</label>
        <div class="grid grid-cols-2 gap-3">
          <button
            v-for="option in roleOptions"
            :key="option.value"
            type="button"
            class="relative flex flex-col items-start gap-2.5 rounded-xl border-2 p-4 text-left active:scale-[0.98]"
            style="transition: border-color 180ms var(--ease-out), background-color 180ms var(--ease-out), transform 140ms var(--ease-out)"
            :style="
              role === option.value
                ? 'border-color:#FD5C20; background-color: var(--color-peach-light)'
                : 'border-color: var(--color-border)'
            "
            @click="selectRole(option.value)"
          >
            <Transition name="pop">
              <span
                v-if="role === option.value"
                class="absolute right-2.5 top-2.5 flex h-5 w-5 items-center justify-center rounded-full bg-primary text-white"
              >
                <Check :size="12" :stroke-width="3" />
              </span>
            </Transition>
            <span
              class="flex h-9 w-9 items-center justify-center rounded-full transition-colors duration-200"
              :style="role === option.value ? 'background-color:#FD5C20; color:#fff' : 'background-color: var(--color-bg); color: var(--color-text-muted)'"
            >
              <component :is="option.icon" :size="17" />
            </span>
            <span>
              <span class="block text-sm font-semibold" style="color: var(--color-text)">{{ option.label }}</span>
              <span class="block text-xs" style="color: var(--color-text-muted)">{{ option.description }}</span>
            </span>
          </button>
        </div>
        <p v-if="fieldErrors.role" class="mt-1 text-xs text-red-500">{{ fieldErrors.role }}</p>
      </div>

      <div class="space-y-5">
        <div>
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">{{ nameLabel }}</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <User :size="16" style="color: var(--color-text-muted)" />
            <input
              v-model="fullName"
              type="text"
              required
              :placeholder="namePlaceholder"
              class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
          </div>
          <p v-if="fieldErrors.fullName" class="mt-1 text-xs text-red-500">{{ fieldErrors.fullName }}</p>
        </div>

        <div>
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
          <p v-if="fieldErrors.phone" class="mt-1 text-xs text-red-500">{{ fieldErrors.phone }}</p>
        </div>

        <div v-if="isEmployer">
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">{{ emailLabel }}</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <Mail :size="16" style="color: var(--color-text-muted)" />
            <input
              v-model="email"
              type="email"
              required
              placeholder="Enter your company email"
              class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
          </div>
          <p v-if="fieldErrors.email" class="mt-1 text-xs text-red-500">{{ fieldErrors.email }}</p>
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
          <p v-if="fieldErrors.password" class="mt-1 text-xs text-red-500">{{ fieldErrors.password }}</p>
          <p v-else class="mt-1 text-xs" style="color: var(--color-text-muted)">
            At least 8 characters, with an uppercase letter, a lowercase letter, a number and a special character.
          </p>
        </div>

        <div>
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Repeat password</label>
          <div class="flex items-center gap-2 rounded-lg border pl-3 transition-colors focus-within:border-primary" style="border-color: var(--color-border)">
            <Lock :size="16" style="color: var(--color-text-muted)" />
            <input
              v-model="repeatPassword"
              :type="showPassword ? 'text' : 'password'"
              required
              placeholder="Repeat your password"
              class="w-full bg-transparent py-2.5 pr-3 text-sm focus:outline-none"
              style="color: var(--color-text)"
            />
          </div>
          <p v-if="fieldErrors.repeatPassword" class="mt-1 text-xs text-red-500">{{ fieldErrors.repeatPassword }}</p>
        </div>
      </div>

      <button type="submit" class="btn-primary w-full disabled:opacity-70" :disabled="submitting">
        {{ submitting ? 'Creating account…' : 'Create Account' }}
      </button>
    </form>

    <p class="mt-6 text-center text-sm" style="color: var(--color-text-muted)">
      Already have an account? <RouterLink to="/login" class="font-semibold text-primary">Login here</RouterLink>
    </p>
  </AuthSplitLayout>
</template>

<style scoped>
.pop-enter-active {
  transition: transform 220ms var(--ease-out), opacity 150ms ease;
}
.pop-enter-from {
  opacity: 0;
  transform: scale(0.5);
}
.pop-leave-active {
  transition: opacity 120ms ease;
}
.pop-leave-to {
  opacity: 0;
}
</style>
