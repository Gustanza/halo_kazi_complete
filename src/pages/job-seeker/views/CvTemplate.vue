<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  UserRound,
  GraduationCap,
  BriefcaseBusiness,
  Wrench,
  Upload,
  Trash2,
  MapPin,
  ArrowLeft,
  ArrowRight,
  Palette,
  Download,
  Plus,
  X,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import AppButton from '../../../components/ui/AppButton.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as cvApi from '../../../api/cv'
import { ApiRequestError } from '../../../api/http'
import { useToast } from '../../../composables/useToast'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const currentStep = ref(1)

const existingCvId = route.query.cv ? Number(route.query.cv) : null
const cvId = ref(existingCvId)
const cvName = ref('')
const saving = ref(false)
const loadingExisting = ref(!!existingCvId)
const formError = ref('')

// Real template ids come from the backend and aren't guaranteed to be 1-5 in this order, so the
// 5 hand-built preview layouts below are picked by each template's `preview` key (set in
// src/api/cv.js from the real template code), not by a hardcoded numeric id.
const templates = ref([])
const existingTemplateId = ref(null)
const selectedTemplateId = computed(
  () => Number(route.query.template) || existingTemplateId.value || templates.value[0]?.id || null,
)
const selectedTemplate = computed(
  () => templates.value.find((t) => t.id === selectedTemplateId.value) ?? templates.value[0] ?? null,
)
const previewKey = computed(() => selectedTemplate.value?.preview ?? 'simple')
const templateName = computed(() => selectedTemplate.value?.name ?? 'CV')

const form = ref({
  fullName: '',
  email: '',
  phone: '',
  location: '',
  professionalTitle: '',
  summary: '',
  photo: null,
})

const education = ref([])

const newEducation = ref({
  degree: '',
  institution: '',
  startYear: '',
  endYear: '',
})

const addEducation = () => {
  if (!newEducation.value.degree.trim() ||
      !newEducation.value.institution.trim()) return

  education.value.push({
    id: Date.now(),
    ...newEducation.value,
  })

  newEducation.value = {
    degree: '',
    institution: '',
    startYear: '',
    endYear: '',
  }
}

const removeEducation = id => {
  education.value = education.value.filter(item => item.id !== id)
}

const experiences = ref([])

const newExperience = ref({
  jobTitle: '',
  company: '',
  location: '',
  startDate: '',
  endDate: '',
  description: '',
})

const addExperience = () => {
  if (!newExperience.value.jobTitle.trim() ||
      !newExperience.value.company.trim()) return

  experiences.value.push({
    id: Date.now(),
    ...newExperience.value,
    endDate: newExperience.value.endDate || 'Present',
  })

  newExperience.value = {
    jobTitle: '',
    company: '',
    location: '',
    startDate: '',
    endDate: '',
    description: '',
  }
}

const removeExperience = id => {
  experiences.value = experiences.value.filter(item => item.id !== id)
}

const skills = ref([])

const newSkill = ref('')

const addSkill = () => {
  const skill = newSkill.value.trim()
  if (!skill || skills.value.includes(skill)) return

  skills.value.push(skill)
  newSkill.value = ''
}

const removeSkill = skill => {
  skills.value = skills.value.filter(item => item !== skill)
}

function hydrateFrom(record) {
  cvName.value = record.cvName || ''
  existingTemplateId.value = record.templateId || null
  form.value = {
    fullName: record.fullName || '',
    email: record.email || '',
    phone: record.phone || '',
    location: record.location || '',
    professionalTitle: record.professionalTitle || '',
    summary: record.summary || '',
    photo: record.photo || null,
  }
  education.value = record.education || []
  experiences.value = record.experiences || []
  skills.value = record.skills || []
}

onMounted(async () => {
  try {
    templates.value = await cvApi.listTemplates()
    if (cvId.value) {
      const existing = await cvApi.getCv(cvId.value)
      hydrateFrom(existing)
    } else {
      // Per the 23 Oct 2026 meeting decision: start a new CV from the last used one
      // instead of a blank form.
      const lastUsed = await cvApi.getLastUsedCv()
      if (lastUsed) hydrateFrom(lastUsed)
    }
  } catch {
    // Fetch failed (e.g. deleted CV) - fall back to a blank form rather than blocking the page.
  } finally {
    loadingExisting.value = false
  }
})

