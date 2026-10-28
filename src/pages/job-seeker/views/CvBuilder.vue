<script setup>
import { ref } from 'vue'
import { Check, Eye, ArrowRight } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const router = useRouter()

const selectedTemplate = ref(null)

const templates = [
  {
    id: 1,
    name: 'Professional',
    description: 'Clean and professional design suitable for most careers.',
    preview: 'professional',
  },
  {
    id: 2,
    name: 'Modern',
    description: 'Modern layout with a stylish and attractive appearance.',
    preview: 'modern',
  },
  {
    id: 3,
    name: 'Minimal',
    description: 'Simple and clean design focused on your experience.',
    preview: 'minimal',
  },
  {
    id: 4,
    name: 'Creative',
    description: 'Creative layout for designers and creative professionals.',
    preview: 'creative',
  },
  {
    id: 5,
    name: 'Simple',
    description: 'Straightforward and easy-to-read professional CV.',
    preview: 'simple',
  },
]

const chooseTemplate = (template) => {
  selectedTemplate.value = template
}

const useTemplate = (template) => {
  router.push({
    path: '/dashboard/cv-template',
    query: {
      template: template.id,
    },
  })
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-5">

      <!-- Header -->
      <section  class="rounded-2xl border border-orange-200 bg-white px-5 py-4 shadow-sm">
        <h1
          class="text-3xl font-extrabold"
          style="color: var(--color-text)"
        >
          Choose a CV Template
        </h1>

        <p
          class="mt-1 text-sm"
        >
          Choose a professional CV template that matches your career and
          personality. You can customize your information after selecting
          a template.
        </p>
      </section>

      <!-- Templates -->
      <section class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">

        <div
          v-for="template in templates"
          :key="template.id"
          @click="chooseTemplate(template)"
          class="group relative cursor-pointer overflow-hidden rounded-2xl border bg-white transition hover:-translate-y-1 hover:shadow-lg"
          :class="
            selectedTemplate?.id === template.id
              ? 'border-primary ring-2 ring-primary/20'
              : 'border-orange-200'
          "
        >

          <!-- Selected -->
          <div
            v-if="selectedTemplate?.id === template.id"
            class="absolute left-3 top-3 z-10 flex h-7 w-7 items-center justify-center rounded-full bg-primary text-white shadow"
          >
            <Check :size="16" />
          </div>

          <!-- Preview -->
          <div class="relative h-[330px] overflow-hidden bg-slate-100 p-5">

            <!-- Professional -->
            <div
              v-if="template.preview === 'professional'"
              class="mx-auto h-full max-w-[220px] overflow-hidden bg-white shadow-md"
            >
              <div class="h-12 bg-blue-700"></div>

              <div class="p-4">
                <div class="h-3 w-28 rounded bg-slate-800"></div>
                <div class="mt-2 h-2 w-20 rounded bg-slate-300"></div>

                <div class="mt-6 grid grid-cols-[55px_1fr] gap-3">
                  <div>
                    <div class="h-14 w-14 rounded-full bg-slate-200"></div>
                    <div class="mt-4 h-2 w-12 rounded bg-slate-300"></div>
                    <div class="mt-2 h-2 w-10 rounded bg-slate-200"></div>
                    <div class="mt-2 h-2 w-12 rounded bg-slate-200"></div>
                  </div>

                  <div>
                    <div class="h-2 w-20 rounded bg-blue-700"></div>
                    <div class="mt-2 h-1.5 w-full rounded bg-slate-200"></div>
                    <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                    <div class="mt-1 h-1.5 w-4/5 rounded bg-slate-200"></div>

                    <div class="mt-5 h-2 w-16 rounded bg-blue-700"></div>
                    <div class="mt-2 h-1.5 w-full rounded bg-slate-200"></div>
                    <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Modern -->
            <div
              v-else-if="template.preview === 'modern'"
              class="mx-auto h-full max-w-[220px] overflow-hidden bg-white shadow-md"
            >
              <div class="flex h-full">
                <div class="w-16 bg-orange-600"></div>

                <div class="flex-1 p-4">
                  <div class="h-3 w-24 rounded bg-slate-800"></div>
                  <div class="mt-2 h-2 w-16 rounded bg-orange-200"></div>

                  <div class="mt-7 h-2 w-20 rounded bg-orange-600"></div>
                  <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                  <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                  <div class="mt-1 h-1.5 w-4/5 rounded bg-slate-200"></div>

                  <div class="mt-6 h-2 w-16 rounded bg-orange-600"></div>
                  <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                  <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                </div>
              </div>
            </div>

            <!-- Minimal -->
            <div
              v-else-if="template.preview === 'minimal'"
              class="mx-auto h-full max-w-[220px] overflow-hidden bg-white p-5 shadow-md"
            >
              <div class="h-3 w-32 rounded bg-slate-900"></div>
              <div class="mt-2 h-2 w-20 rounded bg-slate-300"></div>

              <div class="mt-7 border-t border-slate-200 pt-4">
                <div class="h-2 w-20 rounded bg-slate-800"></div>
                <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-4/5 rounded bg-slate-200"></div>
              </div>

              <div class="mt-6 border-t border-slate-200 pt-4">
                <div class="h-2 w-24 rounded bg-slate-800"></div>
                <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
              </div>

              <div class="mt-6 border-t border-slate-200 pt-4">
                <div class="h-2 w-16 rounded bg-slate-800"></div>
                <div class="mt-3 h-1.5 w-3/4 rounded bg-slate-200"></div>
              </div>
            </div>

            <!-- Creative -->
            <div
              v-else-if="template.preview === 'creative'"
              class="mx-auto h-full max-w-[220px] overflow-hidden bg-white shadow-md"
            >
              <div class="h-20 bg-purple-700 p-4">
                <div class="h-3 w-24 rounded bg-white/90"></div>
                <div class="mt-2 h-2 w-16 rounded bg-white/50"></div>
              </div>

              <div class="p-4">
                <div class="flex gap-3">
                  <div class="h-14 w-14 rounded-xl bg-purple-100"></div>

                  <div class="flex-1">
                    <div class="h-2 w-20 rounded bg-purple-700"></div>
                    <div class="mt-2 h-1.5 w-full rounded bg-slate-200"></div>
                    <div class="mt-1 h-1.5 w-4/5 rounded bg-slate-200"></div>
                  </div>
                </div>

                <div class="mt-6 h-2 w-20 rounded bg-purple-700"></div>
                <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-3/4 rounded bg-slate-200"></div>
              </div>
            </div>

            <!-- Simple -->
            <div
              v-else
              class="mx-auto h-full max-w-[220px] overflow-hidden bg-white p-5 shadow-md"
            >
              <div class="text-center">
                <div class="mx-auto h-3 w-28 rounded bg-slate-900"></div>
                <div class="mx-auto mt-2 h-2 w-20 rounded bg-slate-300"></div>
              </div>

              <div class="mt-7">
                <div class="h-2 w-16 rounded bg-slate-800"></div>
                <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-3/4 rounded bg-slate-200"></div>

                <div class="mt-6 h-2 w-20 rounded bg-slate-800"></div>
                <div class="mt-3 h-1.5 w-full rounded bg-slate-200"></div>
                <div class="mt-1 h-1.5 w-full rounded bg-slate-200"></div>
              </div>
            </div>

          </div>

          <!-- Info -->
          <div class="p-4">
            <div class="flex items-start justify-between gap-3">

              <div>
                <h2
                  class="font-bold"
                  style="color: var(--color-text)"
                >
                  {{ template.name }}
                </h2>

                <p
                  class="mt-1 text-xs leading-5"
                  style="color: var(--color-text-muted)"
                >
                  {{ template.description }}
                </p>
              </div>

              <button
                type="button"
                class="shrink-0 rounded-lg border p-2 text-slate-500 transition hover:bg-slate-50"
                title="Preview template"
                @click.stop="chooseTemplate(template)"
              >
                <Eye :size="17" />
              </button>

            </div>

            <button
              type="button"
              class="mt-4 flex w-full items-center justify-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-semibold text-white transition hover:opacity-90"
              @click.stop="useTemplate(template)"
            >
              Use Template
              <ArrowRight :size="16" />
            </button>
          </div>

        </div>
      </section>

    </div>
  </DashboardLayout>
</template>