<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

import {
  UserRound,
  GraduationCap,
  BriefcaseBusiness,
  Wrench,
  Eye,
  Upload,
  Trash2,
  MapPin,
  ArrowLeft,
  ArrowRight,
  Palette,
  Download,
} from 'lucide-vue-next'

import DashboardLayout from '../components/layout/DashboardLayout.vue'

const route = useRoute()

const currentStep = ref(1)

const selectedTemplate = computed(() => {
  return Number(route.query.template) || 1
})

const form = ref({
  fullName: 'Raymond Mwangi',
  email: 'john.mwangi@email.com',
  phone: '+255 624023920',
  location: 'Dar es Salaam, Tanzania',
  professionalTitle: 'Marketing Specialist',
  summary:
    'Results-driven marketing professional with 5+ years of experience in digital marketing, brand strategy and social media management',
  photo: null,
})

const steps = [
  { number: 1, label: 'Personal Info', icon: UserRound },
  { number: 2, label: 'Education', icon: GraduationCap },
  { number: 3, label: 'Experience', icon: BriefcaseBusiness },
  { number: 4, label: 'Skills', icon: Wrench },
  { number: 5, label: 'Review', icon: Eye },
]

const nextStep = () => {
  if (currentStep.value < steps.length) {
    currentStep.value++
  }
}

const previousStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const uploadPhoto = (event) => {
  const file = event.target.files[0]

  if (file) {
    form.value.photo = URL.createObjectURL(file)
  }
}

const removePhoto = () => {
  form.value.photo = null
}
</script>