async function saveCv() {
  formError.value = ''
  saving.value = true
  try {
    const payload = {
      cvName: cvName.value.trim() || `${form.value.professionalTitle || 'Untitled'} CV`,
      templateId: selectedTemplateId.value,
      headline: form.value.professionalTitle,
      summary: form.value.summary,
      fullName: form.value.fullName,
      email: form.value.email,
      phone: form.value.phone,
      location: form.value.location,
      professionalTitle: form.value.professionalTitle,
      photo: form.value.photo,
      education: education.value,
      experiences: experiences.value,
      skills: skills.value,
    }
    const saved = cvId.value ? await cvApi.updateCv(cvId.value, payload) : await cvApi.createCv(payload)
    await cvApi.setLastUsedCv(saved.id)
    toast.success('CV saved successfully.')
    router.push({ path: '/dashboard/view-cv', query: { cv: saved.id } })
  } catch (error) {
    formError.value = error instanceof ApiRequestError ? error.message : 'Could not save your CV. Please try again.'
  } finally {
    saving.value = false
  }
}

const steps = [
  { number: 1, label: 'Personal Info', icon: UserRound },
  { number: 2, label: 'Education', icon: GraduationCap },
  { number: 3, label: 'Experience', icon: BriefcaseBusiness },
  { number: 4, label: 'Skills', icon: Wrench },
]

const nextStep = () => {
  if (currentStep.value < steps.length) currentStep.value++
}

const previousStep = () => {
  if (currentStep.value > 1) currentStep.value--
}

const uploadPhoto = event => {
  const file = event.target.files?.[0]
  if (!file) return

  if (form.value.photo?.startsWith('blob:')) {
    URL.revokeObjectURL(form.value.photo)
  }

  form.value.photo = URL.createObjectURL(file)
}

const removePhoto = () => {
  if (form.value.photo?.startsWith('blob:')) {
    URL.revokeObjectURL(form.value.photo)
  }
  form.value.photo = null
}

onBeforeUnmount(() => {
  if (form.value.photo?.startsWith('blob:')) {
    URL.revokeObjectURL(form.value.photo)
  }
})

const downloadPdf = () => window.print()
</script>

