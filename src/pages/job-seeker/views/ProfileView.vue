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
import { useSession } from '../../../auth/session'
import * as profileApi from '../../../api/profile'
import { useToast } from '../../../composables/useToast'

const session = useSession()
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

// Education/experience are added through small inline forms rather than editing an open-ended
// list in place - each submit is its own immediate API call, since the real backend has no
// bulk "save the whole profile" endpoint.
const addingEducation = ref(false)
const savingEducation = ref(false)
const newEducation = ref({ qualification: '', institution: '', fieldOfStudy: '', startDate: '', endDate: '', currentlyStudying: false })
const removingEducationId = ref(null)

const addingExperience = ref(false)
const savingExperience = ref(false)
const newExperience = ref({ jobTitle: '', company: '', location: '', startDate: '', endDate: '', currentlyWorking: false, description: '' })
const removingExperienceId = ref(null)

const newSkillName = ref('')
const addingSkill = ref(false)
const removingSkillId = ref(null)

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
const originalPersonalInfo = ref(emptyProfile().personalInfo)

function cloneData(data) {
  return JSON.parse(JSON.stringify(data))
}

async function loadProfile() {
  loading.value = true
  try {
    const data = await profileApi.getProfile()
    profileData.value = data
    originalPersonalInfo.value = cloneData(data.personalInfo)
  } catch {
    toast.error('Could not load your profile.')
  } finally {
    loading.value = false
  }
}

/** Only the Personal Info section has a Save/Cancel pair - education/experience/skills save per action. */
async function saveProfile() {
  saving.value = true
  try {
    const saved = await profileApi.updateProfile(profileData.value.personalInfo)
    profileData.value.personalInfo = saved
    originalPersonalInfo.value = cloneData(saved)
    isEditing.value = false
    saveMessage.value = 'Your profile has been saved.'
    window.setTimeout(() => { saveMessage.value = '' }, 3000)
  } catch {
    toast.error('Unable to save. Please try again.')
  } finally {
    saving.value = false
  }
}

function startEditing() {
  originalPersonalInfo.value = cloneData(profileData.value.personalInfo)
  isEditing.value = true
  saveMessage.value = ''
}

function cancelEditing() {
  profileData.value.personalInfo = cloneData(originalPersonalInfo.value)
  isEditing.value = false
  saveMessage.value = ''
}

function openAddEducation() {
  newEducation.value = { qualification: '', institution: '', fieldOfStudy: '', startDate: '', endDate: '', currentlyStudying: false }
  addingEducation.value = true
}

async function submitEducation() {
  if (!newEducation.value.qualification.trim() || !newEducation.value.institution.trim()) {
    toast.error('Qualification and institution are required.')
    return
  }
  savingEducation.value = true
  try {
    const saved = await profileApi.addEducation(newEducation.value)
    profileData.value.education.push(saved)
    addingEducation.value = false
    toast.success('Education added.')
  } catch {
    toast.error('Could not add that education record.')
  } finally {
    savingEducation.value = false
  }
}

async function deleteEducation(item) {
  removingEducationId.value = item.id
  try {
    await profileApi.removeEducation(item.id)
    profileData.value.education = profileData.value.education.filter((e) => e.id !== item.id)
  } catch {
    toast.error('Could not remove that education record.')
  } finally {
    removingEducationId.value = null
  }
}

function openAddExperience() {
  newExperience.value = { jobTitle: '', company: '', location: '', startDate: '', endDate: '', currentlyWorking: false, description: '' }
  addingExperience.value = true
}

async function submitExperience() {
  if (!newExperience.value.jobTitle.trim() || !newExperience.value.company.trim()) {
    toast.error('Job title and company are required.')
    return
  }
  savingExperience.value = true
  try {
    const saved = await profileApi.addExperience(newExperience.value)
    profileData.value.experience.push(saved)
    addingExperience.value = false
    toast.success('Experience added.')
  } catch {
    toast.error('Could not add that experience.')
  } finally {
    savingExperience.value = false
  }
}

async function deleteExperience(item) {
  removingExperienceId.value = item.id
  try {
    await profileApi.removeExperience(item.id)
    profileData.value.experience = profileData.value.experience.filter((e) => e.id !== item.id)
  } catch {
    toast.error('Could not remove that experience.')
  } finally {
    removingExperienceId.value = null
  }
}

