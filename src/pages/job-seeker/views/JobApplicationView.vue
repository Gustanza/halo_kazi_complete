<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  FileText,
  CheckCircle2,
  Circle,
  X,
  ChevronDown,
  BriefcaseBusiness,
  AlertCircle,
  ArrowLeft,
  MapPin,
  Tag,
  Clock3,
  UploadCloud,
  FileUp,
  Info,
} from 'lucide-vue-next'

import DashboardLayout from '../components/layout/DashboardLayout.vue'

const route = useRoute()
const router = useRouter()

const jobId = route.params.jobId || '2'

// Form inputs state matching the visual design
const form = ref({
  fullName: 'Asha Ndalichako',
  email: 'asha.ndalichako@gmail.com',
  phone: '+255 712 345 678',
  location: 'Dar es Salaam, Tanzania',
  coverLetter: '',
})

// Job data matching sidebar info
const job = {
  title: 'Software Developer',
  company: 'Apex Solutions Ltd',
  companyBadge: 'Technology • 11-50 employees',
  logo: 'A',
  logoBg: '#ffe5d8',
  logoColor: '#ff5a0a',
  location: 'Dar es Salaam',
  experience: '1-3 years',
  category: 'IT & Software',
  salary: 'TSh 1,200,000 - 1,800,000 / month',
  description: 'We are looking for a skilled Software Developer to join our dynamic team. You will be responsible for developing and maintaining web applications, working with modern technologies, and collaborating with cross-functional teams to deliver high-quality solutions.',
  requirements: [
    "Bachelor's degree in Computer Science or related field",
    'At least 1 year of relevant experience',
    'Proficiency in JavaScript, React, or similar frameworks',
    'Good problem-solving and communication skills',
  ],
  deadline: '30 October 2026',
}

// Saved CVs list
const savedCVs = ref([
  {
    id: 1,
    name: 'My Professional CV',
    fileName: 'my-professional-cv.pdf',
    updatedAt: '20 October 2026',
    isDefault: true
  },
  {
    id: 2,
    name: 'Software Developer CV',
    fileName: 'software-developer-cv.pdf',
    updatedAt: '18 October 2026',
    isDefault: false
  },
  {
    id: 3,
    name: 'General CV',
    fileName: 'general-cv.pdf',
    updatedAt: '15 October 2026',
    isDefault: false
  }
])

// Automatically select the default CV.
const selectedCvId = ref(
  savedCVs.value.find(cv => cv.isDefault)?.id ?? null
)

// Controls whether the CV pop-up is visible.
const showCvModal = ref(false)

// Temporarily store the CV selected inside the pop-up.
const pendingCvId = ref(null)

const selectedCv = computed(() =>
  savedCVs.value.find(cv => cv.id === selectedCvId.value) ?? null
)

const pendingCv = computed(() =>
  savedCVs.value.find(cv => cv.id === pendingCvId.value) ?? null
)

const errorMessage = ref('')

function openCvModal() {
  pendingCvId.value = selectedCvId.value
  errorMessage.value = ''
  showCvModal.value = true
}

function closeCvModal() {
  showCvModal.value = false
}

function confirmCvSelection() {
  if (!pendingCvId.value) {
    errorMessage.value = 'Please select a CV to continue.'
    return
  }

  selectedCvId.value = pendingCvId.value
  showCvModal.value = false
  errorMessage.value = ''
}