<template>
  <DashboardLayout>
    <div class="space-y-5 cv-builder-page">
      <!-- Header -->
      <section class="rounded-2xl border border-orange-200 bg-white px-5 py-4 shadow-sm no-print">
      <button
        type="button"
        class="mb-3 inline-flex items-center gap-2 text-sm font-medium text-gray-500 transition hover:text-[#FD5C20]"
        @click="router.push('/dashboard/cv-builder')"
      >
        <ArrowLeft :size="17" />
           Back to CV Builder
      </button>
        <h1 class="text-3xl font-extrabold" style="color: var(--color-text)">
          CV Template
        </h1>
        <p class="mt-1 text-sm">
          Complete your details and preview your selected CV template.
        </p>
      </section>

      <div v-if="loadingExisting" class="grid grid-cols-1 gap-5 xl:grid-cols-[1fr_460px]">
        <Skeleton variant="card" height="24rem" />
        <Skeleton variant="card" height="24rem" />
      </div>

      <div v-else class="grid grid-cols-1 items-start gap-5 xl:grid-cols-[1fr_460px]">
        <!-- LEFT: FORM -->
        <div class="space-y-4 no-print">
          <!-- Steps -->
          <div class="overflow-x-auto rounded-2xl border border-orange-200 bg-white px-4 py-3 shadow-sm">
            <div class="flex min-w-max items-center justify-between gap-2">
              <button
                v-for="step in steps"
                :key="step.number"
                type="button"
                class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium"
                :class="currentStep === step.number
                  ? 'bg-primary-50 text-primary'
                  : currentStep > step.number
                    ? 'text-primary'
                    : 'text-gray-500'"
                @click="currentStep = step.number"
              >
                <span
                  class="flex h-6 w-6 items-center justify-center rounded-full text-xs"
                  :class="currentStep >= step.number
                    ? 'bg-primary text-white'
                    : 'bg-gray-100 text-gray-600'"
                >
                  {{ step.number }}
                </span>
                {{ step.label }}
              </button>
            </div>
          </div>

          <!-- PERSONAL INFO -->
          <section
            v-if="currentStep === 1"
            class="rounded-2xl border border-orange-200 bg-white p-5 shadow-sm"
          >
            <div class="mb-5 flex items-center gap-3">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary-50 text-primary">
                <UserRound :size="21" />
              </div>
              <div>
                <h2 class="text-lg font-bold">Personal Details</h2>
                <p class="text-xs text-gray-500">Your basic information and professional profile.</p>
              </div>
            </div>

            <div class="grid gap-4 md:grid-cols-2">
              <div class="md:col-span-2">
                <label class="mb-1.5 block text-sm font-medium">CV Name *</label>
                <input v-model="cvName" class="cv-input" type="text" placeholder="e.g. Frontend Developer CV" />
              </div>

              <div>
                <label class="mb-1.5 block text-sm font-medium">Full Name *</label>
                <input v-model="form.fullName" class="cv-input" type="text" />
              </div>

              <div>
                <label class="mb-1.5 block text-sm font-medium">Email Address *</label>
                <input v-model="form.email" class="cv-input" type="email" />
              </div>

              <div>
                <label class="mb-1.5 block text-sm font-medium">Phone Number *</label>
                <input v-model="form.phone" class="cv-input" type="text" />
              </div>

              <div>
                <label class="mb-1.5 block text-sm font-medium">Location *</label>
                <div class="relative">
                  <MapPin :size="17" class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
                  <input v-model="form.location" class="cv-input pl-9" type="text" />
                </div>
              </div>

              <div class="md:col-span-2">
                <label class="mb-1.5 block text-sm font-medium">Professional Title *</label>
                <input v-model="form.professionalTitle" class="cv-input" type="text" />
              </div>

              <div class="md:col-span-2">
                <label class="mb-1.5 block text-sm font-medium">Professional Summary</label>
                <textarea v-model="form.summary" rows="4" maxlength="500" class="cv-input resize-y" />
                <p class="mt-1 text-right text-xs text-gray-400">
                  {{ form.summary.length }}/500
                </p>
              </div>

              <div class="md:col-span-2">
                <label class="mb-2 block text-sm font-medium">Profile Photo</label>
                <div class="flex items-center gap-4">
                  <div class="flex h-20 w-20 items-center justify-center overflow-hidden rounded-full border bg-gray-100">
                    <img v-if="form.photo" :src="form.photo" alt="Profile" class="h-full w-full object-cover" />
                    <UserRound v-else :size="32" class="text-gray-400" />
                  </div>

                  <div class="flex flex-wrap gap-2">
                    <label class="inline-flex cursor-pointer items-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-semibold text-white">
                      <Upload :size="17" /> Upload Photo
                      <input type="file" accept="image/png,image/jpeg" class="hidden" @change="uploadPhoto" />
                    </label>
                    <button
                      v-if="form.photo"
                      type="button"
                      class="inline-flex items-center gap-2 rounded-lg border px-4 py-2.5 text-sm"
                      @click="removePhoto"
                    >
                      <Trash2 :size="16" /> Remove
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-7 flex justify-end border-t pt-4">
              <button type="button" class="cv-primary-button" @click="nextStep">
                Save & Continue <ArrowRight :size="16" />
              </button>
            </div>
          </section>

          <!-- EDUCATION -->
          <section
            v-else-if="currentStep === 2"
            class="rounded-2xl border border-orange-200 bg-white p-5 shadow-sm"
          >
            <div class="mb-5 flex items-center gap-3">
              <GraduationCap :size="24" class="text-primary" />
              <div>
                <h2 class="text-lg font-bold">Education</h2>
                <p class="text-xs text-gray-500">Add your qualifications.</p>
              </div>
            </div>

            <div class="space-y-3">
              <div v-for="item in education" :key="item.id" class="rounded-xl border p-4">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <h3 class="text-sm font-bold">{{ item.degree }}</h3>
                    <p class="mt-1 text-xs text-gray-500">{{ item.institution }}</p>
                    <p class="mt-1 text-xs text-gray-400">{{ item.startYear }} - {{ item.endYear }}</p>
                  </div>
                  <button type="button" class="text-red-500" @click="removeEducation(item.id)">
                    <Trash2 :size="16" />
                  </button>
                </div>
              </div>
            </div>

            <div class="mt-5 rounded-xl bg-slate-50 p-4">
              <h3 class="mb-4 text-sm font-bold">Add Education</h3>
              <div class="grid gap-4 md:grid-cols-2">
                <div class="md:col-span-2">
                  <label class="cv-label">Degree / Qualification</label>
                  <input v-model="newEducation.degree" class="cv-input" placeholder="Bachelor of Computer Science" />
                </div>
                <div class="md:col-span-2">
                  <label class="cv-label">Institution</label>
                  <input v-model="newEducation.institution" class="cv-input" placeholder="University name" />
                </div>
                <div>
                  <label class="cv-label">Start Year</label>
                  <input v-model="newEducation.startYear" class="cv-input" placeholder="2019" />
                </div>
                <div>
                  <label class="cv-label">End Year</label>
                  <input v-model="newEducation.endYear" class="cv-input" placeholder="2023" />
                </div>
              </div>
              <button type="button" class="cv-primary-button mt-4" @click="addEducation">
                <Plus :size="16" /> Add Education
              </button>
            </div>

            <div class="mt-7 flex justify-between border-t pt-4">
              <button type="button" class="cv-secondary-button" @click="previousStep">
                <ArrowLeft :size="16" /> Previous
              </button>
              <button type="button" class="cv-primary-button" @click="nextStep">
                Save & Continue <ArrowRight :size="16" />
              </button>
            </div>
          </section>

          <!-- EXPERIENCE -->
          <section
            v-else-if="currentStep === 3"
            class="rounded-2xl border border-orange-200 bg-white p-5 shadow-sm"
          >
            <div class="mb-5 flex items-center gap-3">
              <BriefcaseBusiness :size="24" class="text-primary" />
              <div>
                <h2 class="text-lg font-bold">Work Experience</h2>
                <p class="text-xs text-gray-500">Add your current and previous jobs.</p>
              </div>
            </div>

            <div class="space-y-3">
              <div v-for="item in experiences" :key="item.id" class="rounded-xl border p-4">
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <h3 class="text-sm font-bold">{{ item.jobTitle }}</h3>
                    <p class="mt-1 text-xs text-gray-500">
                      {{ item.company }}<span v-if="item.location"> · {{ item.location }}</span>
                    </p>
                    <p class="mt-1 text-xs text-gray-400">{{ item.startDate }} - {{ item.endDate }}</p>
                    <p v-if="item.description" class="mt-2 text-xs leading-5 text-gray-600">{{ item.description }}</p>
                  </div>
                  <button type="button" class="text-red-500" @click="removeExperience(item.id)">
                    <Trash2 :size="16" />
                  </button>
                </div>
              </div>
            </div>

            <div class="mt-5 rounded-xl bg-slate-50 p-4">
              <h3 class="mb-4 text-sm font-bold">Add Work Experience</h3>
              <div class="grid gap-4 md:grid-cols-2">
                <div>
                  <label class="cv-label">Job Title</label>
                  <input v-model="newExperience.jobTitle" class="cv-input" placeholder="Software Developer" />
                </div>
                <div>
                  <label class="cv-label">Company</label>
                  <input v-model="newExperience.company" class="cv-input" placeholder="Company name" />
                </div>
                <div>
                  <label class="cv-label">Location</label>
                  <input v-model="newExperience.location" class="cv-input" placeholder="Dar es Salaam" />
                </div>
                <div>
                  <label class="cv-label">Start Year</label>
                  <input v-model="newExperience.startDate" class="cv-input" placeholder="2020" />
                </div>
                <div>
                  <label class="cv-label">End Year</label>
                  <input v-model="newExperience.endDate" class="cv-input" placeholder="Present" />
                </div>
                <div class="md:col-span-2">
                  <label class="cv-label">Description</label>
                  <textarea v-model="newExperience.description" rows="3" class="cv-input resize-y" placeholder="Responsibilities and achievements..." />
                </div>
              </div>
              <button type="button" class="cv-primary-button mt-4" @click="addExperience">
                <Plus :size="16" /> Add Experience
              </button>
            </div>

            <div class="mt-7 flex justify-between border-t pt-4">
              <button type="button" class="cv-secondary-button" @click="previousStep">
                <ArrowLeft :size="16" /> Previous
              </button>
              <button type="button" class="cv-primary-button" @click="nextStep">
                Save & Continue <ArrowRight :size="16" />
              </button>
            </div>
          </section>

          <!-- SKILLS -->
          <section
            v-else-if="currentStep === 4"
            class="rounded-2xl border border-orange-200 bg-white p-5 shadow-sm"
          >
            <div class="mb-5 flex items-center gap-3">
              <Wrench :size="24" class="text-primary" />
              <div>
                <h2 class="text-lg font-bold">Skills</h2>
                <p class="text-xs text-gray-500">Add skills relevant to your career.</p>
              </div>
            </div>

            <div class="flex gap-2">
              <input
                v-model="newSkill"
                class="cv-input"
                placeholder="e.g. JavaScript"
                @keyup.enter="addSkill"
              />
              <button type="button" class="cv-primary-button" @click="addSkill">
                <Plus :size="16" /> Add
              </button>
            </div>

            <div class="mt-5">
              <p class="mb-3 text-sm font-semibold">Your Skills</p>
              <div v-if="skills.length" class="flex flex-wrap gap-2">
                <span
                  v-for="skill in skills"
                  :key="skill"
                  class="inline-flex items-center gap-2 rounded-lg bg-primary-50 px-3 py-2 text-xs font-medium text-primary"
                >
                  {{ skill }}
                  <button type="button" @click="removeSkill(skill)">
                    <X :size="13" />
                  </button>
                </span>
              </div>
              <p v-else class="rounded-lg bg-slate-50 p-4 text-center text-xs text-slate-500">
                No skills added yet.
              </p>
            </div>

            <p v-if="formError" class="mt-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600">{{ formError }}</p>

            <div class="mt-7 flex justify-between border-t pt-4">
              <button type="button" class="cv-secondary-button" @click="previousStep">
                <ArrowLeft :size="16" /> Previous
              </button>
              <AppButton :loading="saving" @click="saveCv">
                Save CV
              </AppButton>
            </div>
          </section>
        </div>

        <!-- RIGHT: CV PREVIEW -->
        <section class="rounded-2xl border bg-white p-4 shadow-sm cv-preview-panel">
          <div class="mb-4 flex flex-wrap items-center justify-between gap-2 no-print">
            <div>
              <h2 class="font-bold">CV Preview</h2>
              <p class="text-xs text-gray-500">{{ templateName }} Template</p>
            </div>
            <div class="flex flex-wrap gap-2">
              <button type="button" class="cv-primary-button text-xs" @click="downloadPdf">
                <Download :size="14" /> Download PDF
              </button>
            </div>
          </div>

          <!-- TEMPLATE 1: PROFESSIONAL -->
          <article v-if="previewKey === 'professional'" class="cv-paper cv-professional">
            <header class="professional-header">
              <h1>{{ form.fullName || 'Your Name' }}</h1>
              <p>{{ form.professionalTitle || 'Professional Title' }}</p>
            </header>

            <div class="professional-body">
              <aside class="professional-sidebar">
                <div class="cv-photo cv-photo-round">
                  <img v-if="form.photo" :src="form.photo" alt="Profile" />
                  <UserRound v-else :size="36" />
                </div>

                <h2>CONTACT</h2>
                <p>{{ form.phone }}</p>
                <p class="break-all">{{ form.email }}</p>
                <p>{{ form.location }}</p>

                <h2>SKILLS</h2>
                <ul>
                  <li v-for="skill in skills" :key="skill">{{ skill }}</li>
                </ul>
              </aside>

              <div class="cv-main">
                <section class="cv-section">
                  <h2>Professional Summary</h2>
                  <p>{{ form.summary }}</p>
                </section>

                <section class="cv-section">
                  <h2>Work Experience</h2>
                  <div v-for="item in experiences" :key="item.id" class="cv-entry">
                    <h3>{{ item.jobTitle }}</h3>
                    <p class="cv-meta">{{ item.company }} · {{ item.location }}</p>
                    <p class="cv-date">{{ item.startDate }} - {{ item.endDate }}</p>
                    <p>{{ item.description }}</p>
                  </div>
                </section>

                <section class="cv-section">
                  <h2>Education</h2>
                  <div v-for="item in education" :key="item.id" class="cv-entry">
                    <h3>{{ item.degree }}</h3>
                    <p>{{ item.institution }}</p>
                    <p class="cv-date">{{ item.startYear }} - {{ item.endYear }}</p>
                  </div>
                </section>
              </div>
            </div>
          </article>

          <!-- TEMPLATE 2: MODERN -->
          <article v-else-if="previewKey === 'modern'" class="cv-paper cv-modern">
            <div class="modern-body">
              <aside class="modern-sidebar">
                <div class="cv-photo cv-photo-round modern-photo">
                  <img v-if="form.photo" :src="form.photo" alt="Profile" />
                  <UserRound v-else :size="36" />
                </div>

                <h1>{{ form.fullName || 'Your Name' }}</h1>
                <p class="modern-title">{{ form.professionalTitle }}</p>

                <section>
                  <h2>CONTACT</h2>
                  <p>{{ form.email }}</p>
                  <p>{{ form.phone }}</p>
                  <p>{{ form.location }}</p>
                </section>

                <section>
                  <h2>SKILLS</h2>
                  <ul>
                    <li v-for="skill in skills" :key="skill">{{ skill }}</li>
                  </ul>
                </section>
              </aside>

              <div class="modern-main">
                <section class="cv-section">
                  <h2>Profile</h2>
                  <p>{{ form.summary }}</p>
                </section>

                <section class="cv-section">
                  <h2>Experience</h2>
                  <div v-for="item in experiences" :key="item.id" class="cv-entry">
                    <h3>{{ item.jobTitle }}</h3>
                    <p class="cv-meta">{{ item.company }} · {{ item.location }}</p>
                    <p class="cv-date">{{ item.startDate }} - {{ item.endDate }}</p>
                    <p>{{ item.description }}</p>
                  </div>
                </section>

                <section class="cv-section">
                  <h2>Education</h2>
                  <div v-for="item in education" :key="item.id" class="cv-entry">
                    <h3>{{ item.degree }}</h3>
                    <p>{{ item.institution }}</p>
                    <p class="cv-date">{{ item.startYear }} - {{ item.endYear }}</p>
                  </div>
                </section>
              </div>
            </div>
          </article>

          <!-- TEMPLATE 3: MINIMAL -->
          <article v-else-if="previewKey === 'minimal'" class="cv-paper cv-minimal">
            <header class="minimal-header">
              <div class="cv-photo minimal-photo">
                <img v-if="form.photo" :src="form.photo" alt="Profile" />
                <UserRound v-else :size="32" />
              </div>
              <h1>{{ form.fullName || 'Your Name' }}</h1>
              <p class="minimal-title">{{ form.professionalTitle }}</p>
              <p class="minimal-contact">
                {{ form.email }} · {{ form.phone }} · {{ form.location }}
              </p>
            </header>

            <section class="cv-section">
              <h2>Professional Summary</h2>
              <p>{{ form.summary }}</p>
            </section>

            <section class="cv-section">
              <h2>Work Experience</h2>
              <div v-for="item in experiences" :key="item.id" class="cv-entry">
                <div class="minimal-entry-heading">
                  <h3>{{ item.jobTitle }}</h3>
                  <span class="cv-date">{{ item.startDate }} - {{ item.endDate }}</span>
                </div>
                <p class="cv-meta">{{ item.company }} · {{ item.location }}</p>
                <p>{{ item.description }}</p>
              </div>
            </section>

            <section class="cv-section">
              <h2>Education</h2>
              <div v-for="item in education" :key="item.id" class="cv-entry">
                <div class="minimal-entry-heading">
                  <h3>{{ item.degree }}</h3>
                  <span class="cv-date">{{ item.startYear }} - {{ item.endYear }}</span>
                </div>
                <p>{{ item.institution }}</p>
              </div>
            </section>

            <section class="cv-section">
              <h2>Skills</h2>
              <div class="minimal-skills">
                <span v-for="skill in skills" :key="skill">{{ skill }}</span>
              </div>
            </section>
          </article>

          <!-- TEMPLATE 4: CREATIVE -->
          <article v-else-if="previewKey === 'creative'" class="cv-paper cv-creative">
            <header class="creative-header">
              <div class="cv-photo creative-photo">
                <img v-if="form.photo" :src="form.photo" alt="Profile" />
                <UserRound v-else :size="36" />
              </div>
              <div>
                <p class="creative-kicker">CURRICULUM VITAE</p>
                <h1>{{ form.fullName || 'Your Name' }}</h1>
                <p>{{ form.professionalTitle }}</p>
              </div>
            </header>

            <div class="creative-contact">
              <span>{{ form.email }}</span>
              <span>{{ form.phone }}</span>
              <span>{{ form.location }}</span>
            </div>

            <div class="creative-content">
              <section class="cv-section">
                <h2>About Me</h2>
                <p>{{ form.summary }}</p>
              </section>

              <section class="cv-section">
                <h2>Experience</h2>
                <div v-for="item in experiences" :key="item.id" class="creative-entry cv-entry">
                  <div class="creative-marker"></div>
                  <h3>{{ item.jobTitle }}</h3>
                  <p class="cv-meta">{{ item.company }} · {{ item.location }}</p>
                  <p class="cv-date">{{ item.startDate }} - {{ item.endDate }}</p>
                  <p>{{ item.description }}</p>
                </div>
              </section>

              <section class="cv-section">
                <h2>Education</h2>
                <div v-for="item in education" :key="item.id" class="cv-entry">
                  <h3>{{ item.degree }}</h3>
                  <p>{{ item.institution }}</p>
                  <p class="cv-date">{{ item.startYear }} - {{ item.endYear }}</p>
                </div>
              </section>

              <section class="cv-section">
                <h2>Skills</h2>
                <div class="creative-skills">
                  <span v-for="skill in skills" :key="skill">{{ skill }}</span>
                </div>
              </section>
            </div>
          </article>

          <!-- TEMPLATE 5: SIMPLE -->
          <article v-else class="cv-paper cv-simple">
            <header class="simple-header">
              <div class="cv-photo simple-photo">
                <img v-if="form.photo" :src="form.photo" alt="Profile" />
                <UserRound v-else :size="32" />
              </div>
              <h1>{{ form.fullName || 'Your Name' }}</h1>
              <p class="simple-title">{{ form.professionalTitle }}</p>
              <p>{{ form.email }} | {{ form.phone }} | {{ form.location }}</p>
            </header>

            <section class="cv-section">
              <h2>SUMMARY</h2>
              <p>{{ form.summary }}</p>
            </section>

            <section class="cv-section">
              <h2>WORK EXPERIENCE</h2>
              <div v-for="item in experiences" :key="item.id" class="cv-entry">
                <h3>{{ item.jobTitle }}</h3>
                <p class="cv-meta">{{ item.company }} · {{ item.location }}</p>
                <p class="cv-date">{{ item.startDate }} - {{ item.endDate }}</p>
                <p>{{ item.description }}</p>
              </div>
            </section>

            <section class="cv-section">
              <h2>EDUCATION</h2>
              <div v-for="item in education" :key="item.id" class="cv-entry">
                <h3>{{ item.degree }}</h3>
                <p>{{ item.institution }}</p>
                <p class="cv-date">{{ item.startYear }} - {{ item.endYear }}</p>
              </div>
            </section>

            <section class="cv-section">
              <h2>SKILLS</h2>
              <ul class="simple-skills">
                <li v-for="skill in skills" :key="skill">{{ skill }}</li>
              </ul>
            </section>
          </article>
        </section>
      </div>
    </div>
  </DashboardLayout>
