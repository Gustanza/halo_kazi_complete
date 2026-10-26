<script setup>
import { ref } from 'vue'
import {
  Briefcase,
  Building2,
  Users,
  Clock3,
  UserRound,
  FileText,
  Send,
  BarChart3,
  Bell,
  TrendingUp,
  ShieldCheck,
  ArrowRight,
  LayoutTemplate,
  Eye,
  Download,
  Smile,
  Target,
  Sparkles,
} from 'lucide-vue-next'

import StatPill from '../../../components/common/StatPill.vue'
import FeatureCard from '../../../components/common/FeatureCard.vue'
import SectionHeading from '../../../components/common/SectionHeading.vue'
import CountUpValue from '../../../components/common/CountUpValue.vue'
import GlowOrbs from '../../../components/common/GlowOrbs.vue'
import CtaBanner from '../components/layout/CtaBanner.vue'
import JobCard from '../components/jobs/JobCard.vue'
import FilterSidebar from '../components/jobs/FilterSidebar.vue'
import Pagination from '../components/jobs/Pagination.vue'
import EmployerCard from '../components/employers/EmployerCard.vue'
import TemplateThumb from '../components/cv/TemplateThumb.vue'
import StepCard from '../components/cv/StepCard.vue'

import { jobs, totalJobPages } from '../data/jobs'
import { employers } from '../data/employers'
import { cvTemplates } from '../data/cvTemplates'

const heroStats = [
  { icon: Briefcase, value: '10K+', label: 'New jobs every day' },
  { icon: Building2, value: '5K+', label: 'Registered employers' },
  { icon: Users, value: '50K+', label: 'Job seekers' },
  { icon: Clock3, value: '24/7', label: 'Support' },
]

const helpFeatures = [
  { icon: UserRound, iconBg: '#FD5C20', title: 'Search Jobs', description: 'Search thousands of jobs from various companies' },
  { icon: FileText, iconBg: '#B064F7', title: 'Build Your CV', description: 'Use the CV builder to create a professional CV in minutes' },
  { icon: Send, iconBg: '#2FB573', title: 'Submit Application', description: 'Submit applications directly to employers through our platform' },
  { icon: BarChart3, iconBg: '#2F8FF5', title: 'Track Application', description: 'Track your applications directly through our platform' },
]

const currentPage = ref(1)

const whyEmployerFeatures = [
  { icon: Users, iconBg: '#FD5C20', title: 'Access Top Talent', description: 'Reach thousands of qualified candidates across Tanzania in one place' },
  { icon: Send, iconBg: '#FD5C20', title: 'Easy recruitment', description: 'Post jobs, get best team and hire faster with our platform' },
  { icon: TrendingUp, iconBg: '#FD5C20', title: 'Smart matching', description: 'One platform connects you with the right candidates for your needs' },
  { icon: ShieldCheck, iconBg: '#FD5C20', title: 'Trusted platform', description: 'Verified employers and secure hiring process you can rely on' },
]

const selectedTemplate = ref(cvTemplates[0].id)

const cvSteps = [
  { icon: LayoutTemplate, title: 'Choose template', description: 'Pick a professional template you like' },
  { icon: UserRound, title: 'Add your details', description: 'Fill in your personal information, experience, education and skills' },
  { icon: Eye, title: 'Preview & Edit', description: 'Preview your CV and make changes until you satisfied' },
  { icon: Download, title: 'Download & share', description: 'Download your CV as a PDF and start sharing' },
]

const aboutStats = [
  { icon: Users, value: '50K+', label: 'Job Seekers', sub: 'Trust Halokazi' },
  { icon: Building2, value: '5K+', label: 'Companies', sub: 'Hiring on Halokazi' },
  { icon: Briefcase, value: '10K+', label: 'Jobs posted', sub: 'Every month' },
  { icon: Smile, value: '98%', label: 'Employer', sub: 'Satisfaction Rate' },
]
</script>

