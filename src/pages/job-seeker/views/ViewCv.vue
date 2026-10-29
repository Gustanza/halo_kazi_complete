<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Download } from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const route = useRoute()
const router = useRouter()

const templates = {
  1: {
    name: 'Professional',
    layout: 'sidebar',
    sidebar: 'bg-blue-700',
    heading: 'text-blue-700',
  },
  2: {
    name: 'Modern',
    layout: 'sidebar',
    sidebar: 'bg-orange-600',
    heading: 'text-orange-600',
  },
  3: {
    name: 'Minimal',
    layout: 'minimal',
    sidebar: 'bg-slate-100',
    heading: 'text-slate-800',
  },
  4: {
    name: 'Creative',
    layout: 'sidebar',
    sidebar: 'bg-purple-700',
    heading: 'text-purple-700',
  },
  5: {
    name: 'Simple',
    layout: 'minimal',
    sidebar: 'bg-slate-100',
    heading: 'text-slate-800',
  },
}

const cvRecords = {
  1: {
    name: 'John Mwangi',
    job: 'Marketing Specialist',
    email: 'john.mwangi@email.com',
    phone: '+255 624023920',
    location: 'Dar es Salaam, Tanzania',
    summary:
      'Results-driven professional with experience in marketing, communication and business development.',
    skills: ['Digital Marketing', 'Communication', 'SEO', 'Teamwork'],
    experiences: [
      {
        role: 'Marketing Specialist',
        company: 'ABC Company',
        period: '2022 - Present',
        description:
          'Developed marketing campaigns and managed digital platforms.',
      },
    ],
    education: [
      {
        course: 'Bachelor Degree',
        school: 'University of Dar es Salaam',
        period: '2018 - 2021',
      },
    ],
  },

  2: {
    name: 'Jane Joseph',
    job: 'Graphic Designer',
    email: 'jane@example.com',
    phone: '+255 700000000',
    location: 'Dar es Salaam, Tanzania',
    summary:
      'Creative designer focused on visual communication and digital design.',
    skills: ['UI Design', 'Photoshop', 'Illustrator', 'Creativity'],
    experiences: [
      {
        role: 'Graphic Designer',
        company: 'Creative Studio',
        period: '2023 - Present',
        description:
          'Designed brand identities and digital marketing materials.',
      },
    ],
    education: [
      {
        course: 'Diploma in Graphic Design',
        school: 'Design Institute',
        period: '2020 - 2022',
      },
    ],
  },

  3: {
    name: 'Michael Peter',
    job: 'Software Developer',
    email: 'michael@example.com',
    phone: '+255 711000000',
    location: 'Arusha, Tanzania',
    summary:
      'Software developer interested in building reliable and user-friendly applications.',
    skills: ['Vue.js', 'Laravel', 'JavaScript', 'SQL'],
    experiences: [
      {
        role: 'Junior Developer',
        company: 'Tech Company',
        period: '2023 - Present',
        description:
          'Built and maintained web application features.',
      },
    ],
    education: [
      {
        course: 'Bachelor of Computer Science',
        school: 'University',
        period: '2019 - 2022',
      },
    ],
  },

  4: {
    name: 'Sarah James',
    job: 'UI/UX Designer',
    email: 'sarah@example.com',
    phone: '+255 722000000',
    location: 'Dodoma, Tanzania',
    summary:
      'Creative professional who designs accessible and engaging user experiences.',
    skills: ['Figma', 'Prototyping', 'Research', 'Wireframing'],
    experiences: [
      {
        role: 'UI/UX Designer',
        company: 'Digital Agency',
        period: '2022 - Present',
        description:
          'Created wireframes, prototypes and user interface designs.',
      },
    ],
    education: [
      {
        course: 'Bachelor of Information Technology',
        school: 'University',
        period: '2018 - 2021',
      },
    ],
  },

  5: {
    name: 'David John',
    job: 'Administrative Assistant',
    email: 'david@example.com',
    phone: '+255 733000000',
    location: 'Mwanza, Tanzania',
    summary:
      'Organized professional with strong communication and administrative skills.',
    skills: ['Organization', 'Communication', 'Microsoft Office'],
    experiences: [
      {
        role: 'Administrative Assistant',
        company: 'Business Office',
        period: '2021 - Present',
        description:
          'Coordinated records, schedules and daily office activities.',
      },
    ],
    education: [
      {
        course: 'Diploma in Business Administration',
        school: 'Business College',
        period: '2018 - 2020',
      },
    ],
  },
}