</template>

<style>
.cv-builder-page {
  color: #334155;
}

.cv-input {
  width: 100%;
  border: 1px solid var(--color-border, #e2e8f0);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  background: white;
}

.cv-input:focus {
  border-color: #fd5c20;
  box-shadow: 0 0 0 2px rgb(253 92 32 / 10%);
}

.cv-label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 500;
}

.cv-primary-button,
.cv-secondary-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.cv-primary-button {
  background: #fd5c20;
  color: white;
}

.cv-primary-button:hover {
  background: #e94d14;
}

.cv-secondary-button {
  border: 1px solid #e2e8f0;
  background: white;
  color: #334155;
}

.cv-paper {
  width: 100%;
  max-width: 760px;
  min-height: 700px;
  margin: 0 auto;
  overflow: hidden;
  background: #fff;
  color: #334155;
  font-family: Arial, Helvetica, sans-serif;
  font-size: 11px;
  line-height: 1.55;
  box-shadow: 0 2px 8px rgb(15 23 42 / 8%);
}

.cv-paper h1,
.cv-paper h2,
.cv-paper h3,
.cv-paper p {
  margin-top: 0;
}

.cv-paper h1 {
  font-size: 23px;
  line-height: 1.2;
  font-weight: 800;
}

.cv-paper h2 {
  margin-bottom: 9px;
  font-size: 12px;
  font-weight: 800;
}

