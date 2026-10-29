<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  FileText,
  CheckCircle2,
  Circle,
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
import AppModal from '../../../components/ui/AppModal.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import * as jobsApi from '../../../api/jobs'
import * as cvApi from '../../../api/cv'
import * as applicationsApi from '../../../api/applications'
import { useSession } from '../../../auth/session'
import { ApiRequestError } from '../../../api/http'

const route = useRoute()
const router = useRouter()
const session = useSession()

const jobId = route.params.jobId

const loadingJob = ref(true)
const job = ref(null)

const form = ref({
  coverLetter: '',
})

const savedCVs = ref([])
const loadingCvs = ref(true)
const selectedCvId = ref(null)
const showCvModal = ref(false)
const pendingCvId = ref(null)
const errorMessage = ref('')
const submitting = ref(false)

const selectedCv = computed(() => savedCVs.value.find((cv) => cv.id === selectedCvId.value) ?? null)
const pendingCv = computed(() => savedCVs.value.find((cv) => cv.id === pendingCvId.value) ?? null)

async function loadJob() {
  loadingJob.value = true
  try {
    job.value = await jobsApi.getJob(jobId)
  } catch {
    job.value = null
  } finally {
    loadingJob.value = false
  }
}

async function loadCvs() {
  loadingCvs.value = true
  try {
    savedCVs.value = await cvApi.listCvs()
    selectedCvId.value = savedCVs.value.find((cv) => cv.isLastUsed)?.id ?? savedCVs.value[0]?.id ?? null
  } finally {
    loadingCvs.value = false
  }
}

onMounted(() => {
  loadJob()
  loadCvs()
})

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

