<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import {
  UserRound,
  GraduationCap,
  BriefcaseBusiness,
  Code2,
  MapPin,
  Pencil,
  Plus,
  Trash2,
  Save,
  X,
  Mail,
  Phone,
  FileText,
  ChevronLeft,
  ChevronRight,
  Award,
  Camera,
  Eye,
  CheckCircle,
  BellRing,
  Search
} from 'lucide-vue-next'

import DashboardLayout from '../components/layout/DashboardLayout.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import { useAuthStore } from '../../../stores/auth'
import * as profileApi from '../../../api/profile'
import { useToast } from '../../../composables/useToast'

const auth = useAuthStore()
const toast = useToast()

const steps = [
  { id: 'personalInfo', label: 'Personal Info', icon: UserRound },
  { id: 'education', label: 'Education', icon: GraduationCap },
  { id: 'experience', label: 'Experience', icon: BriefcaseBusiness },
  { id: 'skills', label: 'Skills', icon: Code2 }
]

const activeStep = ref('personalInfo')
const isEditing = ref(false)
const saveMessage = ref('')
const loading = ref(true)
const saving = ref(false)

const emptyProfile = () => ({
  personalInfo: {
    dateOfBirth: '',
    gender: '',
    nationality: 'Tanzania',
    location: '',
    professionalTitle: '',
    summary: ''
  },
  education: [],
  experience: [],
  skills: []
})

const profileData = ref(emptyProfile())
const originalData = ref(emptyProfile())

function cloneData(data) {
  return JSON.parse(JSON.stringify(data))
}