<template>
  <!-- HOME -->
  <section id="home" class="relative overflow-hidden">
    <GlowOrbs variant="mixed" />

    <div class="container-page relative z-10 grid gap-10 py-10 lg:grid-cols-2 lg:items-center lg:gap-14 lg:py-24">
      <div v-reveal>
        <span class="inline-flex items-center gap-1.5 rounded-full bg-primary-50 px-3.5 py-1.5 text-xs font-semibold text-primary dark:bg-white/10">
          <Sparkles :size="13" />
          Tanzania's modern career platform
        </span>
        <h1 class="mt-5 text-4xl font-extrabold leading-[1.05] tracking-tight sm:text-5xl lg:text-6xl" style="color: var(--color-text)">
          Better Jobs<br />
          Starts
          <span class="relative inline-block text-primary">
            Here
            <svg class="absolute -bottom-2 left-0 w-full" height="10" viewBox="0 0 120 10" fill="none" preserveAspectRatio="none">
              <path d="M2 7.5C20 2.5 100 2.5 118 7.5" stroke="#FD5C20" stroke-width="3" stroke-linecap="round" />
            </svg>
          </span>
        </h1>
        <p class="mt-5 max-w-md text-base leading-relaxed" style="color: var(--color-text-muted)">
          Halokazi is a modern platform for finding jobs, building great CVs, and connecting with employers
        </p>
        <div class="mt-7 flex flex-wrap gap-4">
          <RouterLink to="/register" class="btn-primary">Register Now</RouterLink>
          <a href="#jobs" class="btn-outline">Browse Jobs</a>
        </div>

        <div class="mt-12 grid grid-cols-2 gap-6 sm:grid-cols-4">
          <StatPill v-for="stat in heroStats" :key="stat.label" v-bind="stat" />
        </div>
      </div>

      <div class="relative px-4 sm:px-6 lg:px-0" v-reveal="150">
        <div
          class="aspect-[4/3] w-full overflow-hidden rounded-[2rem] p-6 sm:p-8"
          style="background: linear-gradient(145deg, var(--color-peach-light), var(--color-peach))"
        >
          <!-- "product mockup" card, slightly tilted for a designed, non-static feel -->
          <div
            class="h-full w-full -rotate-3 rounded-2xl bg-white p-4 shadow-2xl shadow-black/10 transition-transform duration-500 hover:rotate-0 dark:bg-slate-900"
          >
            <div class="flex items-center gap-1.5">
              <span class="h-2.5 w-2.5 rounded-full bg-red-400/70" />
              <span class="h-2.5 w-2.5 rounded-full bg-amber-400/70" />
              <span class="h-2.5 w-2.5 rounded-full bg-emerald-400/70" />
              <div class="ml-3 h-6 flex-1 rounded-full" style="background-color: var(--color-bg)" />
            </div>

            <div class="mt-4 space-y-2.5">
              <div
                v-for="row in [
                  { bg: '#FD5C20', w: '72%' },
                  { bg: '#B064F7', w: '58%' },
                  { bg: '#2FB573', w: '64%' },
                ]"
                :key="row.w"
                class="flex items-center gap-3 rounded-xl p-2.5"
                style="background-color: var(--color-bg)"
              >
                <span class="h-8 w-8 shrink-0 rounded-lg" :style="{ backgroundColor: row.bg }" />
                <span class="flex-1 space-y-1.5">
                  <span class="block h-2 rounded-full" :style="{ width: row.w, backgroundColor: 'var(--color-border)' }" />
                  <span class="block h-1.5 w-1/3 rounded-full" style="background-color: var(--color-border); opacity: 0.6" />
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="absolute -left-2 top-4 flex max-w-[210px] items-start gap-3 rounded-2xl p-4 shadow-card animate-float sm:-left-6 sm:top-8" style="background-color: var(--color-surface)">
          <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary text-white">
            <Bell :size="18" />
          </div>
          <div>
            <p class="text-sm font-semibold" style="color: var(--color-text)">Jobs Notification</p>
            <p class="mt-0.5 text-xs" style="color: var(--color-text-muted)">Receive jobs notification that suits you</p>
          </div>
        </div>

        <div
          class="absolute -right-2 bottom-4 flex items-center gap-2 rounded-full py-2 pl-2 pr-4 shadow-card sm:-right-4 sm:bottom-8"
          style="background-color: var(--color-surface); animation: float 4s var(--ease-in-out) infinite; animation-delay: 1.2s"
        >
          <span class="flex h-8 w-8 items-center justify-center rounded-full bg-accent-green text-white">
            <ShieldCheck :size="15" />
          </span>
          <span class="text-xs font-semibold" style="color: var(--color-text)">Verified Employers</span>
        </div>
      </div>
    </div>

    <div class="container-page relative z-10 pb-16 pt-6 lg:pb-28">
      <div v-reveal>
        <SectionHeading title="How Halokazi Helps You" align="center" />
      </div>
      <div class="mt-8 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <FeatureCard v-for="(feature, i) in helpFeatures" :key="feature.title" v-bind="feature" v-reveal="i * 90" />
      </div>
    </div>
  </section>

  <!-- JOBS -->
  <section id="jobs" class="relative overflow-hidden py-16 lg:py-24" style="background-color: var(--color-surface)">
    <GlowOrbs variant="warm" />
    <div class="container-page relative z-10">
      <div v-reveal>
        <span class="eyebrow">Find Work</span>
        <h2 class="mt-2 text-3xl font-extrabold sm:text-4xl" style="color: var(--color-text)">Jobs</h2>
        <p class="mt-2 max-w-md text-base" style="color: var(--color-text-muted)">Find job opportunities that suit you</p>
      </div>

      <div class="mt-10 grid gap-8 lg:grid-cols-3">
        <div class="lg:col-span-2">
          <h3 class="mb-4 text-lg font-semibold" style="color: var(--color-text)">New Jobs</h3>
          <div class="space-y-4">
            <JobCard v-for="(job, i) in jobs" :key="job.id" :job="job" v-reveal="i * 70" />
          </div>
          <div class="mt-8">
            <Pagination v-model="currentPage" :total="totalJobPages" />
          </div>
        </div>

        <FilterSidebar v-reveal="100" />
      </div>
    </div>
  </section>

  <!-- EMPLOYERS -->
  <section id="employers" class="relative overflow-hidden py-16 lg:py-24">
    <GlowOrbs variant="cool" />
    <div class="container-page relative z-10">
      <div class="grid gap-10 lg:grid-cols-2 lg:items-center">
        <div v-reveal>
          <span class="eyebrow">For Businesses</span>
          <h2 class="mt-2 text-3xl font-extrabold leading-tight sm:text-4xl" style="color: var(--color-text)">
            Find Top Employers<br />
            Hiring <span class="text-primary">Right now</span>
          </h2>
          <p class="mt-4 max-w-md text-base" style="color: var(--color-text-muted)">
            Discover trusted companies and exciting career opportunities from top employers across Tanzania
          </p>
        </div>
        <div
          class="relative hidden aspect-[4/3] w-full items-center justify-center overflow-hidden rounded-[2rem] sm:flex"
          style="background: linear-gradient(145deg, var(--color-peach-light), var(--color-purple-tint))"
          v-reveal="150"
        >
          <div class="flex items-end gap-4">
            <div class="flex h-28 w-16 flex-col items-center justify-end gap-1 rounded-t-xl bg-white/70 pb-2 shadow-xl dark:bg-slate-900/60">
              <span class="h-2 w-2 rounded-full bg-primary" />
            </div>
            <div class="flex h-40 w-16 flex-col items-center justify-end gap-1 rounded-t-xl bg-white shadow-xl dark:bg-slate-900">
              <div class="mb-2 flex h-9 w-9 items-center justify-center rounded-full bg-primary text-white shadow-lg">
                <Building2 :size="16" />
              </div>
            </div>
            <div class="flex h-20 w-16 flex-col items-center justify-end gap-1 rounded-t-xl bg-white/70 pb-2 shadow-xl dark:bg-slate-900/60">
              <span class="h-2 w-2 rounded-full" style="background-color: #b064f7" />
            </div>
          </div>
          <div class="absolute left-5 top-5 rounded-full px-3 py-1.5 text-[11px] font-bold shadow-card" style="background-color: var(--color-surface); color: var(--color-text)">
            20+ Jobs Open
          </div>
        </div>
      </div>

      <div class="mt-14 flex items-center justify-between">
        <h3 class="text-xl font-semibold" style="color: var(--color-text)">Top employers</h3>
        <a href="#employers" class="inline-flex items-center gap-1 text-sm font-semibold text-primary transition hover:text-primary-600">
          View all top employers <ArrowRight :size="15" />
        </a>
      </div>
      <div class="mt-6 grid grid-cols-2 gap-5 sm:grid-cols-3 lg:grid-cols-5">
        <EmployerCard v-for="(employer, i) in employers" :key="employer.id" :employer="employer" v-reveal="i * 70" />
      </div>

      <div class="mt-14 rounded-2xl p-6 sm:p-10" style="background-color: var(--color-peach)" v-reveal>
        <h3 class="text-center text-2xl font-extrabold sm:text-3xl" style="color: var(--color-text)">Why employers Choose Halokazi?</h3>
        <div class="mt-8 grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
          <FeatureCard v-for="feature in whyEmployerFeatures" :key="feature.title" v-bind="feature" variant="plain" />
        </div>
      </div>
    </div>
  </section>

  <!-- CV BUILDER -->
  <section id="cv-builder" class="relative overflow-hidden py-16 lg:py-24" style="background-color: var(--color-surface)">
    <GlowOrbs variant="mixed" />
    <div class="container-page relative z-10">
      <div class="grid gap-10 lg:grid-cols-2 lg:items-center">
        <div v-reveal>
          <span class="eyebrow">Build Your CV</span>
          <h2 class="mt-2 text-3xl font-extrabold leading-tight sm:text-4xl" style="color: var(--color-text)">
            Build a Professional<br />
            <span class="text-primary">CV</span> In Minutes
          </h2>
          <p class="mt-4 max-w-md text-base" style="color: var(--color-text-muted)">
            Create a standout CV that gets you noticed. Easy, fast and free
          </p>
          <RouterLink to="/register" class="btn-primary mt-6">Start Building</RouterLink>
        </div>
        <div
          class="hidden aspect-[4/3] w-full items-center justify-center rounded-[2rem] p-8 sm:flex"
          style="background: linear-gradient(145deg, var(--color-peach-light), var(--color-peach))"
          v-reveal="150"
        >
          <div class="h-full w-5/6 rotate-2 rounded-2xl bg-white p-4 shadow-2xl shadow-black/10 transition-transform duration-500 hover:rotate-0 dark:bg-slate-900">
            <div class="flex items-center gap-3 border-b pb-3" style="border-color: var(--color-border)">
              <div class="h-11 w-11 rounded-full" style="background-color: #ffd7c2" />
              <div class="flex-1 space-y-1.5">
                <div class="h-2 w-2/3 rounded-full bg-primary" />
                <div class="h-1.5 w-1/3 rounded-full" style="background-color: var(--color-border)" />
              </div>
            </div>
            <div class="mt-3 space-y-1.5">
              <div class="h-1.5 w-full rounded-full" style="background-color: var(--color-border)" />
              <div class="h-1.5 w-5/6 rounded-full" style="background-color: var(--color-border)" />
              <div class="h-1.5 w-2/3 rounded-full" style="background-color: var(--color-border)" />
            </div>
            <div class="mt-4 grid grid-cols-3 gap-2">
              <div v-for="n in 3" :key="n" class="h-10 rounded-lg" :style="{ backgroundColor: ['#FD5C20', '#B064F7', '#2FB573'][n - 1], opacity: 0.85 }" />
            </div>
          </div>
        </div>
      </div>

      <h3 class="mt-14 text-center text-2xl font-extrabold" style="color: var(--color-text)" v-reveal>Choose a CV Template</h3>
      <div class="mt-6 flex gap-4 overflow-x-auto pb-2">
        <div v-for="(template, i) in cvTemplates" :key="template.id" class="w-28 shrink-0 sm:w-32" v-reveal="i * 50">
          <TemplateThumb
            :template="template"
            :selected="selectedTemplate === template.id"
            @select="selectedTemplate = $event"
          />
        </div>
      </div>

      <div class="mt-14 rounded-2xl p-6 sm:p-10" style="background-color: var(--color-peach)" v-reveal>
        <h3 class="text-center text-2xl font-extrabold sm:text-3xl" style="color: var(--color-text)">How it works</h3>
        <div class="mt-8 grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
          <StepCard v-for="(step, i) in cvSteps" :key="step.title" v-bind="step" :step="i + 1" />
        </div>
      </div>
    </div>
  </section>

  <!-- ABOUT -->
  <section id="about" class="relative overflow-hidden py-16 lg:py-24">
    <GlowOrbs variant="warm" />
    <div class="container-page relative z-10">
      <div class="grid gap-10 lg:grid-cols-2 lg:items-center">
        <div v-reveal>
          <span class="eyebrow">About Halokazi</span>
          <h2 class="mt-2 text-3xl font-extrabold leading-tight sm:text-4xl" style="color: var(--color-text)">
            Connecting Talents<br />
            With <span class="text-primary">Opportunities</span>
          </h2>
          <p class="mt-4 max-w-md text-base" style="color: var(--color-text-muted)">
            Halokazi is a Tanzania's leading online platform that connects Job seekers with employers. Our mission is
            to make job searching smarter, faster and more effective to everyone.
          </p>
        </div>
        <div
          class="relative hidden aspect-[4/3] w-full items-center justify-center overflow-hidden rounded-[2rem] sm:flex"
          style="background: linear-gradient(145deg, var(--color-peach-light), var(--color-purple-tint))"
          v-reveal="150"
        >
          <div class="relative flex items-center">
            <div class="flex h-20 w-20 items-center justify-center rounded-full bg-white text-primary shadow-xl dark:bg-slate-900">
              <Users :size="26" />
            </div>
            <div class="-ml-4 flex h-24 w-24 items-center justify-center rounded-full bg-primary text-white shadow-xl">
              <Briefcase :size="30" />
            </div>
            <div class="-ml-4 flex h-20 w-20 items-center justify-center rounded-full text-white shadow-xl" style="background-color: #b064f7">
              <Building2 :size="26" />
            </div>
          </div>
        </div>
      </div>

      <div class="card-surface mt-14 grid grid-cols-2 gap-6 p-6 sm:grid-cols-4 sm:p-8" v-reveal>
        <div
          v-for="(stat, i) in aboutStats"
          :key="stat.label"
          class="flex flex-col items-center gap-2 text-center sm:items-start sm:text-left"
          :class="i > 0 ? 'sm:border-l sm:pl-6' : ''"
          :style="i > 0 ? 'border-color: var(--color-border)' : ''"
        >
          <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
            <component :is="stat.icon" :size="20" />
          </div>
          <p class="text-2xl font-extrabold" style="color: var(--color-text)">
            <CountUpValue :value="stat.value" />
          </p>
          <p class="text-sm font-medium" style="color: var(--color-text)">{{ stat.label }}</p>
          <p class="text-xs" style="color: var(--color-text-muted)">{{ stat.sub }}</p>
        </div>
      </div>

      <div class="mt-16 grid gap-10 lg:grid-cols-2 lg:items-center">
        <div v-reveal>
          <span class="eyebrow">Our Story</span>
          <h3 class="mt-2 text-3xl font-extrabold leading-tight" style="color: var(--color-text)">
            Build to Solve Real<br />Hiring Challenges
          </h3>
          <p class="mt-4 text-sm leading-relaxed" style="color: var(--color-text-muted)">
            Halokazi was founded with a simple idea: job searching should be easy and accessible to everyone. We
            noticed that many talented people in Tanzania struggle to find the right opportunities, while employers
            struggle to find the right talent.
          </p>
          <p class="mt-3 text-sm leading-relaxed" style="color: var(--color-text-muted)">
            Today, Halokazi is proud to bridge that gap by providing innovative tools, a trusted platform, and real
            opportunities that create change.
          </p>
        </div>

        <div class="grid gap-5 sm:grid-cols-2" v-reveal="150">
          <div class="rounded-2xl p-6" style="background-color: var(--color-peach)">
            <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary text-white">
              <Target :size="20" />
            </div>
            <h4 class="mt-4 text-base font-semibold" style="color: var(--color-text)">Our Mission</h4>
            <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
              To empower people and organizations by connecting talent with meaningful opportunities and enabling
              career growth across Tanzania.
            </p>
          </div>
          <div class="rounded-2xl p-6" style="background-color: var(--color-purple-tint)">
            <div class="flex h-11 w-11 items-center justify-center rounded-full text-white" style="background-color: #b064f7">
              <Eye :size="20" />
            </div>
            <h4 class="mt-4 text-base font-semibold" style="color: var(--color-text)">Our Vision</h4>
            <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
              To be the most trusted career platform in Africa, where everyone can discover opportunities and build a
              better future.
            </p>
          </div>
        </div>
      </div>
    </div>
  </section>

  <CtaBanner
    title="Ready to take the next step in your career?"
    subtitle="Join thousands of Tanzanians finding jobs and hiring top talent through Halokazi"
    button-text="Register for Free"
    to="/register"
  />
</template>