const templateId = computed(() => Number(route.query.template) || 1)
const cvId = computed(() => Number(route.query.cv) || 1)

const template = computed(
  () => templates[templateId.value] || templates[1],
)

const cv = computed(
  () => cvRecords[cvId.value] || cvRecords[1],
)

function backToMyCvs() {
  router.push('/dashboard/my-cvs')
}

function downloadPdf() {
  window.print()
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-5">

      <!-- Page heading and navigation -->
      <section
        class="rounded-2xl border border-orange-200 bg-white px-5 py-4 shadow-sm no-print"
      >
        <!-- Back to My CVs -->
        <button
          type="button"
          class="mb-3 inline-flex items-center gap-2 text-sm font-medium text-slate-500 transition hover:text-orange-600"
          @click="backToMyCvs"
        >
          <ArrowLeft :size="17" />
          Back to My CVs
        </button>

        <!-- Heading and download button -->
        <div class="flex flex-wrap items-center justify-between gap-3">
          <h1
            class="text-3xl font-extrabold"
            style="color: var(--color-text)"
          >
            {{ template.name }} CV
          </h1>

          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white"
            @click="downloadPdf"
          >
            <Download :size="16" />
            Download PDF
          </button>
        </div>
      </section>

      <!-- CV Preview -->
      <div
        class="cv-print-area mx-auto w-full max-w-4xl overflow-hidden bg-white shadow-lg"
      >

        <!-- Sidebar layouts -->
        <div
          v-if="template.layout === 'sidebar'"
          class="grid min-h-[850px] grid-cols-[30%_70%] sm:grid-cols-[32%_68%]"
        >
          <!-- Sidebar -->
          <aside :class="['p-5 text-white sm:p-8', template.sidebar]">
            <div
              class="mx-auto mb-5 flex h-24 w-24 items-center justify-center rounded-full border-4 border-white/40 bg-white/20 text-2xl font-bold"
            >
              {{ cv.name.charAt(0) }}
            </div>

            <h2 class="text-center text-lg font-bold">
              {{ cv.name }}
            </h2>

            <p class="mt-2 text-center text-sm text-white/80">
              {{ cv.job }}
            </p>

            <!-- Contact -->
            <section class="mt-8">
              <h3 class="mb-3 border-b border-white/40 pb-2 text-sm font-bold">
                CONTACT
              </h3>

              <p class="mb-2 break-words text-xs">{{ cv.phone }}</p>
              <p class="mb-2 break-words text-xs">{{ cv.email }}</p>
              <p class="text-xs">{{ cv.location }}</p>
            </section>

            <!-- Skills -->
            <section class="mt-8">
              <h3 class="mb-3 border-b border-white/40 pb-2 text-sm font-bold">
                SKILLS
              </h3>

              <ul class="space-y-2">
                <li
                  v-for="skill in cv.skills"
                  :key="skill"
                  class="text-xs"
                >
                  {{ skill }}
                </li>
              </ul>
            </section>
          </aside>

          <!-- Main content -->
          <main class="space-y-7 p-5 text-slate-700 sm:p-8">

            <!-- Summary -->
            <section>
              <h2
                :class="[
                  'mb-3 border-b-2 pb-2 text-lg font-bold uppercase',
                  template.heading,
                ]"
              >
                Professional Summary
              </h2>

              <p class="text-sm leading-6">
                {{ cv.summary }}
              </p>
            </section>

            <!-- Experience -->
            <section>
              <h2
                :class="[
                  'mb-4 border-b-2 pb-2 text-lg font-bold uppercase',
                  template.heading,
                ]"
              >
                Experience
              </h2>

              <article
                v-for="item in cv.experiences"
                :key="item.role"
                class="mb-5"
              >
                <h3 class="font-bold">{{ item.role }}</h3>
                <p class="mt-1 text-sm font-medium">
                  {{ item.company }}
                </p>
                <p class="mt-1 text-xs text-slate-500">
                  {{ item.period }}
                </p>
                <p class="mt-2 text-sm leading-6">
                  {{ item.description }}
                </p>
              </article>
            </section>

            <!-- Education -->
            <section>
              <h2
                :class="[
                  'mb-4 border-b-2 pb-2 text-lg font-bold uppercase',
                  template.heading,
                ]"
              >
                Education
              </h2>

              <article
                v-for="item in cv.education"
                :key="item.course"
                class="mb-4"
              >
                <h3 class="font-bold">{{ item.course }}</h3>
                <p class="mt-1 text-sm">{{ item.school }}</p>
                <p class="mt-1 text-xs text-slate-500">
                  {{ item.period }}
                </p>
              </article>
            </section>
          </main>
        </div>

        <!-- Minimal and Simple layouts -->
        <main
          v-else
          class="min-h-[850px] p-8 text-slate-700 sm:p-12"
        >
          <!-- Minimal header -->
          <header
            v-if="templateId === 3"
            class="mb-8 border-b-2 border-slate-300 pb-6 text-center"
          >
            <h2 class="text-3xl font-bold text-slate-900">
              {{ cv.name }}
            </h2>

            <p :class="['mt-2 font-medium', template.heading]">
              {{ cv.job }}
            </p>

            <p class="mt-3 text-sm">
              {{ cv.email }} · {{ cv.phone }} · {{ cv.location }}
            </p>
          </header>

          <!-- Simple header -->
          <header
            v-else
            class="mb-8 border-t-4 border-slate-800 pt-5"
          >
            <h2 class="text-3xl font-bold text-slate-900">
              {{ cv.name }}
            </h2>

            <p class="mt-2 text-lg">{{ cv.job }}</p>

            <p class="mt-3 text-sm text-slate-500">
              {{ cv.email }} · {{ cv.phone }} · {{ cv.location }}
            </p>
          </header>

          <!-- Summary -->
          <section class="mb-8">
            <h2
              :class="[
                'mb-3 border-b pb-2 text-lg font-bold uppercase',
                template.heading,
              ]"
            >
              Professional Summary
            </h2>

            <p class="text-sm leading-7">
              {{ cv.summary }}
            </p>
          </section>

          <!-- Experience -->
          <section class="mb-8">
            <h2
              :class="[
                'mb-4 border-b pb-2 text-lg font-bold uppercase',
                template.heading,
              ]"
            >
              Experience
            </h2>

            <article
              v-for="item in cv.experiences"
              :key="item.role"
              class="mb-5"
            >
              <h3 class="font-bold">{{ item.role }}</h3>
              <p class="mt-1 text-sm">
                {{ item.company }} · {{ item.period }}
              </p>
              <p class="mt-2 text-sm leading-6">
                {{ item.description }}
              </p>
            </article>
          </section>

          <!-- Education -->
          <section class="mb-8">
            <h2
              :class="[
                'mb-4 border-b pb-2 text-lg font-bold uppercase',
                template.heading,
              ]"
            >
              Education
            </h2>

            <article
              v-for="item in cv.education"
              :key="item.course"
              class="mb-4"
            >
              <h3 class="font-bold">{{ item.course }}</h3>
              <p class="mt-1 text-sm">
                {{ item.school }} · {{ item.period }}
              </p>
            </article>
          </section>

          <!-- Skills -->
          <section>
            <h2
              :class="[
                'mb-4 border-b pb-2 text-lg font-bold uppercase',
                template.heading,
              ]"
            >
              Skills
            </h2>

            <div class="flex flex-wrap gap-2">
              <span
                v-for="skill in cv.skills"
                :key="skill"
                class="rounded border border-slate-200 px-3 py-1.5 text-sm"
              >
                {{ skill }}
              </span>
            </div>
          </section>
        </main>
      </div>
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

  .cv-print-area {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    max-width: none;
    box-shadow: none;
  }

  .no-print {
    display: none !important;
  }
}
</style>