<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  MapPin,
  CalendarDays,
  BriefcaseBusiness,
  Building2,
  CheckCircle2,
  Clock3,
  XCircle,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import StatusBadge from '../../../components/ui/StatusBadge.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as applicationsApi from '../../../api/applications'
import * as jobsApi from '../../../api/jobs'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const notFound = ref(false)
const application = ref(null)
const job = ref(null)

const NEXT_STEP_FALLBACK = {
  SUBMITTED: 'Awaiting response from the employer.',
  IN_REVIEW: 'Your application is being reviewed.',
  SHORTLISTED: 'You have been shortlisted — watch for next steps.',
  REJECTED: 'Not selected for this role.',
}

const STATUS_ICON = {
  SUBMITTED: Clock3,
  IN_REVIEW: CalendarDays,
  SHORTLISTED: CheckCircle2,
  REJECTED: XCircle,
}

function statusIcon(status) {
  return STATUS_ICON[status] ?? Clock3
}

function nextStepLabel(app) {
  return app.statusRemarks || NEXT_STEP_FALLBACK[app.status] || ''
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

function formatSalary(j) {
  if (!j?.salaryMin && !j?.salaryMax) return 'Not disclosed'
  return `${j.salaryCurrency} ${j.salaryMin.toLocaleString()} - ${j.salaryMax.toLocaleString()}`
}

async function load() {
  const id = route.query.application
  if (!id) {
    router.push('/dashboard/applications')
    return
  }
  loading.value = true
  notFound.value = false
  try {
    application.value = await applicationsApi.getApplication(id)
    job.value = await jobsApi.getJob(application.value.jobId).catch(() => null)
  } catch {
    notFound.value = true
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/dashboard/applications')
}

watch(() => route.query.application, load)
onMounted(load)
</script>

<template>
  <DashboardLayout>
    <div class="space-y-3">
      <!-- Header -->
      <section class="card-surface px-5 py-4">
        <button
          type="button"
          class="mb-3 inline-flex items-center gap-2 text-sm font-medium transition hover:text-primary"
          style="color: var(--color-text-muted)"
          @click="goBack"
        >
          <ArrowLeft :size="17" />
          Back to Applications
        </button>
        <h1 class="text-2xl font-extrabold sm:text-3xl" style="color: var(--color-text)">View Application</h1>
        <p class="mt-1 text-sm" style="color: var(--color-text-muted)">View details of your selected job application.</p>
      </section>

      <!-- Loading -->
      <template v-if="loading">
        <Skeleton variant="card" height="8rem" />
        <Skeleton variant="card" height="14rem" />
      </template>

      <!-- Not found -->
      <section v-else-if="notFound || !application" class="card-surface p-10 text-center">
        <h2 class="text-lg font-bold" style="color: var(--color-text)">Application not found</h2>
        <p class="mt-2 text-sm" style="color: var(--color-text-muted)">The selected application could not be found.</p>
        <button type="button" class="btn-primary mt-5" @click="goBack">Back to Applications</button>
      </section>

      <!-- Selected application -->
      <template v-else>
        <!-- Application header card -->
        <section class="card-surface p-5">
          <div class="flex flex-col gap-5 sm:flex-row sm:items-center">
            <div
              class="flex h-20 w-20 shrink-0 items-center justify-center rounded-xl text-xl font-extrabold"
              :style="{ backgroundColor: application.companyColor + '1a', color: application.companyColor }"
            >
              {{ application.companyInitials }}
            </div>

            <div class="min-w-0 flex-1">
              <h2 class="break-words text-2xl font-extrabold" style="color: var(--color-text)">{{ application.jobTitle }}</h2>
              <div class="mt-1 flex flex-wrap items-center gap-2">
                <Building2 :size="17" style="color: var(--color-text-muted)" />
                <span class="font-semibold" style="color: var(--color-text)">{{ application.company }}</span>
              </div>
              <div class="mt-3 flex flex-wrap gap-x-6 gap-y-2 text-sm" style="color: var(--color-text-muted)">
                <span class="inline-flex items-center gap-2"><MapPin :size="16" /> {{ application.location }}</span>
                <span class="inline-flex items-center gap-2"><CalendarDays :size="16" /> Applied on {{ formatDate(application.submittedAt) }}</span>
              </div>
            </div>

            <div class="sm:self-start">
              <StatusBadge :status="application.status" kind="application" />
            </div>
          </div>
        </section>

        <!-- Job info -->
        <section v-if="job" class="grid grid-cols-1 gap-3 md:grid-cols-2">
          <div class="card-surface p-4">
            <div class="flex items-center gap-3">
              <div class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-50 text-primary dark:bg-white/10">
                <BriefcaseBusiness :size="19" />
              </div>
              <div>
                <p class="text-xs" style="color: var(--color-text-muted)">Job Type</p>
                <p class="mt-0.5 text-sm font-semibold" style="color: var(--color-text)">{{ job.employmentType }}</p>
              </div>
            </div>
          </div>

          <div class="card-surface p-4">
            <div class="flex items-center gap-3">
              <div class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-50 text-primary dark:bg-white/10">
                <Building2 :size="19" />
              </div>
              <div>
                <p class="text-xs" style="color: var(--color-text-muted)">Salary</p>
                <p class="mt-0.5 text-sm font-semibold" style="color: var(--color-text)">{{ formatSalary(job) }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- Details -->
        <section class="grid grid-cols-1 gap-3 lg:grid-cols-3">
          <div class="card-surface p-5 lg:col-span-2">
            <h3 class="text-lg font-bold" style="color: var(--color-text)">Cover Letter</h3>
            <p class="mt-3 text-sm leading-7" style="color: var(--color-text-muted)">{{ application.coverLetter }}</p>

            <template v-if="job">
              <h3 class="mt-7 text-lg font-bold" style="color: var(--color-text)">Job Description</h3>
              <p class="mt-3 text-sm leading-7" style="color: var(--color-text-muted)">{{ job.description }}</p>
              <div v-if="job.tags?.length" class="mt-4 flex flex-wrap gap-2">
                <span v-for="tag in job.tags" :key="tag" class="rounded-full px-2.5 py-1 text-xs" style="background-color: var(--color-bg); color: var(--color-text-muted)">
                  {{ tag }}
                </span>
              </div>
            </template>
          </div>

          <div class="card-surface p-5">
            <h3 class="text-lg font-bold" style="color: var(--color-text)">Application Status</h3>
            <div class="mt-5 rounded-xl p-4" style="background-color: var(--color-bg)">
              <div class="flex items-center gap-3">
                <div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
                  <component :is="statusIcon(application.status)" :size="19" />
                </div>
                <div>
                  <p class="text-xs" style="color: var(--color-text-muted)">Current Status</p>
                  <StatusBadge :status="application.status" kind="application" />
                </div>
              </div>

              <div class="mt-5 border-t pt-4" style="border-color: var(--color-border)">
                <p class="text-xs" style="color: var(--color-text-muted)">Next Step</p>
                <p class="mt-1 text-sm font-semibold leading-5" style="color: var(--color-text)">{{ nextStepLabel(application) }}</p>
              </div>

              <div class="mt-5 border-t pt-4" style="border-color: var(--color-border)">
                <p class="text-xs" style="color: var(--color-text-muted)">Application Date</p>
                <p class="mt-1 text-sm font-semibold" style="color: var(--color-text)">{{ formatDate(application.submittedAt) }}</p>
              </div>
            </div>
          </div>
        </section>
      </template>
    </div>
  </DashboardLayout>
</template>
