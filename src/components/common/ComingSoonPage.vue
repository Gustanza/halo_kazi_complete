<script setup>
import { useRouter } from 'vue-router'
import { Construction, LogOut } from 'lucide-vue-next'
import BrandLogo from './BrandLogo.vue'
import { useSession } from '../../auth/session'

const router = useRouter()
const session = useSession()

async function logOut() {
  await session.logout()
  router.replace('/login')
}

defineProps({
  portalName: { type: String, required: true },
  description: { type: String, required: true },
})
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
        <Construction :size="26" />
      </div>
      <h1 class="mt-5 text-2xl font-extrabold" style="color: var(--color-text)">{{ portalName }} portal</h1>
      <p class="mt-2 text-sm leading-relaxed" style="color: var(--color-text-muted)">{{ description }}</p>
      <p v-if="session.user.value" class="mt-4 text-xs" style="color: var(--color-text-muted)">
        Signed in as <span class="font-semibold" style="color: var(--color-text)">{{ session.user.value.fullName }}</span>
      </p>
      <div class="mt-6 flex flex-wrap justify-center gap-3">
        <RouterLink to="/" class="btn-primary">Back to Home</RouterLink>
        <button v-if="session.isAuthenticated.value" type="button" class="btn-outline" @click="logOut">
          <LogOut :size="16" /> Log out
        </button>
      </div>
    </div>
  </div>
</template>
