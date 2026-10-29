<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowLeft,
  CheckCircle2,
  MapPin,
  BriefcaseBusiness,
  Tag,
  Clock3,
  ArrowRight,
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const router = useRouter()
const route = useRoute()

const applicationId = route.query.applicationId || 'APP-2026-089'
const jobTitle = route.query.jobTitle || 'Software Developer'

// Application summary state matching the reference image layout
const applicationSummary = ref({
  applicantName: 'Asha Ndalichako',
  email: 'asha.ndalichako@gmail.com',
  phone: '+255 712 345 678',
  submittedDate: 'Today, 2:45 PM',
  status: 'Under Review',
})

// Sidebar Job Details
const jobDetails = ref({
  company: 'Apex Solutions Ltd',
  companyBadge: 'Technology • 11-50 employees',
  logo: 'A',
  logoBg: '#ffe5d8',
  logoColor: '#ff5a0a',
  location: 'Dar es Salaam',
  experience: '1-3 years',
  category: 'IT & Software',
  salary: 'TSh 1,200,000 - 1,800,000 / month',
  description: 'The hiring team will review your application, education, and work experience to ensure alignment with role requirements.',
  requirements: [
    "Bachelor's degree in Computer Science or related field",
    'At least 1 year of relevant experience',
    'Proficiency in JavaScript, React, or modern frameworks',
    'Good problem-solving and communication skills',
  ],
  deadline: '30 October 2026',
})
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
          <span v-if="applicationId" class="text-slate-400"></span>
        </p>
      </div>

      <!-- Main 2-Column Grid Layout -->
      <div class="grid gap-6 lg:grid-cols-[1fr_400px]">

        <!-- Left Column: Submission Confirmation Card -->
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
                Thank you, <span class="font-semibold text-slate-800">{{ applicationSummary.applicantName }}</span>. Your application for <span class="font-semibold text-slate-800">{{ jobTitle }}</span> has been submitted to <span class="font-semibold text-slate-800">{{ jobDetails.company }}</span>.
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
                <span class="inline-flex items-center rounded-md bg-orange-100 px-2.5 py-0.5 text-[10px] font-semibold text-orange-800 border border-orange-200 mt-0.5">
                  {{ applicationSummary.status }}
                </span>
              </div>
              <div>
                <span class="text-slate-400 block">Company:</span>
                <span class="font-bold text-slate-800">{{ jobDetails.company }}</span>
              </div>
              <div>
                <span class="text-slate-400 block">Applicant Email:</span>
                <span class="font-medium text-slate-700">{{ applicationSummary.email }}</span>
              </div>
              <div>
                <span class="text-slate-400 block">Date & Time:</span>
                <span class="font-medium text-slate-700">{{ applicationSummary.submittedDate }}</span>
              </div>
              <div>
                <span class="text-slate-400 block">Phone Number:</span>
                <span class="font-medium text-slate-700">{{ applicationSummary.phone }}</span>
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

        <!-- Right Column: Sidebar -->
        <div class="space-y-4">
          <div class="rounded-2xl border bg-white p-5 shadow-sm space-y-4" style="border-color:#ffd3bd">
            
            <!-- Company Info Header -->
            <div class="flex items-center gap-3 border-b border-slate-100 pb-4">
              <div
                class="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl text-base font-bold shadow-sm"
                :style="{ backgroundColor: jobDetails.logoBg, color: jobDetails.logoColor }"
              >
                {{ jobDetails.logo }}
              </div>
              <div class="min-w-0">
                <div class="flex items-center gap-1.5">
                  <h3 class="text-xs font-bold text-slate-900 truncate">
                    {{ jobDetails.company }}
                  </h3>
                  <span class="text-blue-500 text-xs">✓</span>
                </div>
                <p class="text-[10px] text-slate-400">
                  {{ jobDetails.companyBadge }}
                </p>
              </div>
            </div>

            <!-- Job Title & Meta -->
            <div>
              <h2 class="text-base font-extrabold text-slate-900">
                {{ jobTitle }}
              </h2>

              <div class="mt-2.5 flex flex-wrap gap-x-3 gap-y-1.5 text-xs text-slate-500">
                <span class="inline-flex items-center gap-1">
                  <MapPin :size="13" class="text-slate-400" /> {{ jobDetails.location }}
                </span>
                <span class="inline-flex items-center gap-1">
                  <BriefcaseBusiness :size="13" class="text-slate-400" /> {{ jobDetails.experience }}
                </span>
                <span class="inline-flex items-center gap-1">
                  <Tag :size="13" class="text-slate-400" /> {{ jobDetails.category }}
                </span>
              </div>

              <p class="mt-3 text-xs font-bold text-slate-900">
                {{ jobDetails.salary }}
              </p>
            </div>

            <!-- Job Description -->
            <div class="border-t border-slate-100 pt-4 space-y-1.5">
              <h4 class="text-xs font-bold text-slate-900">Job Description</h4>
              <p class="text-xs leading-relaxed text-slate-600">
                {{ jobDetails.description }}
              </p>
            </div>

            <!-- Requirements -->
            <div class="border-t border-slate-100 pt-4 space-y-2">
              <h4 class="text-xs font-bold text-slate-900">Requirements</h4>
              <ul class="list-disc pl-4 space-y-1 text-xs text-slate-600">
                <li v-for="(req, idx) in jobDetails.requirements" :key="idx">
                  {{ req }}
                </li>
              </ul>
            </div>

            <!-- Deadline Box -->
            <div class="rounded-xl border border-slate-100 bg-slate-50 p-3.5 flex items-center gap-3">
              <Clock3 :size="18" class="text-slate-400 shrink-0" />
              <div>
                <p class="text-[10px] font-medium text-slate-400 uppercase tracking-wider">Deadline</p>
                <p class="text-xs font-bold text-slate-800">{{ jobDetails.deadline }}</p>
              </div>
            </div>

          </div>
        </div>

      </div>

    </div>
  </DashboardLayout>
</template>