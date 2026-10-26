<script setup>
import { Facebook, Twitter, Linkedin, Instagram } from 'lucide-vue-next'
import BrandLogo from '../../../../components/common/BrandLogo.vue'

const year = new Date().getFullYear()

const jobSeekerLinks = [
  { label: 'Find Jobs', to: { path: '/', hash: '#jobs' } },
  { label: 'CV Builder', to: { path: '/', hash: '#cv-builder' } },
  { label: 'Saved Jobs', to: '/login' },
  { label: 'Job Alerts', to: '/login' },
]
const employerLinks = [
  { label: 'Post a Job', to: { path: '/', hash: '#employers' } },
  { label: 'Browse Candidates', to: { path: '/', hash: '#employers' } },
  { label: 'Employer Login', to: '/login' },
  { label: 'Pricing', to: { path: '/', hash: '#employers' } },
]
const companyLinks = [
  { label: 'About us', to: { path: '/', hash: '#about' } },
  { label: 'Contact', to: { path: '/', hash: '#about' } },
  { label: 'Careers', to: { path: '/', hash: '#about' } },
  { label: 'FAQs', to: { path: '/', hash: '#about' } },
]
</script>

<template>
  <footer class="mt-10 text-slate-300" style="background-color: #0e1730">
    <div class="container-bleed py-10">
      <div class="flex flex-col gap-10 lg:flex-row lg:justify-between">
        <div class="lg:max-w-xs">
          <div class="flex items-center gap-2.5">
            <BrandLogo :size="36" />
            <div class="flex flex-col leading-tight">
              <span class="text-xl font-extrabold text-white">Halokazi</span>
              <span class="text-[11px] font-medium text-slate-400">Kazi bora, maisha bora</span>
            </div>
          </div>
          <p class="mt-3 text-sm text-slate-400">
            A modern platform for finding jobs, building great CVs, and connecting with employers across Tanzania.
          </p>
          <div class="mt-4 flex items-center gap-3">
            <a href="#" aria-label="Facebook" class="flex h-9 w-9 items-center justify-center rounded-full bg-white/10 transition hover:bg-primary hover:text-white">
              <Facebook :size="16" />
            </a>
            <a href="#" aria-label="X" class="flex h-9 w-9 items-center justify-center rounded-full bg-white/10 transition hover:bg-primary hover:text-white">
              <Twitter :size="16" />
            </a>
            <a href="#" aria-label="LinkedIn" class="flex h-9 w-9 items-center justify-center rounded-full bg-white/10 transition hover:bg-primary hover:text-white">
              <Linkedin :size="16" />
            </a>
            <a href="#" aria-label="Instagram" class="flex h-9 w-9 items-center justify-center rounded-full bg-white/10 transition hover:bg-primary hover:text-white">
              <Instagram :size="16" />
            </a>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-x-8 gap-y-10 sm:grid-cols-3 sm:gap-x-16">
          <div>
            <h4 class="text-sm font-semibold text-white">For Job Seekers</h4>
            <ul class="mt-4 space-y-3">
              <li v-for="link in jobSeekerLinks" :key="link.label">
                <RouterLink :to="link.to" class="text-sm text-slate-400 transition hover:text-primary">{{ link.label }}</RouterLink>
              </li>
            </ul>
          </div>

          <div>
            <h4 class="text-sm font-semibold text-white">For Employers</h4>
            <ul class="mt-4 space-y-3">
              <li v-for="link in employerLinks" :key="link.label">
                <RouterLink :to="link.to" class="text-sm text-slate-400 transition hover:text-primary">{{ link.label }}</RouterLink>
              </li>
            </ul>
          </div>

          <div>
            <h4 class="text-sm font-semibold text-white">Company</h4>
            <ul class="mt-4 space-y-3">
              <li v-for="link in companyLinks" :key="link.label">
                <RouterLink :to="link.to" class="text-sm text-slate-400 transition hover:text-primary">{{ link.label }}</RouterLink>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="mt-10 flex flex-col items-center justify-between gap-3 border-t border-white/10 pt-6 text-xs text-slate-500 sm:flex-row">
        <p>&copy; {{ year }} Halokazi. All rights reserved.</p>
        <div class="flex items-center gap-5">
          <a href="#" class="transition hover:text-primary">Privacy Policy</a>
          <a href="#" class="transition hover:text-primary">Terms of Service</a>
          <span>Made in Tanzania</span>
        </div>
      </div>
    </div>
  </footer>
</template>
