<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Download, UserRound } from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const route = useRoute()
const router = useRouter()

const cvs = {
  1: {
    title: 'Professional CV',
    name: 'Raymond Mwangi',
    email: 'john.mwangi@email.com',
    phone: '+255 624023920',
    location: 'Dar es Salaam, Tanzania',
    job: 'Marketing Specialist',
    summary: 'Results-driven marketing professional with 5+ years of experience in digital marketing, brand strategy and social media management.',
    theme: 'professional',
  },
  2: {
    title: 'Creative CV',
    name: 'Raymond Mwangi',
    email: 'john.mwangi@email.com',
    phone: '+255 624023920',
    location: 'Dar es Salaam, Tanzania',
    job: 'Creative Professional',
    summary: 'Creative professional passionate about developing engaging content and innovative marketing solutions.',
    theme: 'creative',
  },
  3: {
    title: 'Minimal CV',
    name: 'Raymond Mwangi',
    email: 'john.mwangi@email.com',
    phone: '+255 624023920',
    location: 'Dar es Salaam, Tanzania',
    job: 'Professional',
    summary: 'Motivated professional with strong communication, organization and problem-solving skills.',
    theme: 'minimal',
  },
  4: {
    title: 'Student CV',
    name: 'Raymond Mwangi',
    email: 'john.mwangi@email.com',
    phone: '+255 624023920',
    location: 'Dar es Salaam, Tanzania',
    job: 'Student',
    summary: 'Enthusiastic student seeking opportunities to develop professional skills and gain practical experience.',
    theme: 'student',
  },
}

const cv = computed(() => cvs[route.query.cv] || cvs[1])

const themeClass = computed(() => ({
  professional: 'bg-blue-700',
  creative: 'bg-purple-700',
  minimal: 'bg-slate-800',
  student: 'bg-violet-700',
}[cv.value.theme] || 'bg-blue-700'))

function goBack() {
  router.push('/dashboard/my-cvs')
}

function downloadCv() {
  window.print()
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-5">
      <!-- Header -->
      <section class="rounded-2xl border border-orange-200 bg-white px-5 py-4 shadow-sm">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h1 class="text-3xl font-extrabold" style="color: var(--color-text)">
              View CV
            </h1>
            <p class="mt-1 text-sm" style="color: var(--color-text-muted)">
              Preview your selected CV.
            </p>
          </div>

          <div class="flex gap-2">
            <button
              type="button"
              @click="goBack"
              class="inline-flex items-center gap-2 rounded-lg border px-4 py-2.5 text-sm"
              style="border-color: var(--color-border)"
            >
              <ArrowLeft :size="16" />
              Back to My CVs
            </button>

            <button
              type="button"
              @click="downloadCv"
              class="inline-flex items-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-semibold text-white"
            >
              <Download :size="16" />
              Print / Save PDF
            </button>
          </div>
        </div>
      </section>

      <!-- CV Preview -->
      <section class="rounded-2xl border border-orange-200 bg-white p-4 shadow-sm sm:p-6">
        <div class="mx-auto max-w-3xl overflow-hidden border bg-white shadow-md print:shadow-none">
          <!-- CV heading -->
          <div :class="themeClass" class="p-8 text-white sm:p-10">
            <div class="flex items-center gap-5">
              <div class="flex h-20 w-20 shrink-0 items-center justify-center rounded-full border-2 border-white/50 bg-white/15">
                <UserRound :size="38" />
              </div>

              <div class="min-w-0">
                <h2 class="text-2xl font-extrabold sm:text-3xl">
                  {{ cv.name }}
                </h2>
                <p class="mt-1 text-sm text-white/85">
                  {{ cv.job }}
                </p>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-[220px_1fr]">
            <!-- Contact -->
            <aside class="bg-slate-50 p-6 sm:p-7">
              <h3 class="border-b border-slate-300 pb-2 text-sm font-bold uppercase tracking-wider">
                Contact
              </h3>

              <div class="mt-4 space-y-3 break-words text-sm text-slate-600">
                <p>{{ cv.phone }}</p>
                <p>{{ cv.email }}</p>
                <p>{{ cv.location }}</p>
              </div>

              <h3 class="mt-8 border-b border-slate-300 pb-2 text-sm font-bold uppercase tracking-wider">
                Skills
              </h3>

              <ul class="mt-4 list-disc space-y-2 pl-4 text-sm text-slate-600">
                <li>Communication</li>
                <li>Problem Solving</li>
                <li>Teamwork</li>
                <li>Digital Skills</li>
              </ul>
            </aside>

            <!-- CV content -->
            <main class="p-6 sm:p-8">
              <section>
                <h3 class="border-b-2 border-slate-800 pb-2 text-base font-bold uppercase">
                  Professional Summary
                </h3>
                <p class="mt-4 text-sm leading-7 text-slate-600">
                  {{ cv.summary }}
                </p>
              </section>

              <section class="mt-8">
                <h3 class="border-b-2 border-slate-800 pb-2 text-base font-bold uppercase">
                  Work Experience
                </h3>

                <div class="mt-4">
                  <div class="flex flex-wrap justify-between gap-2">
                    <h4 class="font-bold text-slate-800">Marketing Specialist</h4>
                    <span class="text-sm text-slate-500">2021 – Present</span>
                  </div>
                  <p class="mt-1 text-sm text-slate-500">ABC Company Ltd</p>
                  <p class="mt-2 text-sm leading-6 text-slate-600">
                    Develop marketing campaigns, manage social media platforms
                    and analyze campaign performance.
                  </p>
                </div>
              </section>

              <section class="mt-8">
                <h3 class="border-b-2 border-slate-800 pb-2 text-base font-bold uppercase">
                  Education
                </h3>
                <div class="mt-4">
                  <h4 class="font-bold text-slate-800">
                    Bachelor of Business Administration
                  </h4>
                  <p class="mt-1 text-sm text-slate-500">
                    University of Dar es Salaam · 2014–2017
                  </p>
                </div>
              </section>
            </main>
          </div>
        </div>
      </section>
    </div>
  </DashboardLayout>
</template>

<style>
@media print {
  body * {
    visibility: hidden;
  }

  .cv-print-area,
  .cv-print-area * {
    visibility: visible;
  }
}
</style>