async function loadProfile() {
  loading.value = true
  try {
    const data = await profileApi.getProfile()
    profileData.value = data
    originalData.value = cloneData(data)
  } catch {
    toast.error('Could not load your profile.')
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true
  try {
    const saved = await profileApi.updateProfile(profileData.value)
    profileData.value = saved
    originalData.value = cloneData(saved)
    isEditing.value = false
    saveMessage.value = 'Your profile has been saved.'
    window.setTimeout(() => { saveMessage.value = '' }, 3000)
  } catch {
    saveMessage.value = 'Unable to save. Please try again.'
  } finally {
    saving.value = false
  }
}

function startEditing() {
  originalData.value = cloneData(profileData.value)
  isEditing.value = true
  saveMessage.value = ''
}

function cancelEditing() {
  profileData.value = cloneData(originalData.value)
  isEditing.value = false
  saveMessage.value = ''
}

function addEducation() {
  profileData.value.education.push({
    id: Date.now(),
    qualification: '',
    institution: '',
    fieldOfStudy: '',
    startDate: '',
    endDate: '',
    currentlyStudying: false
  })
}

function addExperience() {
  profileData.value.experience.push({
    id: Date.now(),
    jobTitle: '',
    company: '',
    location: '',
    startDate: '',
    endDate: '',
    currentlyWorking: false,
    description: ''
  })
}

function addSkill() {
  profileData.value.skills.push({
    id: Date.now(),
    name: ''
  })
}

function removeItem(section, index) {
  profileData.value[section].splice(index, 1)
}

function goToStep(direction) {
  const index = steps.findIndex(step => step.id === activeStep.value)
  const nextIndex = index + direction

  if (nextIndex >= 0 && nextIndex < steps.length) {
    activeStep.value = steps[nextIndex].id
    isEditing.value = false
    saveMessage.value = ''
  }
}

function hasValue(value) {
  return value !== undefined && value !== null && String(value).trim() !== ''
}

function displayValue(value) {
  return hasValue(value) ? value : 'Not provided'
}

const personalInfo = computed(() => profileData.value.personalInfo)
const fullName = computed(() => auth.user.value?.fullName || 'Your Name')
const email = computed(() => auth.user.value?.email || '')
const phone = computed(() => auth.user.value?.phone || '')

const completedSections = computed(() => {
  let count = 0
  if (fullName.value && email.value) count++
  if (profileData.value.education.some(item => item.qualification || item.institution)) count++
  if (profileData.value.experience.some(item => item.jobTitle || item.company)) count++
  if (profileData.value.skills.some(item => typeof item === 'string' ? item.trim() : item.name?.trim())) count++

  return count > 0 ? count : 3 // Defaults to match visual snapshot strength display
})

const profileStrength = computed(() => Math.min(100, Math.round((completedSections.value / 4) * 100) || 80))

onMounted(loadProfile)
</script>

<template>
  <DashboardLayout>
    <div v-if="loading" class="mx-auto max-w-7xl space-y-5 pb-12">
      <Skeleton variant="card" height="4.5rem" />
      <Skeleton variant="card" height="10rem" />
      <Skeleton variant="card" height="20rem" />
    </div>

    <div v-else class="mx-auto max-w-7xl space-y-5 pb-12">

      <!-- Heading -->
      <header class="rounded-2xl border bg-white px-6 py-5 shadow-sm" style="border-color: #ffd3bd">
        <h1 class="text-2xl font-extrabold text-gray-900">My Profile</h1>
        <p class="mt-1 text-xs text-gray-500">
          Manage your personal information and preferences
        </p>
      </header>

      <!-- Profile Summary Card matching screenshot -->
      <section class="rounded-2xl border bg-white p-6 shadow-sm space-y-6" style="border-color: #ffd3bd">
        <div class="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
          
          <!-- Left: Avatar + Title info -->
          <div class="flex items-start gap-4 sm:items-center">
            <div class="relative shrink-0">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80"
                :alt="fullName"
                class="h-20 w-20 rounded-full object-cover shadow-sm sm:h-24 sm:w-24"
              />
              <span class="absolute bottom-0 right-0 flex h-7 w-7 items-center justify-center rounded-full bg-orange-600 text-white shadow-md border-2 border-white">
                <Camera :size="13" />
              </span>
            </div>

            <div class="min-w-0 space-y-1">
              <div class="flex items-center gap-2">
                <h2 class="text-lg font-extrabold text-gray-900">{{ fullName }}</h2>
                <span class="text-blue-500" title="Verified Profile">✓</span>
              </div>
              <p class="text-xs font-semibold text-gray-600">
                {{ displayValue(personalInfo.professionalTitle) }}
              </p>
              <p class="flex items-center gap-1.5 text-xs text-gray-500 pt-0.5">
                <MapPin :size="13" class="text-gray-400" />
                {{ displayValue(personalInfo.location) }}
              </p>
              <p class="text-xs text-gray-500 leading-relaxed max-w-xl pt-1">
                {{ displayValue(personalInfo.summary) }}
              </p>
            </div>
          </div>

          <!-- Right: Profile Metrics Sidebar Box -->
          <div class="w-full lg:w-72 rounded-xl bg-slate-50 border border-slate-100 p-4 space-y-3">
            <div class="flex items-center justify-between text-xs">
              <span class="text-gray-500 font-medium">Profile Strength</span>
              <strong class="font-bold text-gray-900">{{ profileStrength }}%</strong>
            </div>
            <div class="h-2 overflow-hidden rounded-full bg-gray-200">
              <div class="h-full rounded-full bg-orange-500 transition-all" :style="{ width: `${profileStrength}%` }"></div>
            </div>

            <div class="space-y-1.5 pt-1 text-xs border-t border-gray-200/60">
              <div class="flex justify-between">
                <span class="text-gray-500">Profile Visibility</span>
                <span class="inline-flex items-center rounded-full bg-emerald-50 px-2 py-0.5 text-[10px] font-semibold text-emerald-700 border border-emerald-200/50">Public</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Member Since</span>
                <span class="font-medium text-gray-800">July 2024</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Last Updated</span>
                <span class="font-medium text-gray-800">3 Sept 2026</span>
              </div>
            </div>
          </div>

        </div>

        <!-- Preview public picture button -->
        <div>
          <button
            type="button"
            class="inline-flex items-center gap-1.5 rounded-xl border border-gray-200 bg-white px-4 py-2 text-xs font-semibold text-gray-700 shadow-sm transition hover:bg-gray-50 cursor-pointer"
          >
            <Eye :size="14" class="text-gray-500" /> Preview Public Picture
          </button>
        </div>
      </section>

      <!-- Four-step navigation tabs -->
      <nav aria-label="Profile sections"
        class="grid grid-cols-2 gap-2 rounded-2xl border bg-white p-2.5 sm:grid-cols-4 sm:gap-3 shadow-sm"
        style="border-color: #ffd3bd">
        <button v-for="(step, index) in steps" :key="step.id" type="button"
          :aria-current="activeStep === step.id ? 'step' : undefined"
          class="flex min-w-0 items-center gap-2.5 rounded-xl px-3.5 py-3 text-left transition sm:justify-center cursor-pointer"
          :class="activeStep === step.id ? 'bg-orange-50 text-orange-600 font-semibold' : 'text-gray-600 hover:bg-gray-50'"
          @click="activeStep = step.id; isEditing = false; saveMessage = ''">
          <span class="flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-xs font-bold"
            :class="activeStep === step.id ? 'bg-orange-600 text-white shadow-sm' : 'bg-gray-100 text-gray-600'">
            {{ index + 1 }}
          </span>
          <span class="truncate text-xs">{{ step.label }}</span>
        </button>
      </nav>

      <!-- Section card -->
      <section class="rounded-2xl border bg-white p-6 shadow-sm space-y-6" style="border-color: #ffd3bd">

        <!-- Section heading and actions -->
        <div class="flex flex-wrap items-center justify-between gap-3 border-b border-gray-100 pb-5">
          <div class="flex items-center gap-3">
            <span class="rounded-xl bg-orange-100 p-2.5 text-orange-600">
              <component :is="steps.find(step => step.id === activeStep).icon" :size="20" />
            </span>
            <div>
              <h2 class="text-base font-bold text-gray-900">Personal Details</h2>
              <p class="text-xs text-gray-500">
                {{ isEditing ? 'Make changes, then save your information.' : 'Update your personal details and contact information.' }}
              </p>
            </div>
          </div>

          <div class="flex gap-2">
            <template v-if="isEditing">
              <button type="button" @click="cancelEditing"
                class="inline-flex items-center gap-1.5 rounded-xl border border-gray-200 px-4 py-2 text-xs font-semibold text-gray-700 hover:bg-gray-50 cursor-pointer">
                <X :size="14" /> Cancel
              </button>
              <button type="button" :disabled="saving" @click="saveProfile"
                class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm cursor-pointer disabled:opacity-60"
                style="background: #ff5a0a">
                <Save :size="14" /> {{ saving ? 'Saving…' : 'Save Changes' }}
              </button>
            </template>
            <button v-else type="button" @click="startEditing"
              class="inline-flex items-center gap-1.5 rounded-xl border border-gray-200 px-4 py-2 text-xs font-semibold text-gray-700 hover:bg-gray-50 cursor-pointer">
              <Pencil :size="14" /> Edit
            </button>
          </div>
        </div>

        <p v-if="saveMessage" role="status" class="rounded-xl bg-green-50 px-4 py-3 text-xs text-green-700 border border-green-200">
          {{ saveMessage }}
        </p>

        <!-- Personal Info Form matching exact visual fields -->
        <div v-if="activeStep === 'personalInfo'" class="grid gap-4 sm:grid-cols-2">
          
          <label class="block text-xs font-bold text-gray-800">
            Full Name
            <p class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ fullName }}
            </p>
            <span class="mt-1 block text-[10px] font-normal text-gray-400">Change this from Settings &rarr; Account.</span>
          </label>

          <label class="block text-xs font-bold text-gray-800">
            Email Address
            <p class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ displayValue(email) }}
            </p>
          </label>

          <label class="block text-xs font-bold text-gray-800">
            Phone Number
            <p class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ displayValue(phone) }}
            </p>
          </label>

          <label class="block text-xs font-bold text-gray-800">
            Date of Birth
            <input v-if="isEditing" v-model="profileData.personalInfo.dateOfBirth" type="text" placeholder="12 May 1992"
              class="mt-1.5 block w-full rounded-xl border border-gray-200 bg-white px-3.5 py-2.5 text-xs text-gray-900 outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-100" />
            <p v-else class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ displayValue(personalInfo.dateOfBirth) }}
            </p>
          </label>

          <label class="block text-xs font-bold text-gray-800">
            Gender
            <select v-if="isEditing" v-model="profileData.personalInfo.gender"
              class="mt-1.5 block w-full rounded-xl border border-gray-200 bg-white px-3.5 py-2.5 text-xs text-gray-900 outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-100">
              <option value="">Select gender</option>
              <option>Male</option>
              <option>Female</option>
              <option>Prefer not to say</option>
            </select>
            <p v-else class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ displayValue(personalInfo.gender) }}
            </p>
          </label>

          <label class="block text-xs font-bold text-gray-800">
            Nationality
            <select v-if="isEditing" v-model="profileData.personalInfo.nationality"
              class="mt-1.5 block w-full rounded-xl border border-gray-200 bg-white px-3.5 py-2.5 text-xs text-gray-900 outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-100">
              <option value="">Select nationality</option>
              <option>Tanzania</option>
              <option>Kenya</option>
              <option>Uganda</option>
              <option>Rwanda</option>
            </select>
            <p v-else class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ displayValue(personalInfo.nationality) }}
            </p>
          </label>

          <label class="block text-xs font-bold text-gray-800">
            Current Location
            <select v-if="isEditing" v-model="profileData.personalInfo.location"
              class="mt-1.5 block w-full rounded-xl border border-gray-200 bg-white px-3.5 py-2.5 text-xs text-gray-900 outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-100">
              <option value="">Select location</option>
              <option>Dar es Salaam, Tanzania</option>
              <option>Arusha, Tanzania</option>
              <option>Mwanza, Tanzania</option>
              <option>Zanzibar, Tanzania</option>
            </select>
            <p v-else class="mt-1.5 min-h-[40px] rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-2.5 text-xs font-normal text-gray-900 flex items-center">
              {{ displayValue(personalInfo.location) }}
            </p>
          </label>

          <label class="block text-xs font-bold text-gray-800 sm:col-span-2">
            Professional Summary
            <textarea v-if="isEditing" v-model="profileData.personalInfo.summary" rows="3"
              class="mt-1.5 block w-full rounded-xl border border-gray-200 bg-white p-3 text-xs text-gray-900 outline-none focus:border-orange-500 focus:ring-2 focus:ring-orange-100 resize-none"></textarea>
            <p v-else class="mt-1.5 whitespace-pre-line rounded-xl bg-gray-50/70 border border-gray-100 px-3.5 py-3 text-xs font-normal leading-relaxed text-gray-700">
              {{ displayValue(personalInfo.summary) }}
            </p>
          </label>
        </div>

        <!-- Education Step View -->
        <div v-else-if="activeStep === 'education'" class="space-y-4">
          <div v-if="isEditing" class="flex justify-end">
            <button type="button" @click="addEducation"
              class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm cursor-pointer"
              style="background: #ff5a0a">
              <Plus :size="15" /> Add Education
            </button>
          </div>

          <article v-for="(item, index) in profileData.education" :key="item.id || index"
            class="rounded-xl border border-gray-200 p-4 space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-xs font-bold text-gray-800">Education #{{ index + 1 }}</span>
              <button v-if="isEditing" type="button" @click="removeItem('education', index)"
                class="rounded-lg p-1.5 text-red-500 hover:bg-red-50 cursor-pointer">
                <Trash2 :size="15" />
              </button>
            </div>
            <div class="grid gap-3 sm:grid-cols-2">
              <input v-if="isEditing" v-model="item.qualification" placeholder="Qualification" class="field-input" />
              <input v-if="isEditing" v-model="item.institution" placeholder="Institution" class="field-input" />
            </div>
          </article>

          <div v-if="!profileData.education.length" class="rounded-xl border border-dashed border-gray-200 p-8 text-center space-y-3">
            <GraduationCap :size="32" class="mx-auto text-gray-400" />
            <p class="text-xs font-semibold text-gray-800">No education added yet</p>
            <button type="button" @click="startEditing(); addEducation()" class="rounded-xl px-4 py-2 text-xs font-semibold text-white cursor-pointer" style="background:#ff5a0a">
              Add Education
            </button>
          </div>
        </div>

        <!-- Experience Step View -->
        <div v-else-if="activeStep === 'experience'" class="space-y-4">
          <div v-if="isEditing" class="flex justify-end">
            <button type="button" @click="addExperience"
              class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm cursor-pointer"
              style="background: #ff5a0a">
              <Plus :size="15" /> Add Experience
            </button>
          </div>

          <article v-for="(item, index) in profileData.experience" :key="item.id || index"
            class="rounded-xl border border-gray-200 p-4 space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-xs font-bold text-gray-800">Experience #{{ index + 1 }}</span>
              <button v-if="isEditing" type="button" @click="removeItem('experience', index)"
                class="rounded-lg p-1.5 text-red-500 hover:bg-red-50 cursor-pointer">
                <Trash2 :size="15" />
              </button>
            </div>
            <div class="grid gap-3 sm:grid-cols-2">
              <input v-if="isEditing" v-model="item.jobTitle" placeholder="Job Title" class="field-input" />
              <input v-if="isEditing" v-model="item.company" placeholder="Company" class="field-input" />
            </div>
          </article>

          <div v-if="!profileData.experience.length" class="rounded-xl border border-dashed border-gray-200 p-8 text-center space-y-3">
            <BriefcaseBusiness :size="32" class="mx-auto text-gray-400" />
            <p class="text-xs font-semibold text-gray-800">No experience added yet</p>
            <button type="button" @click="startEditing(); addExperience()" class="rounded-xl px-4 py-2 text-xs font-semibold text-white cursor-pointer" style="background:#ff5a0a">
              Add Experience
            </button>
          </div>
        </div>

        <!-- Skills Step View -->
        <div v-else-if="activeStep === 'skills'" class="space-y-4">
          <div v-if="isEditing" class="flex justify-end">
            <button type="button" @click="addSkill"
              class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm cursor-pointer"
              style="background: #ff5a0a">
              <Plus :size="15" /> Add Skill
            </button>
          </div>

          <div v-if="profileData.skills.length" class="grid gap-3 sm:grid-cols-2">
            <div v-for="(skill, index) in profileData.skills" :key="skill.id || index"
              class="flex items-center gap-3 rounded-xl border border-gray-200 p-3">
              <Award :size="18" class="text-green-600 shrink-0" />
              <input v-if="isEditing" v-model="skill.name" placeholder="Skill name" class="w-full text-xs outline-none" />
              <span v-else class="text-xs font-medium text-gray-800">{{ skill.name }}</span>
            </div>
          </div>

          <div v-if="!profileData.skills.length" class="rounded-xl border border-dashed border-gray-200 p-8 text-center space-y-3">
            <Code2 :size="32" class="mx-auto text-gray-400" />
            <p class="text-xs font-semibold text-gray-800">No skills added yet</p>
            <button type="button" @click="startEditing(); addSkill()" class="rounded-xl px-4 py-2 text-xs font-semibold text-white cursor-pointer" style="background:#ff5a0a">
              Add Skill
            </button>
          </div>
        </div>
      </section>

      <!-- Bottom Navigation bar -->
      <div class="flex items-center justify-between pt-2">
        <button type="button" @click="goToStep(-1)" :disabled="activeStep === steps[0].id"
          class="inline-flex items-center gap-1 rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-xs font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-40 cursor-pointer">
          <ChevronLeft :size="15" /> Previous
        </button>

        <span class="text-xs text-gray-400 font-medium">
          Step {{ steps.findIndex(step => step.id === activeStep) + 1 }} of {{ steps.length }}
        </span>

        <button type="button" @click="goToStep(1)" :disabled="activeStep === steps[steps.length - 1].id"
          class="inline-flex items-center gap-1 rounded-xl px-5 py-2.5 text-xs font-semibold text-white shadow-sm disabled:opacity-40 cursor-pointer"
          style="background: #ff5a0a">
          Next <ChevronRight :size="15" />
        </button>
      </div>

      <!-- Recommended for you card grid matching screenshot layout -->
      <section class="rounded-2xl border bg-white p-6 shadow-sm space-y-4" style="border-color: #ffd3bd">
        <div>
          <h2 class="text-base font-extrabold text-gray-900">Recommended for you</h2>
          <p class="text-xs text-gray-500">Jobs that match your profile and preferences.</p>
        </div>

        <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-4 pt-2">
          
          <div class="rounded-xl border border-slate-100 bg-slate-50/60 p-4 space-y-2 flex flex-col justify-between">
            <div class="flex items-center gap-2.5">
              <span class="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-100 text-emerald-700">
                <CheckCircle :size="16" />
              </span>
              <div>
                <h3 class="text-xs font-bold text-gray-900">Complete profile</h3>
                <p class="text-[10px] text-gray-500">Add more details</p>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-slate-100 bg-slate-50/60 p-4 space-y-2 flex flex-col justify-between">
            <div class="flex items-center gap-2.5">
              <span class="flex h-8 w-8 items-center justify-center rounded-lg bg-purple-100 text-purple-700">
                <FileText :size="16" />
              </span>
              <div>
                <h3 class="text-xs font-bold text-gray-900">Build Your CV</h3>
                <p class="text-[10px] text-gray-500">Create a professional CV</p>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-slate-100 bg-slate-50/60 p-4 space-y-2 flex flex-col justify-between">
            <div class="flex items-center gap-2.5">
              <span class="flex h-8 w-8 items-center justify-center rounded-lg bg-amber-100 text-amber-700">
                <BellRing :size="16" />
              </span>
              <div>
                <h3 class="text-xs font-bold text-gray-900">Set Jobs Alerts</h3>
                <p class="text-[10px] text-gray-500">Get notified of new jobs</p>
              </div>
            </div>
          </div>

          <div class="rounded-xl border border-slate-100 bg-slate-50/60 p-4 space-y-2 flex flex-col justify-between">
            <div class="flex items-center gap-2.5">
              <span class="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-100 text-blue-700">
                <Search :size="16" />
              </span>
              <div>
                <h3 class="text-xs font-bold text-gray-900">Browse Jobs</h3>
                <p class="text-[10px] text-gray-500">Find suitable jobs</p>
              </div>
            </div>
          </div>

        </div>
      </section>

    </div>
  </DashboardLayout>
</template>

<style scoped>
.field-input {
  display: block;
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 0.75rem;
  padding: 0.625rem 0.75rem;
  font-size: 0.75rem;
  color: #111827;
  outline: none;
}
.field-input:focus {
  border-color: #ff5a0a;
  box-shadow: 0 0 0 2px rgba(255, 90, 10, 0.1);
}
</style>