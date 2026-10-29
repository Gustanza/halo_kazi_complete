<script setup>
import { computed, ref } from 'vue'
import {
  UserRound, LockKeyhole, Bell, ShieldCheck, Globe, Palette,
  CircleHelp, Camera, Save, MapPin, Settings as SettingsIcon,
  CheckCircle2, Circle, FileText, Bookmark, BriefcaseBusiness,
  Headset, ChevronRight, Eye, EyeOff
} from 'lucide-vue-next'

import DashboardLayout from '../components/layout/DashboardLayout.vue'

const activeSection = ref('account')
const savedMessage = ref('')
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const photoPreview = ref('')

const account = ref({
  fullName: 'Raymond Mwangi',
  email: 'mwangiraymond@gmail.com',
  phone: '+255 624023920',
  dateOfBirth: '2001-01-12',
  gender: 'Male',
  location: 'Dar es Salaam, Tanzania',
  about: 'Results-driven marketing professional with 5+ years of experience in digital marketing, brand strategy and social media management.'
})

const password = ref({
  current: '',
  next: '',
  confirm: ''
})

const notifications = ref({
  email: true,
  applications: true,
  jobAlerts: true,
  newsletter: false
})

const privacy = ref({
  profileVisible: true,
  showEmail: false,
  showPhone: false
})

const preferences = ref({
  language: 'English',
  region: 'Tanzania',
  appearance: 'Light'
})

const sections = [
  { id: 'account', label: 'Account Information', icon: UserRound },
  { id: 'password', label: 'Change Password', icon: LockKeyhole },
  { id: 'notifications', label: 'Notifications', icon: Bell },
  { id: 'privacy', label: 'Privacy & Security', icon: ShieldCheck },
  { id: 'language', label: 'Language & Region', icon: Globe },
  { id: 'appearance', label: 'Appearance', icon: Palette },
  { id: 'help', label: 'Help & Support', icon: CircleHelp }
]

const notificationItems = [
  { key: 'email', title: 'Email notifications', desc: 'Receive important account updates by email.' },
  { key: 'applications', title: 'Application updates', desc: 'Get updates about your job applications.' },
  { key: 'jobAlerts', title: 'Job alerts', desc: 'Receive alerts for jobs matching your preferences.' },
  { key: 'newsletter', title: 'Career tips and newsletter', desc: 'Receive career advice and platform news.' }
]

const privacyItems = [
  { key: 'profileVisible', title: 'Profile visibility', desc: 'Allow employers to discover your profile.' },
  { key: 'showEmail', title: 'Display email address', desc: 'Allow employers to see your email on your profile.' },
  { key: 'showPhone', title: 'Display phone number', desc: 'Allow employers to see your phone number.' }
]

const sectionTitle = computed(() =>
  sections.find(item => item.id === activeSection.value)?.label || 'Settings'
)

const aboutCount = computed(() => account.value.about.length)

function saveChanges() {
  if (activeSection.value === 'password') {
    if (password.value.next.length < 8) {
      savedMessage.value = 'Password must contain at least 8 characters.'
      return
    }

    if (password.value.next !== password.value.confirm) {
      savedMessage.value = 'Passwords do not match.'
      return
    }

    savedMessage.value = 'Password form validated. Connect your API to update it.'
  } else {
    savedMessage.value = 'Changes are ready. Connect your API to save them permanently.'
  }
}

function uploadPhoto(event) {
  const file = event.target.files?.[0]
  if (!file) return

  if (!['image/jpeg', 'image/png'].includes(file.type)) {
    savedMessage.value = 'Please select a JPG or PNG image.'
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    savedMessage.value = 'Image must be smaller than 2MB.'
    return
  }

  if (photoPreview.value) URL.revokeObjectURL(photoPreview.value)
  photoPreview.value = URL.createObjectURL(file)
  savedMessage.value = 'Photo preview updated.'
}

function openQuickLink(link) {
  // Badilisha route hizi kulingana na router yako.
  if (link.route) {
    window.location.href = link.route
  }
}
</script>

