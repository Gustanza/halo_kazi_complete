<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowLeft,
  CheckCircle2,
  ArrowRight,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import StatusBadge from '../../../components/ui/StatusBadge.vue'
import { useAuthStore } from '../../../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const applicationId = route.query.applicationId || ''
const jobTitle = route.query.jobTitle || 'this role'
const company = route.query.company || 'the employer'

const submittedAt = computed(() => new Date().toLocaleString('en-GB', { dateStyle: 'medium', timeStyle: 'short' }))
</script>

<template>
  <DashboardLayout>
    <div class="mx-auto max-w-7xl space-y-4 pb-10">

      <!-- Back Link -->
      <div>
        <button
          type="button"
          class="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 transition hover:text-slate-800 cursor-pointer"
          @click="router.push('/dashboard/find-jobs')"
        >
          <ArrowLeft :size="15" /> Back to find jobs
        </button>
      </div>

      <!-- Main Title -->
      <div>
        <h1 class="text-2xl font-extrabold text-slate-900">
          Application Submitted
        </h1>
        <p class="mt-0.5 text-xs text-slate-500">
          Your application has been successfully sent to the employer.
        </p>
      </div>

      <div class="rounded-2xl border bg-white p-6 shadow-sm space-y-6" style="border-color:#ffd3bd">

        <!-- Header with Step Indicator -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-5">
          <h2 class="text-base font-bold text-slate-900">
            Submission Status
          </h2>

          <div class="flex items-center gap-3 text-xs font-semibold">
            <div class="flex items-center gap-1.5 text-emerald-600">
              <span class="flex h-6 w-6 items-center justify-center rounded-full bg-emerald-100 text-emerald-700 text-[11px] font-bold">✓</span>
              Application
            </div>
            <span class="h-px w-8 bg-orange-300"></span>
            <div class="flex items-center gap-1.5 text-orange-600 font-bold">
              <span class="flex h-6 w-6 items-center justify-center rounded-full bg-orange-600 text-white text-[11px] font-bold shadow-sm">2</span>
              Confirmation
            </div>
          </div>
        </div>

        <!-- Success Alert Box -->
        <div class="flex flex-col items-center justify-center text-center py-6 px-4 rounded-2xl bg-orange-50/50 border border-orange-100 space-y-3">
          <div class="flex h-12 w-12 items-center justify-center rounded-full bg-orange-500 text-white shadow-sm">
            <CheckCircle2 :size="28" />
          </div>
          <div class="space-y-1">
            <h3 class="text-base font-extrabold text-slate-900">
              Application Sent Successfully!
            </h3>
            <p class="text-xs text-slate-600 max-w-md">
              Thank you, <span class="font-semibold text-slate-800">{{ auth.user.value?.fullName }}</span>. Your application for
              <span class="font-semibold text-slate-800">{{ jobTitle }}</span> has been submitted to
              <span class="font-semibold text-slate-800">{{ company }}</span>.
            </p>
          </div>
        </div>

        <!-- Application Summary Table Box -->
        <div class="rounded-xl border border-slate-100 bg-slate-50/50 p-5 space-y-4">
          <h4 class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">
            Application Summary
          </h4>

          <div class="grid gap-4 sm:grid-cols-2 text-xs">
            <div>
              <span class="text-slate-400 block">Submitted Position:</span>
              <span class="font-bold text-slate-800">{{ jobTitle }}</span>
            </div>
            <div>
              <span class="text-slate-400 block">Current Status:</span>
              <StatusBadge status="SUBMITTED" kind="application" />
            </div>
            <div>
              <span class="text-slate-400 block">Company:</span>
              <span class="font-bold text-slate-800">{{ company }}</span>
            </div>
            <div v-if="applicationId">
              <span class="text-slate-400 block">Reference:</span>
              <span class="font-medium text-slate-700">#{{ applicationId }}</span>
            </div>
            <div>
              <span class="text-slate-400 block">Applicant Email:</span>
              <span class="font-medium text-slate-700">{{ auth.user.value?.email }}</span>
            </div>
            <div>
              <span class="text-slate-400 block">Date & Time:</span>
              <span class="font-medium text-slate-700">{{ submittedAt }}</span>
            </div>
          </div>
        </div>

        <!-- What Happens Next Bullet List -->
        <div class="space-y-2 text-xs text-slate-600">
          <p class="font-bold text-slate-800">What happens next?</p>
          <ul class="list-disc pl-4 space-y-1.5 text-slate-600">
            <li>The hiring team will review your application against role requirements.</li>
            <li>Track status updates directly on your applications dashboard anytime.</li>
            <li>If shortlisted, the hiring team will contact you for next steps.</li>
          </ul>
        </div>

        <!-- Footer Actions -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-3 pt-4 border-t border-slate-100">
          <button
            type="button"
            class="w-full sm:w-auto inline-flex items-center justify-center rounded-xl border border-slate-200 bg-white px-5 py-2.5 text-xs font-semibold text-slate-700 shadow-sm transition hover:bg-slate-50 cursor-pointer"
            @click="router.push('/dashboard/find-jobs')"
          >
            Browse More Jobs
          </button>

          <button
            type="button"
            class="w-full sm:w-auto inline-flex items-center justify-center gap-2 rounded-xl bg-orange-600 px-6 py-2.5 text-xs font-semibold text-white shadow-sm transition hover:bg-orange-700 cursor-pointer"
            style="background-color: #ff5a0a"
            @click="router.push('/dashboard/applications')"
          >
            Go to My Applications <ArrowRight :size="15" />
          </button>
        </div>

      </div>
    </div>
  </DashboardLayout>
</template>