.cv-paper h3 {
  margin-bottom: 3px;
  font-size: 11px;
  font-weight: 700;
}

.cv-paper p {
  margin-bottom: 5px;
  overflow-wrap: anywhere;
}

.cv-photo {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: #f1f5f9;
  color: #94a3b8;
}

.cv-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cv-photo-round {
  width: 100px;
  height: 100px;
  border: 4px solid rgb(255 255 255 / 45%);
  border-radius: 50%;
}

.cv-main {
  min-width: 0;
  padding: 25px 20px;
}

.cv-section {
  margin-bottom: 23px;
  min-width: 0;
}

.cv-section > h2 {
  padding-bottom: 6px;
  border-bottom: 1px solid #cbd5e1;
  text-transform: uppercase;
  letter-spacing: 0.8px;
}

.cv-entry {
  margin-top: 13px;
  break-inside: avoid;
}

.cv-meta {
  color: #64748b;
  font-weight: 600;
}

.cv-date {
  color: #64748b;
  font-size: 10px;
}

/* 1. PROFESSIONAL */
.cv-professional {
  border-top: 8px solid #1d4ed8;
}

.professional-header {
  padding: 25px 24px 20px;
  background: #1d4ed8;
  color: white;
}

.professional-header h1 {
  margin-bottom: 5px;
}