<template>
  <DashboardLayout>
    <div class="min-w-0 space-y-6 pb-8">

      <!-- Page heading -->
      <header class="flex items-center gap-4">
        <div class="flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl bg-orange-50 text-orange-600">
          <SettingsIcon :size="30" />
        </div>

        <div class="min-w-0">
          <h1 class="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
            Settings
          </h1>
          <p class="mt-1 text-sm text-slate-500">
            Manage your account, preferences and privacy.
          </p>
        </div>
      </header>

      <!-- Main page columns -->
      <div class="grid min-w-0 grid-cols-1 items-start gap-5 xl:grid-cols-[185px_minmax(0,1fr)]">

        <!-- Settings navigation -->
        <aside class="min-w-0 rounded-2xl border border-slate-200 bg-white p-3 shadow-sm">
          <p class="mb-3 px-3 pt-2 text-[10px] font-bold uppercase tracking-widest text-slate-400">
            Settings Menu
          </p>

          <button
            v-for="section in sections"
            :key="section.id"
            type="button"
            class="mb-1 flex w-full items-center gap-3 rounded-lg border-l-[3px] px-3 py-3 text-left text-xs font-medium transition last:mb-0"
            :class="activeSection === section.id
              ? 'border-orange-500 bg-orange-50 text-orange-600'
              : 'border-transparent text-slate-600 hover:bg-slate-50'"
            @click="activeSection = section.id; savedMessage = ''"
          >
            <component :is="section.icon" :size="17" class="shrink-0" />
            <span class="leading-5">{{ section.label }}</span>
          </button>
        </aside>

        <!-- Main settings content -->
        <main class="min-w-0 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">

          <!-- Section header -->
          <div class="mb-6 flex flex-wrap items-start justify-between gap-4 border-b border-slate-100 pb-5">
            <div class="flex min-w-0 items-start gap-3">
              <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-slate-50 text-slate-700">
                <component
                  :is="sections.find(item => item.id === activeSection)?.icon || SettingsIcon"
                  :size="21"
                />
              </div>

              <div class="min-w-0">
                <h2 class="text-base font-bold text-slate-900">
                  {{ sectionTitle }}
                </h2>
                <p class="mt-1 text-xs leading-5 text-slate-500">
                  <template v-if="activeSection === 'account'">
                    Update your personal details and contact information.
                  </template>
                  <template v-else-if="activeSection === 'password'">
                    Keep your account secure with a strong password.
                  </template>
                  <template v-else-if="activeSection === 'notifications'">
                    Choose the notifications you want to receive.
                  </template>
                  <template v-else-if="activeSection === 'privacy'">
                    Control your profile visibility and personal information.
                  </template>
                  <template v-else-if="activeSection === 'language'">
                    Set your preferred language and region.
                  </template>
                  <template v-else-if="activeSection === 'appearance'">
                    Customize the appearance of your portal.
                  </template>
                  <template v-else>
                    Get help with your account and applications.
                  </template>
                </p>
              </div>
            </div>

            <button
              v-if="['language', 'appearance'].includes(activeSection)"
              type="button"
              class="inline-flex shrink-0 items-center gap-2 rounded-lg bg-orange-600 px-4 py-2.5 text-xs font-semibold text-white transition hover:bg-orange-700"
              @click="saveChanges"
            >
              <Save :size="15" />
              Save Changes
            </button>
          </div>

          <!-- Feedback message -->
          <div
            v-if="savedMessage"
            class="mb-5 rounded-lg border border-orange-200 bg-orange-50 px-4 py-3 text-xs leading-5 text-orange-800"
            role="status"
          >
            {{ savedMessage }}
            <button class="ml-2 font-bold underline" @click="savedMessage = ''">
              Dismiss
            </button>
          </div>

          <!-- ACCOUNT INFORMATION -->
          <div v-if="activeSection === 'account'" class="space-y-6">

            <!-- Profile photo -->
            <div class="flex flex-wrap items-center gap-4">
              <div class="flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-full border-4 border-slate-50 bg-slate-100">
                <img
                  v-if="photoPreview"
                  :src="photoPreview"
                  alt="Profile"
                  class="h-full w-full object-cover"
                />
                <UserRound v-else :size="34" class="text-slate-400" />
              </div>

              <div class="min-w-0">
                <p class="text-sm font-semibold text-slate-800">Profile Photo</p>
                <p class="mt-1 text-xs text-slate-500">
                  Your account profile photo.
                </p>
              </div>
            </div>

            <!-- Personal details -->
            <div>
              <h3 class="mb-4 text-sm font-bold text-slate-800">
                Personal Information
              </h3>

              <div class="grid grid-cols-1 gap-x-4 gap-y-5 md:grid-cols-2">
                <div>
                  <p class="text-xs font-semibold text-slate-500">Full Name</p>
                  <p class="mt-2 break-words text-sm font-medium text-slate-800">
                    {{ account.fullName }}
                  </p>
                </div>

                <div>
                  <p class="text-xs font-semibold text-slate-500">Email Address</p>
                  <p class="mt-2 break-words text-sm font-medium text-slate-800">
                    {{ account.email }}
                  </p>
                </div>

                <div>
                  <p class="text-xs font-semibold text-slate-500">Phone Number</p>
                  <p class="mt-2 text-sm font-medium text-slate-800">
                    {{ account.phone }}
                  </p>
                </div>

                <div>
                  <p class="text-xs font-semibold text-slate-500">Date of Birth</p>
                  <p class="mt-2 text-sm font-medium text-slate-800">
                    {{ account.dateOfBirth }}
                  </p>
                </div>

                <div>
                  <p class="text-xs font-semibold text-slate-500">Gender</p>
                  <p class="mt-2 text-sm font-medium text-slate-800">
                    {{ account.gender }}
                  </p>
                </div>

                <div>
                  <p class="text-xs font-semibold text-slate-500">Location</p>
                  <p class="mt-2 break-words text-sm font-medium text-slate-800">
                    {{ account.location }}
                  </p>
                </div>
              </div>
            </div>

            <!-- About -->
            <div>
              <h3 class="text-sm font-bold text-slate-800">About Me</h3>
              <p class="mt-2 whitespace-pre-line text-sm leading-6 text-slate-600">
                {{ account.about }}
              </p>
              <p class="mt-1 text-right text-[11px] text-slate-400">
                {{ aboutCount }}/500
              </p>
            </div>
          </div>

          <!-- CHANGE PASSWORD -->
          <form
            v-else-if="activeSection === 'password'"
            class="w-full space-y-5"
            @submit.prevent="saveChanges"
          >
            <label class="block text-xs font-semibold text-slate-700">
              Current Password
              <span class="relative mt-2 block">
                <input
                  v-model="password.current"
                  required
                  :type="showCurrentPassword ? 'text' : 'password'"
                  autocomplete="current-password"
                  class="w-full rounded-lg border border-slate-200 px-3 py-3 pr-11 text-sm font-normal outline-none focus:border-orange-400 focus:ring-4 focus:ring-orange-50"
                />
                <button
                  type="button"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400"
                  @click="showCurrentPassword = !showCurrentPassword"
                >
                  <Eye v-if="!showCurrentPassword" :size="17" />
                  <EyeOff v-else :size="17" />
                </button>
              </span>
            </label>

            <label class="block text-xs font-semibold text-slate-700">
              New Password
              <span class="relative mt-2 block">
                <input
                  v-model="password.next"
                  required
                  minlength="8"
                  :type="showNewPassword ? 'text' : 'password'"
                  autocomplete="new-password"
                  class="w-full rounded-lg border border-slate-200 px-3 py-3 pr-11 text-sm font-normal outline-none focus:border-orange-400 focus:ring-4 focus:ring-orange-50"
                />
                <button
                  type="button"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400"
                  @click="showNewPassword = !showNewPassword"
                >
                  <Eye v-if="!showNewPassword" :size="17" />
                  <EyeOff v-else :size="17" />
                </button>
              </span>
              <span class="mt-2 block font-normal text-slate-400">
                Use at least 8 characters.
              </span>
            </label>

            <label class="block text-xs font-semibold text-slate-700">
              Confirm New Password
              <span class="relative mt-2 block">
                <input
                  v-model="password.confirm"
                  required
                  minlength="8"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  autocomplete="new-password"
                  class="w-full rounded-lg border border-slate-200 px-3 py-3 pr-11 text-sm font-normal outline-none focus:border-orange-400 focus:ring-4 focus:ring-orange-50"
                />
                <button
                  type="button"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <Eye v-if="!showConfirmPassword" :size="17" />
                  <EyeOff v-else :size="17" />
                </button>
              </span>
            </label>

            <p
              v-if="password.confirm && password.next !== password.confirm"
              class="text-xs text-red-600"
            >
              Passwords do not match.
            </p>

            <div class="border-t border-slate-100 pt-5">
              <button
                type="submit"
                :disabled="!password.current || password.next.length < 8 || password.next !== password.confirm"
                class="rounded-lg bg-orange-600 px-5 py-3 text-xs font-semibold text-white transition hover:bg-orange-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Update Password
              </button>
            </div>
          </form>

          <!-- NOTIFICATIONS -->
          <div v-else-if="activeSection === 'notifications'">
            <div class="divide-y divide-slate-100">
              <label
                v-for="item in notificationItems"
                :key="item.key"
                class="flex cursor-pointer items-center justify-between gap-4 py-5 first:pt-0"
              >
                <span class="min-w-0">
                  <span class="block text-sm font-semibold text-slate-800">
                    {{ item.title }}
                  </span>
                  <span class="mt-1 block text-xs leading-5 text-slate-500">
                    {{ item.desc }}
                  </span>
                </span>
                <input
                  v-model="notifications[item.key]"
                  type="checkbox"
                  class="h-5 w-5 shrink-0 accent-orange-600"
                />
              </label>
            </div>

            <div class="mt-5 flex justify-end border-t border-slate-100 pt-5">
              <button
                type="button"
                class="rounded-lg bg-orange-600 px-5 py-3 text-xs font-semibold text-white hover:bg-orange-700"
                @click="saveChanges"
              >
                Save Preferences
              </button>
            </div>
          </div>

          <!-- PRIVACY -->
          <div v-else-if="activeSection === 'privacy'">
            <div class="divide-y divide-slate-100">
              <label
                v-for="item in privacyItems"
                :key="item.key"
                class="flex cursor-pointer items-center justify-between gap-4 py-5 first:pt-0"
              >
                <span class="min-w-0">
                  <span class="block text-sm font-semibold text-slate-800">
                    {{ item.title }}
                  </span>
                  <span class="mt-1 block text-xs leading-5 text-slate-500">
                    {{ item.desc }}
                  </span>
                </span>
                <input
                  v-model="privacy[item.key]"
                  type="checkbox"
                  class="h-5 w-5 shrink-0 accent-orange-600"
                />
              </label>
            </div>

            <div class="mt-5 flex justify-end border-t border-slate-100 pt-5">
              <button
                type="button"
                class="rounded-lg bg-orange-600 px-5 py-3 text-xs font-semibold text-white hover:bg-orange-700"
                @click="saveChanges"
              >
                Save Privacy Settings
              </button>
            </div>
          </div>

          <!-- LANGUAGE & REGION -->
          <div v-else-if="activeSection === 'language'" class="space-y-5">
            <label class="block text-xs font-semibold text-slate-700">
              Preferred Language
              <select
                v-model="preferences.language"
                class="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-3 text-sm font-normal outline-none focus:border-orange-400 focus:ring-4 focus:ring-orange-50"
              >
                <option>English</option>
                <option>Kiswahili</option>
              </select>
            </label>

            <label class="block text-xs font-semibold text-slate-700">
              Country / Region
              <select
                v-model="preferences.region"
                class="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-3 text-sm font-normal outline-none focus:border-orange-400 focus:ring-4 focus:ring-orange-50"
              >
                <option>Tanzania</option>
                <option>Kenya</option>
                <option>Uganda</option>
                <option>Other</option>
              </select>
            </label>

            <div class="border-t border-slate-100 pt-5">
              <button
                type="button"
                class="rounded-lg bg-orange-600 px-5 py-3 text-xs font-semibold text-white hover:bg-orange-700"
                @click="saveChanges"
              >
                Save Preferences
              </button>
            </div>
          </div>

          <!-- APPEARANCE -->
          <div v-else-if="activeSection === 'appearance'">
            <p class="mb-5 text-xs text-slate-500">
              Choose how you prefer the portal to look.
            </p>

            <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
              <button
                v-for="option in ['Light', 'Dark', 'System']"
                :key="option"
                type="button"
                class="rounded-xl border-2 p-3 text-left transition"
                :class="preferences.appearance === option
                  ? 'border-orange-500 bg-orange-50'
                  : 'border-slate-200 hover:border-slate-300'"
                @click="preferences.appearance = option"
              >
                <div
                  class="mb-3 flex h-20 items-center justify-center rounded-lg border"
                  :class="option === 'Dark'
                    ? 'border-slate-700 bg-slate-800'
                    : 'border-slate-200 bg-white'"
                >
                  <Palette
                    :size="25"
                    :class="option === 'Dark' ? 'text-white' : 'text-slate-400'"
                  />
                </div>

                <span class="text-xs font-semibold text-slate-800">
                  {{ option }}
                </span>

                <CheckCircle2
                  v-if="preferences.appearance === option"
                  :size="16"
                  class="float-right text-orange-600"
                />
                <Circle
                  v-else
                  :size="16"
                  class="float-right text-slate-300"
                />
              </button>
            </div>

            <div class="mt-6 flex justify-end border-t border-slate-100 pt-5">
              <button
                type="button"
                class="rounded-lg bg-orange-600 px-5 py-3 text-xs font-semibold text-white hover:bg-orange-700"
                @click="saveChanges"
              >
                Save Appearance
              </button>
            </div>
          </div>

          <!-- HELP & SUPPORT -->
          <div v-else class="space-y-5">
            <div class="rounded-xl bg-slate-50 p-5">
              <div class="mb-3 flex h-11 w-11 items-center justify-center rounded-xl bg-orange-50 text-orange-600">
                <Headset :size="23" />
              </div>
              <h3 class="text-sm font-bold text-slate-800">
                Need help with your account?
              </h3>
              <p class="mt-2 text-xs leading-6 text-slate-500">
                Our support team can help with your profile, CV, job applications and account settings.
              </p>
            </div>

            <a
              href="mailto:support@halokazi.co.tz"
              class="flex items-center justify-between rounded-xl border border-slate-200 p-4 text-xs font-semibold text-slate-700 transition hover:bg-slate-50"
            >
              <span class="flex items-center gap-3">
                <Headset :size="19" />
                Contact Support
              </span>
              <ChevronRight :size="17" />
            </a>

            <p class="text-xs leading-5 text-slate-400">
              Replace the support email with the official contact address for your platform.
            </p>
          </div>
        </main>
      </div>
    </div>
  </DashboardLayout>
</template>
