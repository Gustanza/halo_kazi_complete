<script setup>
import { Compass } from 'lucide-vue-next'
import BrandLogo from '../../../components/common/BrandLogo.vue'
import { useSession } from '../../../auth/session'

const session = useSession()
</script>

<template>
  <div class="flex min-h-screen flex-col items-center justify-center px-4 text-center" style="background-color: var(--color-bg)">
    <RouterLink to="/" class="mb-8 flex items-center gap-2.5">
      <BrandLogo :size="40" />
      <span class="flex flex-col items-start leading-tight">
        <span class="text-2xl font-extrabold text-primary">Halokazi</span>
        <span class="text-[11px] font-medium" style="color: var(--color-text-muted)">Kazi bora, maisha bora</span>
      </span>
    </RouterLink>

    <div class="card-surface max-w-md p-10">
      <div class="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-primary-50 text-primary dark:bg-white/10">
        <Compass :size="26" />
      </div>
      <h1 class="mt-5 text-2xl font-extrabold" style="color: var(--color-text)">Page not found</h1>
      <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">
        The page you're looking for doesn't exist or may have moved.
      </p>
      <RouterLink :to="session.isAuthenticated.value ? '/dashboard' : '/'" class="btn-primary mt-6">
        {{ session.isAuthenticated.value ? 'Back to Dashboard' : 'Back to Home' }}
      </RouterLink>
    </div>
  </div>
</template>