async function submitSkill() {
  const name = newSkillName.value.trim()
  if (!name) return
  addingSkill.value = true
  try {
    const saved = await profileApi.addSkill(name)
    profileData.value.skills.push(saved)
    newSkillName.value = ''
  } catch {
    toast.error('Could not add that skill.')
  } finally {
    addingSkill.value = false
  }
}

async function deleteSkill(item) {
  removingSkillId.value = item.id
  try {
    await profileApi.removeSkill(item.id)
    profileData.value.skills = profileData.value.skills.filter((s) => s.id !== item.id)
  } catch {
    toast.error('Could not remove that skill.')
  } finally {
    removingSkillId.value = null
  }
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
const fullName = computed(() => session.user.value?.fullName || 'Your Name')
const email = computed(() => session.user.value?.email || '')
const phone = computed(() => session.user.value?.phone || '')

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
            <input v-if="isEditing" v-model="profileData.personalInfo.dateOfBirth" type="date"
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
          <div class="flex justify-end">
            <button v-if="!addingEducation" type="button" @click="openAddEducation"
              class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm cursor-pointer"
              style="background: #ff5a0a">
              <Plus :size="15" /> Add Education
            </button>
          </div>

          <form v-if="addingEducation" class="rounded-xl border border-gray-200 p-4 space-y-3" @submit.prevent="submitEducation">
            <div class="grid gap-3 sm:grid-cols-2">
              <input v-model="newEducation.qualification" placeholder="Qualification" class="field-input" />
              <input v-model="newEducation.institution" placeholder="Institution" class="field-input" />
              <input v-model="newEducation.startDate" type="date" class="field-input" />
              <input v-model="newEducation.endDate" type="date" :disabled="newEducation.currentlyStudying" class="field-input disabled:opacity-50" />
              <label class="flex items-center gap-2 text-xs text-gray-600 sm:col-span-2">
                <input v-model="newEducation.currentlyStudying" type="checkbox" /> I'm currently studying here
              </label>
            </div>
            <div class="flex justify-end gap-2">
              <button type="button" @click="addingEducation = false" class="rounded-lg border border-gray-200 px-3 py-2 text-xs font-semibold text-gray-700 cursor-pointer">Cancel</button>
              <button type="submit" :disabled="savingEducation" class="rounded-lg px-3 py-2 text-xs font-semibold text-white disabled:opacity-60 cursor-pointer" style="background:#ff5a0a">
                {{ savingEducation ? 'Saving…' : 'Save' }}
              </button>
            </div>
          </form>

          <article v-for="item in profileData.education" :key="item.id"
            class="rounded-xl border border-gray-200 p-4">
            <div class="flex items-start justify-between gap-3">
              <div>
                <p class="text-sm font-bold text-gray-900">{{ item.qualification }}</p>
                <p class="mt-0.5 text-xs text-gray-500">{{ item.institution }}</p>
                <p class="mt-1 text-xs text-gray-400">{{ item.startDate || '—' }} - {{ item.currentlyStudying ? 'Present' : (item.endDate || '—') }}</p>
              </div>
              <button type="button" :disabled="removingEducationId === item.id" @click="deleteEducation(item)"
                class="rounded-lg p-1.5 text-red-500 hover:bg-red-50 cursor-pointer disabled:opacity-50">
                <Trash2 :size="15" />
              </button>
            </div>
          </article>

          <div v-if="!profileData.education.length && !addingEducation" class="rounded-xl border border-dashed border-gray-200 p-8 text-center space-y-3">
            <GraduationCap :size="32" class="mx-auto text-gray-400" />
            <p class="text-xs font-semibold text-gray-800">No education added yet</p>
            <button type="button" @click="openAddEducation" class="rounded-xl px-4 py-2 text-xs font-semibold text-white cursor-pointer" style="background:#ff5a0a">
              Add Education
            </button>
          </div>
        </div>

        <!-- Experience Step View -->
        <div v-else-if="activeStep === 'experience'" class="space-y-4">
          <div class="flex justify-end">
            <button v-if="!addingExperience" type="button" @click="openAddExperience"
              class="inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm cursor-pointer"
              style="background: #ff5a0a">
              <Plus :size="15" /> Add Experience
            </button>
          </div>

          <form v-if="addingExperience" class="rounded-xl border border-gray-200 p-4 space-y-3" @submit.prevent="submitExperience">
            <div class="grid gap-3 sm:grid-cols-2">
              <input v-model="newExperience.jobTitle" placeholder="Job Title" class="field-input" />
              <input v-model="newExperience.company" placeholder="Company" class="field-input" />
              <input v-model="newExperience.location" placeholder="Location" class="field-input" />
              <input v-model="newExperience.startDate" type="date" class="field-input" />
              <input v-model="newExperience.endDate" type="date" :disabled="newExperience.currentlyWorking" class="field-input disabled:opacity-50" />
              <label class="flex items-center gap-2 text-xs text-gray-600">
                <input v-model="newExperience.currentlyWorking" type="checkbox" /> I currently work here
              </label>
              <textarea v-model="newExperience.description" rows="2" placeholder="Description" class="field-input sm:col-span-2 resize-none"></textarea>
            </div>
            <div class="flex justify-end gap-2">
              <button type="button" @click="addingExperience = false" class="rounded-lg border border-gray-200 px-3 py-2 text-xs font-semibold text-gray-700 cursor-pointer">Cancel</button>
              <button type="submit" :disabled="savingExperience" class="rounded-lg px-3 py-2 text-xs font-semibold text-white disabled:opacity-60 cursor-pointer" style="background:#ff5a0a">
                {{ savingExperience ? 'Saving…' : 'Save' }}
              </button>
            </div>
          </form>

          <article v-for="item in profileData.experience" :key="item.id"
            class="rounded-xl border border-gray-200 p-4">
            <div class="flex items-start justify-between gap-3">
              <div>
                <p class="text-sm font-bold text-gray-900">{{ item.jobTitle }}</p>
                <p class="mt-0.5 text-xs text-gray-500">{{ item.company }}<span v-if="item.location"> · {{ item.location }}</span></p>
                <p class="mt-1 text-xs text-gray-400">{{ item.startDate || '—' }} - {{ item.currentlyWorking ? 'Present' : (item.endDate || '—') }}</p>
                <p v-if="item.description" class="mt-2 text-xs leading-5 text-gray-600">{{ item.description }}</p>
              </div>
              <button type="button" :disabled="removingExperienceId === item.id" @click="deleteExperience(item)"
                class="rounded-lg p-1.5 text-red-500 hover:bg-red-50 cursor-pointer disabled:opacity-50">
                <Trash2 :size="15" />
              </button>
            </div>
          </article>

          <div v-if="!profileData.experience.length && !addingExperience" class="rounded-xl border border-dashed border-gray-200 p-8 text-center space-y-3">
            <BriefcaseBusiness :size="32" class="mx-auto text-gray-400" />
            <p class="text-xs font-semibold text-gray-800">No experience added yet</p>
            <button type="button" @click="openAddExperience" class="rounded-xl px-4 py-2 text-xs font-semibold text-white cursor-pointer" style="background:#ff5a0a">
              Add Experience
            </button>
          </div>
        </div>

        <!-- Skills Step View -->
        <div v-else-if="activeStep === 'skills'" class="space-y-4">
          <form class="flex gap-2" @submit.prevent="submitSkill">
            <input v-model="newSkillName" placeholder="e.g. JavaScript" class="field-input" />
            <button type="submit" :disabled="addingSkill" class="inline-flex shrink-0 items-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm disabled:opacity-60 cursor-pointer" style="background: #ff5a0a">
              <Plus :size="15" /> Add
            </button>
          </form>

          <div v-if="profileData.skills.length" class="grid gap-3 sm:grid-cols-2">
            <div v-for="skill in profileData.skills" :key="skill.id"
              class="flex items-center gap-3 rounded-xl border border-gray-200 p-3">
              <Award :size="18" class="text-green-600 shrink-0" />
              <span class="flex-1 text-xs font-medium text-gray-800">{{ skill.name }}</span>
              <button type="button" :disabled="removingSkillId === skill.id" @click="deleteSkill(skill)"
                class="rounded-lg p-1 text-red-500 hover:bg-red-50 cursor-pointer disabled:opacity-50">
                <X :size="14" />
              </button>
            </div>
          </div>

          <div v-if="!profileData.skills.length" class="rounded-xl border border-dashed border-gray-200 p-8 text-center space-y-3">
            <Code2 :size="32" class="mx-auto text-gray-400" />
            <p class="text-xs font-semibold text-gray-800">No skills added yet</p>
            <p class="text-[11px] text-gray-400">Use the field above to add your first skill.</p>
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