.professional-header p {
  margin-bottom: 0;
  color: #dbeafe;
  font-size: 12px;
}

.professional-body {
  display: grid;
  grid-template-columns: 190px minmax(0, 1fr);
  min-height: 600px;
}

.professional-sidebar {
  padding: 22px 16px;
  background: #eff6ff;
  overflow-wrap: anywhere;
}

.professional-sidebar .cv-photo {
  margin: 0 auto 18px;
  border-color: #bfdbfe;
}

.professional-sidebar h2 {
  margin-top: 22px;
  margin-bottom: 8px;
  padding-bottom: 5px;
  border-bottom: 2px solid #93c5fd;
  color: #1d4ed8;
  letter-spacing: 0.7px;
}

.professional-sidebar p,
.professional-sidebar li {
  font-size: 10px;
  margin-bottom: 7px;
}

.professional-sidebar ul {
  padding-left: 14px;
  list-style: disc;
}

/* 2. MODERN */
.cv-modern {
  border-top: 7px solid #ea580c;
}

.modern-body {
  display: grid;
  grid-template-columns: 205px minmax(0, 1fr);
  min-height: 700px;
}

.modern-sidebar {
  padding: 24px 16px;
  background: #ea580c;
  color: white;
  overflow-wrap: anywhere;
}

