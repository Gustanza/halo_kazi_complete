<script setup>
import { computed, onMounted, ref } from 'vue'
import {
  UserRound, LockKeyhole, Bell, ShieldCheck, Globe, Palette,
  CircleHelp, Camera, Save, MapPin, Settings as SettingsIcon,
  CheckCircle2, Circle, FileText, Bookmark, BriefcaseBusiness,
  Headset, ChevronRight, Eye, EyeOff
} from 'lucide-vue-next'

import DashboardLayout from '../components/layout/DashboardLayout.vue'
import * as accountApi from '../../../api/accountApi'
import { useSession } from '../../../auth/session'
import { useTheme } from '../../../composables/useTheme'

const session = useSession()
const { preference: themePreference, setPreference: setThemePreference } = useTheme()

const activeSection = ref('account')
const savedMessage = ref('')
const messageTone = ref('success') // 'success' | 'error'
const loading = ref(true)
const loadError = ref('')
const saving = ref(false)
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const photoPreview = ref('') // shown once profile photos can be uploaded (no file storage yet)

// Filled from GET /api/me/settings.
const account = ref({ fullName: '', phone: '', dateOfBirth: '', gender: '', location: '', about: '' })

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

const LANGUAGE_LABELS = { EN: 'English', SW: 'Kiswahili' }
const THEME_LABELS = { light: 'Light', dark: 'Dark', system: 'System' }
const GENDER_LABELS = { MALE: 'Male', FEMALE: 'Female', OTHER: 'Other', PREFER_NOT_TO_SAY: 'Prefer not to say' }
const NOT_PROVIDED = 'Not provided'

const preferences = ref({
  language: 'English',
  region: 'Tanzania', // Halokazi serves Tanzania only, so region is not a saved setting
  appearance: THEME_LABELS[themePreference.value]
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

// No showEmail toggle: job seekers register and log in with phone number alone and have no
// real email to display (see RegistrationService / AccountSettingsService).
const privacyItems = [
  { key: 'profileVisible', title: 'Profile visibility', desc: 'Allow employers to discover your profile.' },
  { key: 'showPhone', title: 'Display phone number', desc: 'Allow employers to see your phone number.' }
]

const sectionTitle = computed(() =>
  sections.find(item => item.id === activeSection.value)?.label || 'Settings'
)

const aboutCount = computed(() => (account.value.about === NOT_PROVIDED ? 0 : account.value.about.length))

/** Copies a GET/PUT /api/me/settings response into the screen's state. */
function applySettings(settings) {
  const a = settings.account
  const place = [a.location?.address, a.location?.district, a.location?.region?.nameEn].filter(Boolean)
  account.value = {
    fullName: a.fullName,
    phone: a.phone,
    dateOfBirth: a.dateOfBirth || NOT_PROVIDED,
    gender: GENDER_LABELS[a.gender] || NOT_PROVIDED,
    location: place.length ? `${place.join(', ')}, Tanzania` : NOT_PROVIDED,
    about: a.about || NOT_PROVIDED
  }
  notifications.value = {
    email: settings.notifications.email,
    applications: settings.notifications.applicationUpdates,
    jobAlerts: settings.notifications.jobAlerts,
    newsletter: settings.notifications.newsletter
  }
  privacy.value = { ...settings.privacy }
  preferences.value.language = LANGUAGE_LABELS[settings.preferences.language] || 'English'
}

async function loadSettings() {
  loading.value = true
  loadError.value = ''
  try {
    applySettings(await accountApi.getSettings())
  } catch (e) {
    loadError.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(loadSettings)

function showMessage(text, tone = 'success') {
  savedMessage.value = text
  messageTone.value = tone
}

async function saveChanges() {
  if (saving.value) return
  saving.value = true
  try {
    switch (activeSection.value) {
      case 'password':
        await savePassword()
        break
      case 'notifications':
        applySettings(await accountApi.updateNotifications({
          email: notifications.value.email,
          applicationUpdates: notifications.value.applications,
          jobAlerts: notifications.value.jobAlerts,
          newsletter: notifications.value.newsletter
        }))
        showMessage('Notification preferences saved.')
        break
      case 'privacy':
        applySettings(await accountApi.updatePrivacy({ ...privacy.value }))
        showMessage('Privacy settings saved.')
        break
      case 'language':
        applySettings(await accountApi.updateLanguage(preferences.value.language === 'Kiswahili' ? 'SW' : 'EN'))
        await session.refresh() // keep the signed-in user's preferredLanguage in step
        showMessage('Language saved.')
        break
      case 'appearance':
        // Appearance is a per-device choice, kept in this browser.
        setThemePreference(preferences.value.appearance.toLowerCase())
        showMessage('Appearance saved on this device.')
        break
    }
  } catch (e) {
    showMessage(e.message, 'error')
  } finally {
    saving.value = false
  }
}

async function savePassword() {
  if (password.value.next !== password.value.confirm) {
    showMessage('Passwords do not match.', 'error')
    return
  }
  try {
    await accountApi.changePassword(password.value.current, password.value.next, password.value.confirm)
    password.value = { current: '', next: '', confirm: '' }
    showMessage('Password changed. Any other devices signed in to your account have been signed out.')
  } catch (e) {
    // Field errors (wrong current password, weak new password) are more specific than the summary.
    showMessage(e.fieldErrors?.[0]?.message || e.message, 'error')
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
                    Your personal details and contact information. Edit them from My Profile.
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
                :disabled="saving"
            >
              <Save :size="15" />
              Save Changes
            </button>
          </div>

          <!-- Feedback message -->
          <div
            v-if="savedMessage"
            class="mb-5 rounded-lg border px-4 py-3 text-xs leading-5"
            :class="messageTone === 'error'
              ? 'border-red-200 bg-red-50 text-red-700'
              : 'border-emerald-200 bg-emerald-50 text-emerald-800'"
            role="status"
          >
            {{ savedMessage }}
            <button class="ml-2 font-bold underline" @click="savedMessage = ''">
              Dismiss
            </button>
          </div>

          <!-- Loading / load failure (GET /api/me/settings) -->
          <p v-if="loading" class="py-10 text-center text-sm text-slate-500">Loading your settings…</p>
          <div v-else-if="loadError" class="rounded-lg border border-red-200 bg-red-50 px-4 py-4 text-sm text-red-700">
            We couldn't load your settings. {{ loadError }}
            <button type="button" class="ml-2 font-bold underline" @click="loadSettings">Try again</button>
          </div>

          <!-- ACCOUNT INFORMATION -->
          <div v-else-if="activeSection === 'account'" class="space-y-6">

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
                :disabled="saving || !password.current || password.next.length < 8 || password.next !== password.confirm"
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
                :disabled="saving"
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
                :disabled="saving"
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
                disabled
                title="Halokazi is available in Tanzania"
                class="mt-2 w-full rounded-lg border border-slate-200 bg-white px-3 py-3 text-sm font-normal outline-none focus:border-orange-400 focus:ring-4 focus:ring-orange-50 disabled:cursor-not-allowed disabled:bg-slate-50"
              >
                <option>Tanzania</option>
              </select>
            </label>

            <div class="border-t border-slate-100 pt-5">
              <button
                type="button"
                class="rounded-lg bg-orange-600 px-5 py-3 text-xs font-semibold text-white hover:bg-orange-700"
                @click="saveChanges"
                :disabled="saving"
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
                :disabled="saving"
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

          </div>
        </main>
      </div>
    </div>
  </DashboardLayout>
</template>
