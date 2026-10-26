<script setup>
import {
  LayoutDashboard,
  UserRound,
  FileEdit,
  FolderOpen,
  Search,
  Bookmark,
  Send,
  BellRing,
  Settings,
  Sparkles,
} from 'lucide-vue-next'

defineProps({
  open: { type: Boolean, default: false },
})
defineEmits(['close'])

const primaryLinks = [
  { label: 'Dashboard', to: '/dashboard', icon: LayoutDashboard },
  { label: 'My Profile', to: '/dashboard/profile', icon: UserRound },
  { label: 'CV Builder', to: '/dashboard/cv-builder', icon: FileEdit },
  { label: 'My CVs', to: '/dashboard/my-cvs', icon: FolderOpen },
]

const secondaryLinks = [
  { label: 'Find Jobs', to: '/dashboard/find-jobs', icon: Search },
  { label: 'Saved Jobs', to: '/dashboard/saved-jobs', icon: Bookmark },
  { label: 'Applications', to: '/dashboard/applications', icon: Send },
  { label: 'Job Alerts', to: '/dashboard/job-alerts', icon: BellRing },
]
</script>

<template>
  <!-- mobile backdrop -->
  <Transition name="fade">
    <div
      v-if="open"
      class="fixed inset-0 z-30 bg-black/40 lg:hidden"
      @click="$emit('close')"
    />
  </Transition>

  <aside
    class="fixed inset-y-0 left-0 z-40 flex w-64 shrink-0 flex-col border-r px-4 py-6 lg:static lg:translate-x-0"
    :class="open ? 'translate-x-0' : '-translate-x-full'"
    style="background-color: var(--color-surface); border-color: var(--color-border); transition: transform 280ms var(--ease-drawer)"
  >
    <nav class="flex flex-1 flex-col gap-1 overflow-y-auto">
      <RouterLink
        v-for="link in primaryLinks"
        :key="link.to"
        :to="link.to"
        class="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors hover:bg-primary-50 dark:hover:bg-white/5"
        active-class="!bg-primary-50 !text-primary dark:!bg-white/10"
        style="color: var(--color-text)"
        @click="$emit('close')"
      >
        <component :is="link.icon" :size="18" />
        {{ link.label }}
      </RouterLink>

      <div class="my-3 h-px" style="background-color: var(--color-border)" />

      <RouterLink
        v-for="link in secondaryLinks"
        :key="link.to"
        :to="link.to"
        class="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors hover:bg-primary-50 dark:hover:bg-white/5"
        active-class="!bg-primary-50 !text-primary dark:!bg-white/10"
        style="color: var(--color-text)"
        @click="$emit('close')"
      >
        <component :is="link.icon" :size="18" />
        {{ link.label }}
      </RouterLink>
    </nav>

    <div class="mt-4 space-y-4">
      <RouterLink
        to="/dashboard/settings"
        class="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors hover:bg-primary-50 dark:hover:bg-white/5"
        active-class="!bg-primary-50 !text-primary dark:!bg-white/10"
        style="color: var(--color-text)"
        @click="$emit('close')"
      >
        <Settings :size="18" />
        Settings
      </RouterLink>

      <div class="rounded-2xl p-4" style="background-color: var(--color-peach)">
        <div class="flex h-9 w-9 items-center justify-center rounded-full bg-primary text-white">
          <Sparkles :size="16" />
        </div>
        <p class="mt-3 text-sm font-semibold" style="color: var(--color-text)">Stand Out to Employers</p>
        <p class="mt-1 text-xs" style="color: var(--color-text-muted)">Complete your profiles and increase your chances</p>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 200ms ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
