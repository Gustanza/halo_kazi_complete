<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  MapPin,
  CalendarDays,
  BriefcaseBusiness,
  Building2,
  CheckCircle2,
  Clock3,
  XCircle
} from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const route = useRoute()
const router = useRouter()

const applications = {
  1: {
    id: 1,
    title: 'Frontend Developer',
    company: 'Google',
    location: 'Dar es Salaam, Tanzania',
    applied: '12 Apr 2026',
    status: 'Shortlisted',
    nextStep: 'Technical Interview',
    logo: 'G',
    logoStyle: 'google',
    jobType: 'Full Time',
    experience: '2+ Years',
    salary: 'TZS 1,500,000 - 2,500,000',
    description:
      'We are looking for a talented Frontend Developer to join our team and help build modern, responsive and user-friendly web applications.',
    requirements: [
      'Strong knowledge of HTML, CSS and JavaScript',
      'Experience with Vue.js or React',
      'Good understanding of responsive design',
      'Ability to work effectively in a team',
    ],
  },

  2: {
    id: 2,
    title: 'Full Stack Developer',
    company: 'TechSolution Ltd',
    location: 'Dar es Salaam, Tanzania',
    applied: '17 May 2026',
    status: 'Interview',
    nextStep: 'Attend Interview',
    logo: '⬡',
    logoStyle: 'tech',
    jobType: 'Full Time',
    experience: '3+ Years',
    salary: 'TZS 2,000,000 - 3,500,000',
    description:
      'TechSolution Ltd is looking for a Full Stack Developer who can develop reliable and scalable web applications across both frontend and backend.',
    requirements: [
      'Experience with Laravel or Node.js',
      'Strong JavaScript knowledge',
      'Experience with databases and REST APIs',
      'Understanding of Git and software development practices',
    ],
  },

  3: {
    id: 3,
    title: 'Customer Care Agency',
    company: 'Vodacom Tanzania',
    location: 'Dar es Salaam, Tanzania',
    applied: '25 August 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'v',
    logoStyle: 'vodacom',
    jobType: 'Full Time',
    experience: '1+ Year',
    salary: 'TZS 800,000 - 1,200,000',
    description:
      'We are looking for a customer-focused professional to provide excellent support to customers and help resolve their questions and concerns.',
    requirements: [
      'Excellent communication skills',
      'Good customer service skills',
      'Ability to work under pressure',
      'Basic computer skills',
    ],
  },

  4: {
    id: 4,
    title: 'IT Support Specialist',
    company: 'CRDB Bank PLC',
    location: 'Dar es Salaam, Tanzania',
    applied: '09 July 2026',
    status: 'Rejected',
    nextStep: 'Not selected for this role',
    logo: 'CRDB',
    logoStyle: 'crdb',
    jobType: 'Full Time',
    experience: '2+ Years',
    salary: 'TZS 1,200,000 - 2,000,000',
    description:
      'CRDB Bank PLC was looking for an IT Support Specialist to provide technical assistance, troubleshoot system issues and support users.',
    requirements: [
      'Diploma or degree in Information Technology',
      'Strong troubleshooting skills',
      'Knowledge of computer networks',
      'Good communication and problem-solving skills',
    ],
  },

  5: {
    id: 5,
    title: 'UI/UX Designer',
    company: 'Creative Studio',
    location: 'Arusha, Tanzania',
    applied: '15 August 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'CS',
    logoStyle: 'creative',
    jobType: 'Full Time',
    experience: '2+ Years',
    salary: 'TZS 1,000,000 - 2,000,000',
    description:
      'Creative Studio is seeking a UI/UX Designer to create intuitive, attractive and user-friendly digital experiences.',
    requirements: [
      'Experience with Figma or similar design tools',
      'Strong understanding of UI/UX principles',
      'Good visual design skills',
      'Ability to create user-centered designs',
    ],
  },

  6: {
    id: 6,
    title: 'Software Engineer',
    company: 'Tanzania Tech',
    location: 'Dar es Salaam, Tanzania',
    applied: '20 August 2026',
    status: 'Shortlisted',
    nextStep: 'Technical Interview',
    logo: 'TT',
    logoStyle: 'tech',
    jobType: 'Full Time',
    experience: '3+ Years',
    salary: 'TZS 2,000,000 - 3,500,000',
    description:
      'Tanzania Tech is looking for a Software Engineer to design, develop and maintain high-quality software solutions.',
    requirements: [
      'Strong programming skills',
      'Experience with modern frameworks',
      'Knowledge of databases',
      'Good understanding of software development lifecycle',
    ],
  },

  7: {
    id: 7,
    title: 'Product Designer',
    company: 'Digital Solutions',
    location: 'Dodoma, Tanzania',
    applied: '02 September 2026',
    status: 'Interview',
    nextStep: 'Attend Interview',
    logo: 'DS',
    logoStyle: 'creative',
    jobType: 'Full Time',
    experience: '2+ Years',
    salary: 'TZS 1,500,000 - 2,500,000',
    description:
      'Digital Solutions is looking for a Product Designer to design meaningful digital products and improve user experiences.',
    requirements: [
      'Strong UI/UX design knowledge',
      'Experience with Figma',
      'Understanding of design systems',
      'Strong communication skills',
    ],
  },

  8: {
    id: 8,
    title: 'Business Analyst',
    company: 'Finance Group',
    location: 'Dar es Salaam, Tanzania',
    applied: '10 September 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'FG',
    logoStyle: 'tech',
    jobType: 'Full Time',
    experience: '2+ Years',
    salary: 'TZS 1,500,000 - 2,500,000',
    description:
      'Finance Group is seeking a Business Analyst to analyze business requirements, identify opportunities and support business decisions.',
    requirements: [
      'Strong analytical skills',
      'Good knowledge of business processes',
      'Excellent communication skills',
      'Experience with data analysis tools',
    ],
  },

  9: {
    id: 9,
    title: 'Network Administrator',
    company: 'Telecom Services',
    location: 'Arusha, Tanzania',
    applied: '18 September 2026',
    status: 'Rejected',
    nextStep: 'Not selected for this role',
    logo: 'TS',
    logoStyle: 'creative',
    jobType: 'Full Time',
    experience: '2+ Years',
    salary: 'TZS 1,300,000 - 2,300,000',
    description:
      'Telecom Services was looking for a Network Administrator to maintain network infrastructure and ensure reliable connectivity.',
    requirements: [
      'Knowledge of TCP/IP networking',
      'Experience with network troubleshooting',
      'Knowledge of routers and switches',
      'Good problem-solving skills',
    ],
  },

  10: {
    id: 10,
    title: 'Junior Developer',
    company: 'WebWorks Ltd',
    location: 'Dodoma, Tanzania',
    applied: '25 September 2026',
    status: 'Applied',
    nextStep: 'Awaiting response',
    logo: 'W',
    logoStyle: 'tech',
    jobType: 'Full Time',
    experience: '0-2 Years',
    salary: 'TZS 800,000 - 1,500,000',
    description:
      'WebWorks Ltd is looking for a Junior Developer to join the development team and contribute to modern web projects.',
    requirements: [
      'Basic knowledge of HTML, CSS and JavaScript',
      'Basic knowledge of Vue.js or React',
      'Willingness to learn',
      'Good teamwork and communication skills',
    ],
  },
}