async function submitApplication() {
  errorMessage.value = ''
  if (!selectedCv.value) {
    errorMessage.value = 'Please create or select a CV before submitting your application.'
    return
  }
  if (!form.value.coverLetter.trim()) {
    errorMessage.value = 'Please write a short cover letter before submitting.'
    return
  }

  submitting.value = true
  try {
    const application = await applicationsApi.createApplication({
      jobId,
      cvId: selectedCv.value.id,
      coverLetter: form.value.coverLetter,
    })
    router.push({
      path: '/dashboard/application-success',
      query: {
        applicationId: application.id,
        jobTitle: application.jobTitle,
        company: application.company,
      },
    })
  } catch (error) {
    errorMessage.value = error instanceof ApiRequestError ? error.message : 'Could not submit your application. Please try again.'
  } finally {
    submitting.value = false
  }
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

      <Skeleton v-if="loadingJob" variant="card" height="20rem" />

      <EmptyState
        v-else-if="!job"
        title="Job not found"
        description="This job may have been closed or removed."
      />

      <!-- Main 2-Column Grid Layout -->
      <div v-else class="grid gap-6 lg:grid-cols-[1fr_400px]">

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

            <!-- Applicant Info (read-only, from the signed-in account) -->
            <div class="grid gap-4 sm:grid-cols-2">
              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">Full Name</label>
                <input
                  :value="session.user.value?.fullName"
                  type="text"
                  disabled
                  class="w-full rounded-xl border border-slate-200 bg-slate-50 px-3.5 py-2.5 text-xs text-slate-500"
                />
              </div>

              <div class="space-y-1.5">
                <label class="block text-xs font-bold text-slate-800">Phone Number</label>
                <input
                  :value="session.user.value?.phone"
                  type="tel"
                  disabled
                  class="w-full rounded-xl border border-slate-200 bg-slate-50 px-3.5 py-2.5 text-xs text-slate-500"
                />
              </div>
            </div>

            <!-- Cover Letter -->
            <div class="space-y-1.5">
              <label class="block text-xs font-bold text-slate-800">
                Cover Letter <span class="text-orange-600">*</span>
              </label>
              <div class="relative">
                <textarea
                  v-model="form.coverLetter"
                  rows="4"
                  maxlength="1000"
                  required
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
                  CV <span class="text-orange-600">*</span>
                </label>
                <div
                  class="flex flex-col items-center justify-center rounded-2xl border-2 border-dashed border-slate-200 bg-slate-50/50 p-6 text-center cursor-pointer transition hover:border-orange-400 hover:bg-orange-50/30"
                  @click="openCvModal"
                >
                  <UploadCloud :size="24" class="text-slate-400 mb-1" />
                  <template v-if="selectedCv">
                    <span class="text-xs font-semibold text-orange-600 truncate max-w-full">{{ selectedCv.cvName }}</span>
                    <span class="text-[10px] text-slate-500 mt-0.5">Click to change CV</span>
                  </template>
                  <template v-else>
                    <span class="text-xs font-semibold text-slate-700">Click to choose a CV</span>
                    <span class="text-[10px] text-slate-400 mt-0.5">Create one first in CV Builder if you don't have one</span>
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
                :disabled="submitting"
                class="inline-flex items-center gap-2 rounded-xl bg-orange-600 px-6 py-2.5 text-xs font-semibold text-white shadow-sm transition hover:bg-orange-700 cursor-pointer disabled:cursor-not-allowed disabled:opacity-60"
                style="background-color: #ff5a0a"
              >
                {{ submitting ? 'Submitting…' : 'Submit Application' }}
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
                :style="{ backgroundColor: job.companyColor + '1a', color: job.companyColor }"
              >
                {{ job.companyInitials }}
              </div>
              <div class="min-w-0">
                <h3 class="text-xs font-bold text-slate-900 truncate">
                  {{ job.company }}
                </h3>
                <p class="text-[10px] text-slate-400">
                  {{ job.category }}
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
                  <BriefcaseBusiness :size="13" class="text-slate-400" /> {{ job.employmentType }}
                </span>
                <span class="inline-flex items-center gap-1">
                  <Tag :size="13" class="text-slate-400" /> {{ job.category }}
                </span>
              </div>

              <p class="mt-3 text-xs font-bold text-slate-900">
                {{ job.salaryCurrency }} {{ job.salaryMin.toLocaleString() }} - {{ job.salaryMax.toLocaleString() }} / month
              </p>
            </div>

            <!-- Job Description -->
            <div class="border-t border-slate-100 pt-4 space-y-1.5">
              <h4 class="text-xs font-bold text-slate-900">Job Description</h4>
              <p class="text-xs leading-relaxed text-slate-600">
                {{ job.description }}
              </p>
            </div>

            <!-- Tags -->
            <div class="border-t border-slate-100 pt-4 space-y-2">
              <h4 class="text-xs font-bold text-slate-900">Skills</h4>
              <div class="flex flex-wrap gap-2">
                <span v-for="tag in job.tags" :key="tag" class="rounded-full bg-slate-100 px-2.5 py-1 text-[10px] text-slate-600">{{ tag }}</span>
              </div>
            </div>

            <!-- Posted date -->
            <div class="rounded-xl border border-slate-100 bg-slate-50 p-3.5 flex items-center gap-3">
              <Clock3 :size="18" class="text-slate-400 shrink-0" />
              <div>
                <p class="text-[10px] font-medium text-slate-400 uppercase tracking-wider">Posted</p>
                <p class="text-xs font-bold text-slate-800">{{ new Date(job.postedAt).toLocaleDateString() }}</p>
              </div>
            </div>

          </div>
        </div>

      </div>

    </div>

    <!-- CV SELECTION MODAL -->
    <AppModal :open="showCvModal" title="Choose a CV" size="md" @close="closeCvModal">
      <p class="text-sm" style="color: var(--color-text-muted)">Select the CV you want to send to the employer.</p>

      <div class="mt-4 space-y-3">
        <Skeleton v-if="loadingCvs" variant="card" height="4rem" />

        <EmptyState
          v-else-if="!savedCVs.length"
          :icon="FileText"
          title="No saved CVs found"
          description="Create a CV in CV Builder before applying for this job."
        />

        <button
          v-for="cv in savedCVs"
          :key="cv.id"
          type="button"
          class="flex w-full items-center gap-3 rounded-xl border p-4 text-left transition cursor-pointer"
          :class="pendingCvId === cv.id ? 'border-primary' : ''"
          :style="pendingCvId === cv.id ? 'background-color: var(--color-peach-light)' : 'border-color: var(--color-border)'"
          @click="pendingCvId = cv.id"
        >
          <div class="rounded-lg p-3" style="background-color: var(--color-bg)">
            <FileText :size="23" style="color: var(--color-text-muted)" />
          </div>

          <div class="min-w-0 flex-1">
            <p class="truncate font-semibold" style="color: var(--color-text)">{{ cv.cvName }}</p>
            <p class="mt-1 truncate text-sm" style="color: var(--color-text-muted)">{{ cv.headline }}</p>
            <span v-if="cv.isLastUsed" class="mt-2 inline-flex rounded-full bg-emerald-100 px-2 py-1 text-xs font-medium text-emerald-700">
              Last used
            </span>
          </div>

          <CheckCircle2 v-if="pendingCvId === cv.id" :size="22" class="shrink-0 text-primary" />
          <Circle v-else :size="22" class="shrink-0" style="color: var(--color-border)" />
        </button>
      </div>

      <div class="mt-6 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
        <button type="button" class="btn-outline" @click="closeCvModal">Cancel</button>
        <button type="button" class="btn-primary disabled:cursor-not-allowed disabled:opacity-50" :disabled="!pendingCv" @click="confirmCvSelection">
          Use Selected CV
        </button>
      </div>
    </AppModal>
  </DashboardLayout>
</template>