.modern-photo {
  margin: 0 auto 18px;
  border-color: rgb(255 255 255 / 45%);
}

.modern-sidebar h1 {
  margin-bottom: 7px;
  text-align: center;
  font-size: 20px;
}

.modern-sidebar > p {
  text-align: center;
}

.modern-title {
  color: #ffedd5;
  font-size: 11px;
}

.modern-sidebar section {
  margin-top: 25px;
}

.modern-sidebar h2 {
  padding-bottom: 6px;
  border-bottom: 1px solid rgb(255 255 255 / 45%);
}

.modern-sidebar p,
.modern-sidebar li {
  margin-bottom: 7px;
  font-size: 10px;
}

.modern-sidebar ul {
  padding-left: 14px;
  list-style: disc;
}

.modern-main {
  min-width: 0;
  padding: 25px 20px;
}

.modern-main .cv-section > h2 {
  color: #ea580c;
  border-bottom: 2px solid #fdba74;
}

/* 3. MINIMAL */
.cv-minimal {
  padding: 35px 32px;
}

.minimal-header {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 2px solid #0f172a;
}

.minimal-photo {
  width: 72px;
  height: 72px;
  margin-bottom: 15px;
  border-radius: 4px;
}

.minimal-header h1 {
  margin-bottom: 5px;
  color: #0f172a;
  font-size: 27px;
}

