<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Download } from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as cvApi from '../../../api/cv'

const route = useRoute()
const router = useRouter()

// Presentational layout metadata only (not CV data) for the 5 templates CvTemplate.vue builds.
// Keyed by the real template code, not templateId - the backend's numeric ids follow DB
// seed/insert order (CLASSIC, MODERN, PROFESSIONAL, MINIMAL, GRADUATE), not this list's order.
const LAYOUTS = {
  PROFESSIONAL: { name: 'Professional', layout: 'sidebar', sidebar: 'bg-blue-700', heading: 'text-blue-700' },
  MODERN: { name: 'Modern', layout: 'sidebar', sidebar: 'bg-orange-600', heading: 'text-orange-600' },
  MINIMAL: { name: 'Minimal', layout: 'minimal', sidebar: 'bg-slate-100', heading: 'text-slate-800' },
  GRADUATE: { name: 'Creative', layout: 'sidebar', sidebar: 'bg-purple-700', heading: 'text-purple-700' },
  CLASSIC: { name: 'Simple', layout: 'minimal', sidebar: 'bg-slate-100', heading: 'text-slate-800' },
}

const loading = ref(true)
const record = ref(null)

onMounted(async () => {
  const cvId = route.query.cv
  if (!cvId) {
    router.replace('/dashboard/my-cvs')
    return
  }
  try {
    record.value = await cvApi.getCv(cvId)
  } catch {
    router.replace('/dashboard/my-cvs')
    return
  } finally {
    loading.value = false
  }
})

const template = computed(() => LAYOUTS[record.value?.templateCode] || LAYOUTS.PROFESSIONAL)

// Normalizes the CV record's field names (fullName/jobTitle/degree/...) into the shape
// this view's existing markup expects (name/role/course/...).
const cv = computed(() => {
  if (!record.value) return null
  return {
    name: record.value.fullName,
    job: record.value.professionalTitle,
    email: record.value.email,
    phone: record.value.phone,
    location: record.value.location,
    summary: record.value.summary,
    skills: record.value.skills,
    experiences: (record.value.experiences || []).map((item) => ({
      role: item.jobTitle,
      company: item.company,
      period: `${item.startDate} - ${item.endDate}`,
      description: item.description,
    })),
    education: (record.value.education || []).map((item) => ({
      course: item.degree,
      school: item.institution,
      period: `${item.startYear} - ${item.endYear}`,
    })),
  }
})

function backToMyCvs() {
  router.push('/dashboard/my-cvs')
}

async function downloadPdf() {
  const cvId = route.query.cv
  try {
    const blob = await cvApi.downloadCv(cvId)
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `cv-${cvId}.pdf`
    link.click()
    URL.revokeObjectURL(url)
  } catch {
    // The rendered PDF endpoint failed - fall back to the browser's own print-to-PDF.
    window.print()
  }
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

      <!-- Loading -->
      <div v-if="loading" class="mx-auto w-full max-w-4xl">
        <Skeleton variant="card" height="32rem" />
      </div>

      <!-- CV Preview -->
      <div
        v-else-if="cv"
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
            v-if="record?.templateCode === 'MINIMAL'"
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