const application = computed(() => {
  const id = Number(route.query.application)

  return applications[id] || null
})

const statusClass = computed(() => {
  if (!application.value) {
    return ''
  }

  const status = application.value.status

  if (status === 'Applied') {
    return 'bg-green-100 text-green-700'
  }

  if (status === 'Shortlisted') {
    return 'bg-green-100 text-green-700'
  }

  if (status === 'Interview') {
    return 'bg-sky-100 text-sky-700'
  }

  return 'bg-slate-200 text-slate-600'
})

const statusIcon = computed(() => {
  if (!application.value) {
    return Clock3
  }

  const status = application.value.status

  if (status === 'Rejected') {
    return XCircle
  }

  if (status === 'Interview') {
    return CalendarDays
  }

  if (status === 'Shortlisted') {
    return CheckCircle2
  }

  return Clock3
})

function goBack() {
  router.push('/dashboard/applications')
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-3 text-slate-800">

      <!-- Header -->
      <section
        class="rounded-xl border border-orange-200 bg-white px-5 py-4 shadow-sm"
      >
        <div
          class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"
        >

          <div>
            <h1 class="text-3xl font-extrabold text-slate-800">
              View Application
            </h1>

            <p class="mt-1 text-sm text-slate-500">
              View details of your selected job application.
            </p>
          </div>

        </div>
      </section>

      <!-- No application -->
      <section
        v-if="!application"
        class="rounded-xl border border-orange-200 bg-white p-10 text-center"
      >
        <h2 class="text-lg font-bold text-slate-800">
          Application not found
        </h2>

        <p class="mt-2 text-sm text-slate-500">
          The selected application could not be found.
        </p>

        <button
          @click="goBack"
          class="mt-5 rounded-lg bg-orange-600 px-4 py-2 text-sm font-semibold text-white hover:bg-orange-700"
        >
          Back to Applications
        </button>
      </section>

      <!-- Selected Application -->
      <template v-else>

        <!-- Application Header Card -->
        <section
          class="rounded-xl border border-orange-200 bg-white p-5 shadow-sm"
        >
          <div
            class="flex flex-col gap-5 sm:flex-row sm:items-center"
          >

            <!-- Company Logo -->
            <div
              class="flex h-20 w-20 shrink-0 items-center justify-center rounded-xl border border-slate-100 bg-white text-2xl font-bold"
              :class="{
                'text-blue-500': application.logoStyle === 'google',
                'text-violet-700': application.logoStyle === 'tech',
                'text-red-500': application.logoStyle === 'vodacom',
                'text-green-600': application.logoStyle === 'crdb',
                'text-orange-600': application.logoStyle === 'creative',
              }"
            >

              <span
                v-if="application.logoStyle === 'google'"
                class="text-4xl font-bold"
              >
                G
              </span>

              <span
                v-else-if="application.logoStyle === 'vodacom'"
                class="text-4xl"
              >
                ◉
              </span>

              <span
                v-else
                class="text-xl font-extrabold"
              >
                {{ application.logo }}
              </span>

            </div>

            <!-- Main Details -->
            <div class="min-w-0 flex-1">

              <h2
                class="break-words text-2xl font-extrabold text-slate-900"
              >
                {{ application.title }}
              </h2>

              <div
                class="mt-1 flex flex-wrap items-center gap-2"
              >
                <Building2
                  :size="17"
                  class="text-slate-500"
                />

                <span class="font-semibold text-slate-800">
                  {{ application.company }}
                </span>

                <span class="text-blue-500">
                  ✦
                </span>
              </div>

              <div
                class="mt-3 flex flex-wrap gap-x-6 gap-y-2 text-sm text-slate-600"
              >

                <span
                  class="inline-flex items-center gap-2"
                >
                  <MapPin :size="16" />
                  {{ application.location }}
                </span>

                <span
                  class="inline-flex items-center gap-2"
                >
                  <CalendarDays :size="16" />
                  Applied on {{ application.applied }}
                </span>

              </div>

            </div>

            <!-- Status -->
            <div class="sm:self-start">

              <span
                class="inline-flex items-center gap-2 rounded-full px-4 py-2 text-xs font-semibold"
                :class="statusClass"
              >
                <component
                  :is="statusIcon"
                  :size="15"
                />

                {{ application.status }}
              </span>

            </div>

          </div>
        </section>

        <!-- Application Information -->
        <section class="grid grid-cols-1 gap-3 md:grid-cols-3">

          <!-- Job Type -->
          <div
            class="rounded-xl border border-orange-200 bg-white p-4"
          >
            <div class="flex items-center gap-3">

              <div
                class="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-50 text-orange-600"
              >
                <BriefcaseBusiness :size="19" />
              </div>

              <div>
                <p class="text-xs text-slate-500">
                  Job Type
                </p>

                <p class="mt-0.5 text-sm font-semibold text-slate-800">
                  {{ application.jobType }}
                </p>
              </div>

            </div>
          </div>

          <!-- Experience -->
          <div
            class="rounded-xl border border-orange-200 bg-white p-4"
          >
            <div class="flex items-center gap-3">

              <div
                class="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-50 text-orange-600"
              >
                <BriefcaseBusiness :size="19" />
              </div>

              <div>
                <p class="text-xs text-slate-500">
                  Experience
                </p>

                <p class="mt-0.5 text-sm font-semibold text-slate-800">
                  {{ application.experience }}
                </p>
              </div>

            </div>
          </div>

          <!-- Salary -->
          <div
            class="rounded-xl border border-orange-200 bg-white p-4"
          >
            <div class="flex items-center gap-3">

              <div
                class="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-50 text-orange-600"
              >
                <Building2 :size="19" />
              </div>

              <div>
                <p class="text-xs text-slate-500">
                  Salary
                </p>

                <p class="mt-0.5 text-sm font-semibold text-slate-800">
                  {{ application.salary }}
                </p>
              </div>

            </div>
          </div>

        </section>

        <!-- Application Details -->
        <section class="grid grid-cols-1 gap-3 lg:grid-cols-3">

          <!-- Job Description -->
          <div
            class="rounded-xl border border-orange-200 bg-white p-5 lg:col-span-2"
          >

            <h3 class="text-lg font-bold text-slate-900">
              Job Description
            </h3>

            <p class="mt-3 text-sm leading-7 text-slate-600">
              {{ application.description }}
            </p>

            <h3 class="mt-7 text-lg font-bold text-slate-900">
              Requirements
            </h3>

            <ul class="mt-3 space-y-3">

              <li
                v-for="requirement in application.requirements"
                :key="requirement"
                class="flex items-start gap-2 text-sm text-slate-600"
              >
                <CheckCircle2
                  :size="17"
                  class="mt-0.5 shrink-0 text-green-600"
                />

                <span>
                  {{ requirement }}
                </span>
              </li>

            </ul>

          </div>

          <!-- Application Status -->
          <div
            class="rounded-xl border border-orange-200 bg-white p-5"
          >

            <h3 class="text-lg font-bold text-slate-900">
              Application Status
            </h3>

            <div
              class="mt-5 rounded-xl bg-slate-50 p-4"
            >

              <div class="flex items-center gap-3">

                <div
                  class="flex h-10 w-10 items-center justify-center rounded-full"
                  :class="statusClass"
                >
                  <component
                    :is="statusIcon"
                    :size="19"
                  />
                </div>

                <div>
                  <p class="text-xs text-slate-500">
                    Current Status
                  </p>

                  <p class="mt-0.5 text-sm font-bold text-slate-800">
                    {{ application.status }}
                  </p>
                </div>

              </div>

              <div
                class="mt-5 border-t border-slate-200 pt-4"
              >
                <p class="text-xs text-slate-500">
                  Next Step
                </p>

                <p
                  class="mt-1 text-sm font-semibold leading-5 text-slate-800"
                >
                  {{ application.nextStep }}
                </p>
              </div>

              <div
                class="mt-5 border-t border-slate-200 pt-4"
              >
                <p class="text-xs text-slate-500">
                  Application Date
                </p>

                <p
                  class="mt-1 text-sm font-semibold text-slate-800"
                >
                  {{ application.applied }}
                </p>
              </div>

            </div>

          </div>

        </section>

      </template>

    </div>
  </DashboardLayout>
</template>