.minimal-title {
  color: #475569;
  font-size: 13px;
}

.minimal-contact {
  margin-top: 10px;
  color: #64748b;
  font-size: 10px;
}

.cv-minimal .cv-section > h2 {
  color: #0f172a;
  border-bottom: 1px solid #cbd5e1;
}

.minimal-entry-heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}

.minimal-entry-heading h3 {
  min-width: 0;
}

.minimal-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 7px;
}

.minimal-skills span {
  border: 1px solid #cbd5e1;
  border-radius: 3px;
  padding: 4px 8px;
}

/* 4. CREATIVE */
.cv-creative {
  border: 1px solid #e9d5ff;
}

.creative-header {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 28px 25px;
  background: #6b21a8;
  color: white;
}

.creative-photo {
  width: 100px;
  height: 100px;
  border: 3px solid #d8b4fe;
  border-radius: 18px;
}

.creative-header h1 {
  margin-bottom: 5px;
}

.creative-header p {
  margin-bottom: 0;
  color: #f3e8ff;
}

.creative-kicker {
  margin-bottom: 8px !important;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 2px;
}

.creative-contact {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 15px;
  padding: 12px 25px;
  background: #f3e8ff;
  color: #581c87;
  font-size: 10px;
  overflow-wrap: anywhere;
}

.creative-content {
  padding: 25px;
}

.cv-creative .cv-section > h2 {
  color: #6b21a8;
  border-bottom: 2px solid #d8b4fe;
}

.creative-entry {
  position: relative;
  padding-left: 13px;
  border-left: 2px solid #d8b4fe;
}

.creative-marker {
  position: absolute;
  top: 2px;
  left: -5px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #7e22ce;
}

.creative-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 7px;
}

.creative-skills span {
  border-radius: 5px;
  padding: 5px 8px;
  background: #f3e8ff;
  color: #6b21a8;
  font-size: 10px;
}

/* 5. SIMPLE */
.cv-simple {
  padding: 32px;
}

.simple-header {
  margin-bottom: 27px;
  padding-bottom: 18px;
  border-bottom: 2px solid #334155;
  text-align: center;
}

.simple-photo {
  width: 75px;
  height: 75px;
  margin: 0 auto 12px;
  border-radius: 50%;
}

.simple-header h1 {
  margin-bottom: 5px;
  color: #0f172a;
}

.simple-title {
  margin-bottom: 8px !important;
  font-size: 12px;
  font-weight: 600;
}

.simple-header > p:last-child {
  color: #64748b;
  font-size: 10px;
}

.cv-simple .cv-section > h2 {
  color: #334155;
  border-bottom: 1px solid #94a3b8;
  letter-spacing: 0.5px;
}

.simple-skills {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 18px;
  padding-left: 16px;
  list-style: disc;
}

/* PRINT */
@media print {
  @page {
    size: A4;
    margin: 10mm;
  }

  body {
    background: white !important;
  }

  body * {
    visibility: hidden;
  }

  .cv-preview-panel,
  .cv-preview-panel * {
    visibility: visible;
  }

  .cv-preview-panel {
    position: absolute !important;
    inset: 0 auto auto 0 !important;
    width: 100% !important;
    max-width: none !important;
    padding: 0 !important;
    margin: 0 !important;
    border: none !important;
    box-shadow: none !important;
  }

  .cv-preview-panel .no-print {
    display: none !important;
  }

  .cv-paper {
    width: 100% !important;
    max-width: none !important;
    min-height: 0 !important;
    margin: 0 !important;
    box-shadow: none !important;
    print-color-adjust: exact !important;
    -webkit-print-color-adjust: exact !important;
  }

  .cv-entry,
  .cv-section,
  .professional-body,
  .modern-body {
    break-inside: avoid;
  }
}
</style>