function submitApplication() {
  if (!selectedCv.value) {
    errorMessage.value =
      'Please create or select a CV before submitting your application.'
    return
  }

  // Navigation to success confirmation page
  router.push({
    path: '/dashboard/application-success',
    query: {
      applicationId: `DEMO-${jobId}`,
      jobTitle: job.title
    }
  })
}
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-7xl space-y-4 px-4 py-6 sm:px-6">

      <!-- Back navigation -->
      <div>
        <button
          type="button"
          class="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 transition hover:text-slate-800 cursor-pointer"
          @click="router.push('/dashboard/find-jobs')"
        >
          <ArrowLeft :size="15" /> Back to job details
        </button>
      </div>

      <!-- Application heading -->
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900">
          Apply for Job
        </h1>
        <p class="mt-0.5 text-xs text-slate-500">
          Complete the form below to submit your application for this position.
        </p>
      </div>

      <!-- Main 2-Column Grid Layout -->
      <div class="grid gap-6 lg:grid-cols-[1fr_400px]">

        <!-- Left Column: Application Form Card -->
        <div class="rounded-2xl border bg-white p-6 shadow-sm space-y-6" style="border-color:#ffd3bd">
          
          <!-- Form Header & Step Indicator -->
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-5">
            <h2 class="text-base font-bold text-slate-900">
              Application Form
            </h2>

            <div class="flex items-center gap-3 text-xs font-semibold">
              <div class="flex items-center gap-1.5 text-orange-600 font-bold">
                <span class="flex h-6 w-6 items-center justify-center rounded-full bg-orange-600 text-white text-[11px] font-bold shadow-sm">1</span>
                Application
              </div>
              <span class="h-px w-8 bg-slate-200"></span>
              <div class="flex items-center gap-1.5 text-slate-400">
                <span class="flex h-6 w-6 items-center justify-center rounded-full bg-slate-200 text-slate-600 text-[11px] font-bold">2</span>
                Confirmation
              </div>
            </div>
          </div>

          <!-- Form Fields -->
          <form class="space-y-4" @submit.prevent="submitApplication">
            
            <!-- Full Name & Email -->
            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">
                  Full Name <span class="text-orange-600">*</span>
                </label>
                <input
                  v-model="form.fullName"
                  type="text"
                  required
                  class="w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-xs text-slate-900 outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
                />
              </div>

              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">
                  Email Address <span class="text-orange-600">*</span>
                </label>
                <input
                  v-model="form.email"
                  type="email"
                  required
                  class="w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-xs text-slate-900 outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
                />
              </div>
            </div>

            <!-- Phone Number & Location -->
            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">
                  Phone Number <span class="text-orange-600">*</span>
                </label>
                <input
                  v-model="form.phone"
                  type="text"
                  required
                  class="w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-xs text-slate-900 outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100"
                />
              </div>

              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">
                  Location
                </label>
                <div class="relative">
                  <select
                    v-model="form.location"
                    class="w-full rounded-xl border border-slate-200 bg-white px-3.5 py-2.5 text-xs text-slate-900 outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100 appearance-none cursor-pointer"
                  >
                    <option>Dar es Salaam, Tanzania</option>
                    <option>Arusha, Tanzania</option>
                    <option>Mwanza, Tanzania</option>
                    <option>Zanzibar, Tanzania</option>
                  </select>
                  <MapPin :size="15" class="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                </div>
              </div>
            </div>

            <!-- Cover Letter -->
            <div class="space-y-1.5">
              <label class="block text-xs font-bold text-slate-800">
                Cover Letter <span class="font-normal text-slate-400">(Optional)</span>
              </label>
              <div class="relative">
                <textarea
                  v-model="form.coverLetter"
                  rows="4"
                  maxlength="1000"
                  placeholder="Tell the employer why you are a great fit for this role..."
                  class="w-full rounded-xl border border-slate-200 bg-white p-3 text-xs text-slate-900 outline-none transition focus:border-orange-500 focus:ring-2 focus:ring-orange-100 resize-none"
                ></textarea>
                <span class="absolute bottom-2.5 right-3 text-[10px] text-slate-400">
                  {{ form.coverLetter.length }}/1000
                </span>
              </div>
            </div>

            <!-- CV and Additional Documents Upload Grid -->
            <div class="grid gap-4 sm:grid-cols-2 pt-2">
              
              <!-- Upload CV (Triggers CV selection modal) -->
              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">
                  Upload CV <span class="text-orange-600">*</span>
                </label>
                <div
                  type="button"
                  class="flex flex-col items-center justify-center rounded-2xl border-2 border-dashed border-slate-200 bg-slate-50/50 p-6 text-center cursor-pointer transition hover:border-orange-400 hover:bg-orange-50/30"
                  @click="openCvModal"
                >
                  <UploadCloud :size="24" class="text-slate-400 mb-1" />
                  <template v-if="selectedCv">
                    <span class="text-xs font-semibold text-orange-600 truncate max-w-full">{{ selectedCv.name }}</span>
                    <span class="text-[10px] text-slate-500 mt-0.5">Click to change CV</span>
                  </template>
                  <template v-else>
                    <span class="text-xs font-semibold text-slate-700">Click to choose or change CV</span>
                    <span class="text-[10px] text-slate-400 mt-0.5">PDF, DOC, DOCX (Max 5MB)</span>
                  </template>
                </div>
              </div>

              <!-- Additional Documents -->
              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">
                  Additional Documents <span class="font-normal text-slate-400">(Optional)</span>
                </label>
                <label class="flex flex-col items-center justify-center rounded-2xl border-2 border-dashed border-slate-200 bg-slate-50/50 p-6 text-center cursor-pointer transition hover:border-orange-400 hover:bg-orange-50/30">
                  <FileUp :size="24" class="text-slate-400 mb-1" />
                  <span class="text-xs font-semibold text-slate-700">Add supporting documents</span>
                  <span class="text-[10px] text-slate-400 mt-0.5">Certificates, portfolio, etc. (Max 5MB)</span>
                  <input type="file" class="hidden" />
                </label>
              </div>

            </div>

            <!-- Submission error -->
            <div
              v-if="errorMessage"
              class="flex items-start gap-2 rounded-xl bg-red-50 p-3 text-xs text-red-700 border border-red-200"
            >
              <AlertCircle :size="16" class="mt-0.5 shrink-0" />
              <p>{{ errorMessage }}</p>
            </div>

            <!-- Notice banner -->
            <div class="flex items-center gap-2.5 rounded-xl border border-blue-100 bg-blue-50/60 p-3 text-xs text-blue-800">
              <Info :size="16" class="shrink-0 text-blue-500" />
              <span>Your application will be sent to the employer once you submit. You can track its status in your Applications section.</span>
            </div>

            <!-- Action buttons -->
            <div class="flex items-center justify-between pt-4 border-t border-slate-100">
              <button
                type="button"
                class="rounded-xl px-5 py-2.5 text-xs font-semibold text-slate-600 hover:bg-slate-100 transition cursor-pointer"
                @click="router.push('/dashboard/find-jobs')"
              >
                Cancel
              </button>

              <button
                type="submit"
                class="inline-flex items-center gap-2 rounded-xl bg-orange-600 px-6 py-2.5 text-xs font-semibold text-white shadow-sm transition hover:bg-orange-700 cursor-pointer"
                style="background-color: #ff5a0a"
              >
                Submit Application
              </button>
            </div>

          </form>

        </div>

        <!-- Right Column: Job Summary Sidebar -->
        <div class="space-y-4">
          <div class="rounded-2xl border bg-white p-5 shadow-sm space-y-4" style="border-color:#ffd3bd">
            
            <!-- Company Info Header -->
            <div class="flex items-center gap-3 border-b border-slate-100 pb-4">
              <div
                class="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl text-base font-bold shadow-sm"
                :style="{ backgroundColor: job.logoBg, color: job.logoColor }"
              >
                {{ job.logo }}
              </div>
              <div class="min-w-0">
                <div class="flex items-center gap-1.5">
                  <h3 class="text-xs font-bold text-slate-900 truncate">
                    {{ job.company }}
                  </h3>
                  <span class="text-blue-500 text-xs">✓</span>
                </div>
                <p class="text-[10px] text-slate-400">
                  {{ job.companyBadge }}
                </p>
              </div>
            </div>

            <!-- Job Title & Meta -->
            <div>
              <h2 class="text-base font-extrabold text-slate-900">
                {{ job.title }}
              </h2>

              <div class="mt-2.5 flex flex-wrap gap-x-3 gap-y-1.5 text-xs text-slate-500">
                <span class="inline-flex items-center gap-1">
                  <MapPin :size="13" class="text-slate-400" /> {{ job.location }}
                </span>
                <span class="inline-flex items-center gap-1">
                  <BriefcaseBusiness :size="13" class="text-slate-400" /> {{ job.experience }}
                </span>
                <span class="inline-flex items-center gap-1">
                  <Tag :size="13" class="text-slate-400" /> {{ job.category }}
                </span>
              </div>

              <p class="mt-3 text-xs font-bold text-slate-900">
                {{ job.salary }}
              </p>
            </div>

            <!-- Job Description -->
            <div class="border-t border-slate-100 pt-4 space-y-1.5">
              <h4 class="text-xs font-bold text-slate-900">Job Description</h4>
              <p class="text-xs leading-relaxed text-slate-600">
                {{ job.description }}
              </p>
            </div>

            <!-- Requirements -->
            <div class="border-t border-slate-100 pt-4 space-y-2">
              <h4 class="text-xs font-bold text-slate-900">Requirements</h4>
              <ul class="list-disc pl-4 space-y-1 text-xs text-slate-600">
                <li v-for="(req, idx) in job.requirements" :key="idx">
                  {{ req }}
                </li>
              </ul>
            </div>

            <!-- Deadline Box -->
            <div class="rounded-xl border border-slate-100 bg-slate-50 p-3.5 flex items-center gap-3">
              <Clock3 :size="18" class="text-slate-400 shrink-0" />
              <div>
                <p class="text-[10px] font-medium text-slate-400 uppercase tracking-wider">Deadline</p>
                <p class="text-xs font-bold text-slate-800">{{ job.deadline }}</p>
              </div>
            </div>

          </div>
        </div>

      </div>

    </div>

    <!-- CV SELECTION POP-UP -->
    <Teleport to="body">
      <div
        v-if="showCvModal"
        class="fixed inset-0 z-[100] flex items-center justify-center overflow-y-auto bg-black/50 p-4"
        @click.self="closeCvModal"
        @keydown.esc="closeCvModal"
      >
        <section
          class="my-auto w-full max-w-lg rounded-2xl bg-white shadow-2xl"
          role="dialog"
          aria-modal="true"
          aria-labelledby="cv-modal-title"
        >
          <!-- Modal heading -->
          <div class="flex items-start justify-between border-b border-gray-200 p-5 sm:p-6">
            <div>
              <h2
                id="cv-modal-title"
                class="text-xl font-bold text-gray-900"
              >
                Choose a CV
              </h2>

              <p class="mt-1 text-sm text-gray-500">
                Select the CV you want to send to the employer.
              </p>
            </div>

            <button
              type="button"
              aria-label="Close CV selection"
              class="rounded-lg p-2 text-gray-500 hover:bg-gray-100 hover:text-gray-900 cursor-pointer"
              @click="closeCvModal"
            >
              <X :size="21" />
            </button>
          </div>

          <!-- Saved CV list -->
          <div class="space-y-3 p-5 sm:p-6">
            <div
              v-if="savedCVs.length === 0"
              class="rounded-lg bg-gray-50 p-5 text-center"
            >
              <FileText
                :size="32"
                class="mx-auto text-gray-400"
              />

              <p class="mt-3 font-medium text-gray-900">
                No saved CVs found
              </p>

              <p class="mt-1 text-sm text-gray-500">
                Create a CV in your profile before applying for this job.
              </p>
            </div>

            <button
              v-for="cv in savedCVs"
              :key="cv.id"
              type="button"
              class="flex w-full items-center gap-3 rounded-xl border p-4 text-left transition cursor-pointer"
              :class="
                pendingCvId === cv.id
                  ? 'border-orange-500 bg-orange-50 ring-1 ring-orange-500'
                  : 'border-gray-200 hover:border-orange-300'
              "
              @click="pendingCvId = cv.id"
            >
              <div class="rounded-lg bg-gray-100 p-3">
                <FileText :size="23" class="text-gray-600" />
              </div>

              <div class="min-w-0 flex-1">
                <p class="truncate font-semibold text-gray-900">
                  {{ cv.name }}
                </p>

                <p class="mt-1 truncate text-sm text-gray-500">
                  {{ cv.fileName }}
                </p>

                <span
                  v-if="cv.isDefault"
                  class="mt-2 inline-flex rounded-full bg-green-100 px-2 py-1 text-xs font-medium text-green-700"
                >
                  Default CV
                </span>
              </div>

              <CheckCircle2
                v-if="pendingCvId === cv.id"
                :size="22"
                class="shrink-0 text-orange-600"
              />

              <Circle
                v-else
                :size="22"
                class="shrink-0 text-gray-300"
              />
            </button>
          </div>

          <!-- Modal actions -->
          <div class="flex flex-col-reverse gap-3 border-t border-gray-200 p-5 sm:flex-row sm:justify-end sm:p-6">
            <button
              type="button"
              class="rounded-lg border border-gray-300 px-5 py-3 text-sm font-semibold text-gray-700 hover:bg-gray-50 cursor-pointer"
              @click="closeCvModal"
            >
              Cancel
            </button>

            <button
              type="button"
              class="rounded-lg px-5 py-3 text-sm font-semibold text-white transition hover:bg-orange-600 disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer"
              style="background-color: #ff5a0a"
              :disabled="!pendingCv"
              @click="confirmCvSelection"
            >
              Use Selected CV
            </button>
          </div>
        </section>
      </div>
    </Teleport>
  </DashboardLayout>
</template>