<template>
  <DashboardLayout>
    <div class="space-y-5">

    <!-- Page Header -->
    <div
      class="rounded-2xl border border-orange-200 bg-white px-5 py-4 shadow-sm"
    >
      <h1
        class="text-3xl font-extrabold"
        style="color: var(--color-text)"
      >
        CV Builder
      </h1>

      <p
        class="mt-1 text-sm"
      >
        Create and manage your professional CV. Fill in your details step by step
        or use a template to get started quickly
      </p>
    </div>

    <!-- Main Content -->
    <div class="grid grid-cols-1 items-start gap-5 xl:grid-cols-[1fr_460px]">

      <!-- LEFT SIDE -->
      <div class="space-y-4">

        <!-- Steps -->
        <div
          class="overflow-x-auto rounded-2xl border border-orange-200 bg-white px-4 py-3 shadow-sm"
        >
          <div class="flex min-w-max items-center justify-between gap-2">
            <button
              v-for="step in steps"
              :key="step.number"
              type="button"
              class="flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium transition"
              :class="
                currentStep === step.number
                  ? 'bg-primary-50 text-primary'
                  : currentStep > step.number
                    ? 'text-primary'
                    : 'text-gray-500'
              "
              @click="currentStep = step.number"
            >
              <span
                class="flex h-6 w-6 items-center justify-center rounded-full text-xs"
                :class="
                  currentStep === step.number
                    ? 'bg-primary text-white'
                    : currentStep > step.number
                      ? 'bg-primary text-white'
                      : 'bg-gray-100 text-gray-600'
                "
              >
                {{ step.number }}
              </span>

              <span>{{ step.label }}</span>
            </button>
          </div>
        </div>

        <!-- Personal Details -->
        <div
          v-if="currentStep === 1"
          class="rounded-2xl border border-orange-200 bg-white p-5 shadow-sm"
        >
          <!-- Section Header -->
          <div class="mb-5 flex items-center gap-3">
            <div
              class="flex h-10 w-10 items-center justify-center rounded-full bg-primary-50 text-primary"
            >
              <UserRound :size="21" />
            </div>

            <div>
              <h2
                class="text-lg font-bold"
                style="color: var(--color-text)"
              >
                Personal Details
              </h2>

              <p
                class="text-xs"
                style="color: var(--color-text-muted)"
              >
                Start with your basic details. This information will appear at the top of your CV.
              </p>
            </div>
          </div>

          <!-- Form -->
          <div class="grid gap-4 md:grid-cols-2">

            <!-- Full Name -->
            <div>
              <label class="mb-1.5 block text-sm font-medium">
                Full Name <span class="text-red-500">*</span>
              </label>

              <input
                v-model="form.fullName"
                type="text"
                class="w-full rounded-lg border px-3 py-2.5 text-sm outline-none transition focus:border-primary"
                style="border-color: var(--color-border)"
              />
            </div>

            <!-- Email -->
            <div>
              <label class="mb-1.5 block text-sm font-medium">
                Email Address <span class="text-red-500">*</span>
              </label>

              <input
                v-model="form.email"
                type="email"
                class="w-full rounded-lg border px-3 py-2.5 text-sm outline-none transition focus:border-primary"
                style="border-color: var(--color-border)"
              />
            </div>

            <!-- Phone -->
            <div>
              <label class="mb-1.5 block text-sm font-medium">
                Phone Number <span class="text-red-500">*</span>
              </label>

              <input
                v-model="form.phone"
                type="text"
                class="w-full rounded-lg border px-3 py-2.5 text-sm outline-none transition focus:border-primary"
                style="border-color: var(--color-border)"
              />
            </div>

            <!-- Location -->
            <div>
              <label class="mb-1.5 block text-sm font-medium">
                Location <span class="text-red-500">*</span>
              </label>

              <div class="relative">
                <MapPin
                  :size="17"
                  class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"
                />

                <input
                  v-model="form.location"
                  type="text"
                  class="w-full rounded-lg border py-2.5 pl-9 pr-3 text-sm outline-none transition focus:border-primary"
                  style="border-color: var(--color-border)"
                />
              </div>
            </div>

            <!-- Professional Title -->
            <div class="md:col-span-2">
              <label class="mb-1.5 block text-sm font-medium">
                Professional Title <span class="text-red-500">*</span>
              </label>

              <input
                v-model="form.professionalTitle"
                type="text"
                class="w-full rounded-lg border px-3 py-2.5 text-sm outline-none transition focus:border-primary"
                style="border-color: var(--color-border)"
              />
            </div>

            <!-- Summary -->
            <div class="md:col-span-2">
              <label class="mb-1.5 block text-sm font-medium">
                Professional Summary
              </label>

              <textarea
                v-model="form.summary"
                rows="4"
                maxlength="300"
                class="w-full resize-none rounded-lg border px-3 py-2.5 text-sm outline-none transition focus:border-primary"
                style="border-color: var(--color-border)"
              />

              <div class="mt-1 text-right text-xs text-gray-400">
                {{ form.summary.length }}/300
              </div>
            </div>

            <!-- Profile Photo -->
            <div class="md:col-span-2">
              <label class="mb-1.5 block text-sm font-medium">
                Profile Photo
              </label>

              <p class="mb-3 text-xs" style="color: var(--color-text-muted)">
                Add a professional photo (JPG, PNG)
              </p>

              <div class="flex items-center gap-4">

                <div
                  class="relative flex h-20 w-20 items-center justify-center overflow-hidden rounded-full border bg-gray-100"
                  style="border-color: var(--color-border)"
                >
                  <img
                    v-if="form.photo"
                    :src="form.photo"
                    alt="Profile"
                    class="h-full w-full object-cover"
                  />

                  <UserRound
                    v-else
                    :size="32"
                    class="text-gray-400"
                  />
                </div>

                <div class="flex flex-col gap-2 sm:flex-row">

                  <label
                    class="inline-flex cursor-pointer items-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-semibold text-white transition hover:opacity-90"
                  >
                    <Upload :size="17" />
                    Upload Photo

                    <input
                      type="file"
                      accept="image/png,image/jpeg"
                      class="hidden"
                      @change="uploadPhoto"
                    />
                  </label>

                  <button
                    v-if="form.photo"
                    type="button"
                    class="inline-flex items-center gap-2 rounded-lg border px-4 py-2.5 text-sm font-medium"
                    style="border-color: var(--color-border)"
                    @click="removePhoto"
                  >
                    <Trash2 :size="16" />
                    Remove
                  </button>

                </div>
              </div>

              <p class="mt-2 text-xs text-gray-400">
                Recommended size: 400px × 400px
              </p>
            </div>
          </div>

          <!-- Footer -->
          <div
            class="mt-7 flex items-center justify-between border-t pt-4"
            style="border-color: var(--color-border)"
          >
            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-lg border px-4 py-2.5 text-sm font-medium"
              style="border-color: var(--color-border)"
            >
              <ArrowLeft :size="16" />
              Cancel
            </button>

            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-lg bg-primary px-5 py-2.5 text-sm font-semibold text-white transition hover:opacity-90"
              @click="nextStep"
            >
              Save & Continue
              <ArrowRight :size="16" />
            </button>
          </div>
        </div>

        <!-- Placeholder for other steps -->
        <div
          v-else
          class="rounded-2xl border border-orange-200 bg-white p-10 text-center shadow-sm"
        >
          <h2
            class="text-xl font-bold"
            style="color: var(--color-text)"
          >
            {{ steps[currentStep - 1].label }}
          </h2>

          <p
            class="mt-2 text-sm"
            style="color: var(--color-text-muted)"
          >
            This section will be implemented next.
          </p>

          <button
            type="button"
            class="mt-5 inline-flex items-center gap-2 rounded-lg border px-4 py-2.5 text-sm"
            style="border-color: var(--color-border)"
            @click="previousStep"
          >
            <ArrowLeft :size="16" />
            Previous
          </button>
        </div>
      </div>

      <!-- RIGHT SIDE - CV PREVIEW -->
      <div
        class="rounded-2xl border bg-white p-4 shadow-sm"
        style="border-color: var(--color-border)"
      >
        <div class="mb-4 flex items-center justify-between">
          <h2
            class="font-bold"
            style="color: var(--color-text)"
          >
            CV Preview
          </h2>

          <div class="flex gap-2">
            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-lg border px-3 py-2 text-xs font-medium"
              style="border-color: var(--color-border)"
            >
              <Palette :size="14" />
              Change Template
            </button>

            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-lg bg-primary px-3 py-2 text-xs font-semibold text-white"
            >
              <Download :size="14" />
              Download PDF
            </button>
          </div>
        </div>

        <!-- CV Paper -->
        <div
          class="mx-auto overflow-hidden rounded-lg border bg-white shadow-md"
          style="max-width: 360px; border-color: var(--color-border)"
        >
          <div class="grid grid-cols-[115px_1fr] min-h-[500px]">

            <!-- CV Sidebar -->
            <div class="bg-slate-900 p-4 text-white">

              <div class="mb-4 flex justify-center">
                <div
                  class="flex h-20 w-20 items-center justify-center overflow-hidden rounded-full border-4 border-white/30 bg-slate-700"
                >
                  <img
                    v-if="form.photo"
                    :src="form.photo"
                    alt="Profile"
                    class="h-full w-full object-cover"
                  />

                  <UserRound
                    v-else
                    :size="30"
                    class="text-white/60"
                  />
                </div>
              </div>

              <h3 class="text-center text-sm font-bold">
                {{ form.fullName || 'Your Name' }}
              </h3>

              <p class="mt-1 text-center text-[9px] text-white/70">
                {{ form.professionalTitle || 'Professional Title' }}
              </p>

              <div class="mt-5 space-y-3 text-[8px] text-white/75">
                <div>
                  <p class="mb-1 font-bold text-white">CONTACT</p>
                  <p>{{ form.phone }}</p>
                  <p class="mt-1 break-words">{{ form.email }}</p>
                  <p class="mt-1">{{ form.location }}</p>
                </div>

                <div>
                  <p class="mb-1 font-bold text-white">SKILLS</p>
                  <p>Digital Marketing</p>
                  <p>Social Media Management</p>
                  <p>SEO & SEM</p>
                  <p>Content Creation</p>
                </div>

                <div>
                  <p class="mb-1 font-bold text-white">LANGUAGES</p>
                  <p>English</p>
                  <p class="mt-1">Swahili</p>
                </div>
              </div>
            </div>

            <!-- CV Main -->
            <div class="p-4 text-[9px] text-slate-700">

              <section>
                <h4 class="border-b pb-1 text-[10px] font-bold uppercase">
                  Professional Summary
                </h4>

                <p class="mt-2 leading-relaxed">
                  {{ form.summary }}
                </p>
              </section>

              <section class="mt-5">
                <h4 class="border-b pb-1 text-[10px] font-bold uppercase">
                  Work Experience
                </h4>

                <div class="mt-2">
                  <div class="flex justify-between gap-2">
                    <strong>Marketing Specialist</strong>
                    <span class="text-[8px]">2021 - Present</span>
                  </div>

                  <p class="mt-1 text-[8px] font-medium">
                    ABC Company Ltd, Dar es Salaam
                  </p>

                  <ul class="mt-1 list-disc space-y-1 pl-3 text-[8px]">
                    <li>Develop and execute digital marketing campaigns</li>
                    <li>Manage social media platforms and content</li>
                    <li>Analyze campaign performance and reporting</li>
                  </ul>
                </div>

                <div class="mt-4">
                  <div class="flex justify-between gap-2">
                    <strong>Marketing Officer</strong>
                    <span class="text-[8px]">2018 - 2021</span>
                  </div>

                  <p class="mt-1 text-[8px] font-medium">
                    XYZ Company, Dar es Salaam
                  </p>

                  <ul class="mt-1 list-disc space-y-1 pl-3 text-[8px]">
                    <li>Assisted in developing marketing strategies</li>
                    <li>Managed customer communications</li>
                  </ul>
                </div>
              </section>

              <section class="mt-5">
                <h4 class="border-b pb-1 text-[10px] font-bold uppercase">
                  Education
                </h4>

                <div class="mt-2 flex justify-between gap-2">
                  <div>
                    <strong>Bachelor of Business Administration</strong>
                    <p class="text-[8px]">University of Dar es Salaam</p>
                  </div>

                  <span class="text-[8px]">2014 - 2017</span>
                </div>
              </section>

              <section class="mt-5">
                <h4 class="border-b pb-1 text-[10px] font-bold uppercase">
                  Certifications
                </h4>

                <p class="mt-2 font-medium">Digital Marketing Certification</p>
                <p class="text-[8px]">Google Digital Garage</p>

                <p class="mt-2 font-medium">Social Media Marketing</p>
                <p class="text-[8px]">HubSpot Academy</p>
              </section>

            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
  </DashboardLayout>
</template>