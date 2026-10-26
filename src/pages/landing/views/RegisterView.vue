<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Eye, EyeOff, Mail, Lock, User, Search, Building2, Check } from 'lucide-vue-next'
import AuthSplitLayout from '../components/auth/AuthSplitLayout.vue'

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

function selectRole(value) {
  role.value = value
}

function handleSubmit() {
  // No backend yet. Per the documented TO-BE flow, every new account must pass
  // System (OTP/SMS) verification before it can log in - so hand off to that step
  // instead of logging the user straight in.
  router.push({
    path: '/verify-otp',
    query: { phone: phone.value, role: role.value, name: fullName.value },
  })
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
        </div>

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

        <div v-if="isEmployer">
          <label class="mb-1.5 block text-xs font-medium" style="color: var(--color-text-muted)">Company email</label>
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
        </div>
      </div>

      <button type="submit" class="btn-primary w-full">Create Account</button>
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
