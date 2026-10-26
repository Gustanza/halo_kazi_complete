<script setup>
import { useRoute } from 'vue-router'
import { Construction } from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'

const route = useRoute()
</script>

<template>
  <DashboardLayout>
    <h1 class="text-2xl font-extrabold sm:text-3xl" style="color: var(--color-text)">{{ route.meta.title }}</h1>
    <p class="mt-1 text-sm" style="color: var(--color-text-muted)">{{ route.meta.description }}</p>

    <div class="card-surface mt-8 flex flex-col items-center gap-3 p-12 text-center">
      <div class="flex h-14 w-14 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
        <Construction :size="24" />
      </div>
      <h2 class="text-lg font-semibold" style="color: var(--color-text)">Coming soon</h2>
      <p class="max-w-sm text-sm" style="color: var(--color-text-muted)">
        This page is still being built. Check back soon — or head back to your Dashboard.
      </p>
      <RouterLink to="/dashboard" class="btn-primary mt-2">Back to Dashboard</RouterLink>
    </div>
  </DashboardLayout>
</template>
