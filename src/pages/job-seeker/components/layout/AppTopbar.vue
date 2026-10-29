<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Moon, Sun, Bell, ChevronDown, Menu, UserRound, Settings, LogOut } from 'lucide-vue-next'
import { useTheme } from '../../../../composables/useTheme'
import { useSession } from '../../../../auth/session'
import * as notificationsApi from '../../../../api/notifications'

defineEmits(['toggle-sidebar'])

const router = useRouter()
const session = useSession()
const fullName = computed(() => session.user.value?.fullName ?? '')
const { theme, toggleTheme } = useTheme()
const userMenuOpen = ref(false)
const notifOpen = ref(false)
const topbarRef = ref(null)
const unreadCount = ref(0)
const recentNotifications = ref([])

function closeMenus() {
  userMenuOpen.value = false
  notifOpen.value = false
}

function onClickOutside(event) {
  if (topbarRef.value && !topbarRef.value.contains(event.target)) closeMenus()
}

async function loadNotifications() {
  const result = await notificationsApi.listNotifications({ pageSize: 3 })
  recentNotifications.value = result.items
  unreadCount.value = result.unreadCount
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
  loadNotifications()
})
onUnmounted(() => document.removeEventListener('click', onClickOutside))

const initials = computed(() =>
  fullName.value
    .split(/\s+/)
    .filter(Boolean)
    .map((part) => part[0].toUpperCase())
    .join('')
    .slice(0, 2) || '?',
)

async function logOut() {
  closeMenus()
  await session.logout()
  router.replace('/login')
}
</script>

<template>
  <header class="sticky top-0 z-20 border-b" style="background-color: var(--color-surface); border-color: var(--color-border)">
    <div class="flex h-20 items-center justify-between px-4 sm:px-6 lg:px-8">
      <div class="flex items-center gap-3">
        <button
          type="button"
          class="press flex h-10 w-10 items-center justify-center rounded-full border lg:hidden"
          style="border-color: var(--color-border); color: var(--color-text)"
          aria-label="Toggle sidebar"
          @click="$emit('toggle-sidebar')"
        >
          <Menu :size="18" />
        </button>
        <RouterLink to="/dashboard" class="flex flex-col leading-tight">
          <span class="text-xl font-extrabold text-primary sm:text-2xl">Halokazi</span>
          <span class="text-[11px] font-medium" style="color: var(--color-text-muted)">Kazi bora, maisha bora</span>
        </RouterLink>
      </div>

      <div ref="topbarRef" class="flex items-center gap-2 sm:gap-3">
        <button
          type="button"
          class="press flex h-10 w-10 items-center justify-center overflow-hidden rounded-full border transition-colors hover:border-primary hover:text-primary"
          style="border-color: var(--color-border); color: var(--color-text)"
          :aria-label="theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'"
          @click="toggleTheme"
        >
          <Transition name="icon-swap" mode="out-in">
            <Sun v-if="theme === 'dark'" key="sun" :size="18" />
            <Moon v-else key="moon" :size="18" />
          </Transition>
        </button>

        <div class="relative">
          <button
            type="button"
            class="press relative flex h-10 w-10 items-center justify-center rounded-full border transition-colors hover:border-primary hover:text-primary"
            style="border-color: var(--color-border); color: var(--color-text)"
            aria-label="Notifications"
            @click="notifOpen = !notifOpen; userMenuOpen = false"
          >
            <Bell :size="18" />
            <span v-if="unreadCount > 0" class="absolute right-2 top-2 h-2 w-2 rounded-full bg-primary" />
          </button>
          <Transition name="popover">
            <div
              v-if="notifOpen"
              class="card-surface absolute right-0 mt-2 w-72 p-2 text-sm"
            >
              <p class="px-2 py-1.5 font-semibold" style="color: var(--color-text)">Notifications</p>
              <p v-if="!recentNotifications.length" class="px-2 py-2" style="color: var(--color-text-muted)">
                You're all caught up — no new notifications right now.
              </p>
              <div v-for="notification in recentNotifications" :key="notification.id" class="rounded-lg px-2 py-2 hover:bg-primary-50 dark:hover:bg-white/5">
                <p class="font-medium" style="color: var(--color-text)">{{ notification.title }}</p>
                <p class="mt-0.5 line-clamp-2" style="color: var(--color-text-muted)">{{ notification.body }}</p>
              </div>
              <RouterLink
                to="/dashboard/notifications"
                class="mt-1 block rounded-lg px-2 py-2 text-center font-semibold text-primary hover:bg-primary-50 dark:hover:bg-white/5"
                @click="notifOpen = false"
              >
                View all
              </RouterLink>
            </div>
          </Transition>
        </div>

        <div class="relative">
          <button
            type="button"
            class="press flex items-center gap-2 rounded-full border py-1 pl-1 pr-2 transition-colors hover:border-primary sm:pr-3"
            style="border-color: var(--color-border)"
            @click="userMenuOpen = !userMenuOpen; notifOpen = false"
          >
            <span class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-xs font-bold text-white">
              {{ initials }}
            </span>
            <span class="hidden text-sm font-medium sm:inline" style="color: var(--color-text)">{{ fullName }}</span>
            <ChevronDown :size="15" style="color: var(--color-text-muted)" />
          </button>
          <Transition name="popover">
            <div v-if="userMenuOpen" class="card-surface absolute right-0 mt-2 w-48 overflow-hidden p-1.5 text-sm">
              <RouterLink
                to="/dashboard/profile"
                class="flex items-center gap-2.5 rounded-lg px-3 py-2 transition-colors hover:bg-primary-50 dark:hover:bg-white/5"
                style="color: var(--color-text)"
                @click="userMenuOpen = false"
              >
                <UserRound :size="16" /> My Profile
              </RouterLink>
              <RouterLink
                to="/dashboard/settings"
                class="flex items-center gap-2.5 rounded-lg px-3 py-2 transition-colors hover:bg-primary-50 dark:hover:bg-white/5"
                style="color: var(--color-text)"
                @click="userMenuOpen = false"
              >
                <Settings :size="16" /> Settings
              </RouterLink>
              <div class="my-1 h-px" style="background-color: var(--color-border)" />
              <button
                type="button"
                class="flex w-full items-center gap-2.5 rounded-lg px-3 py-2 text-left text-red-500 transition-colors hover:bg-red-50 dark:hover:bg-red-500/10"
                @click="logOut"
              >
                <LogOut :size="16" /> Log out
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.icon-swap-enter-active,
.icon-swap-leave-active {
  transition: opacity 150ms var(--ease-out), transform 150ms var(--ease-out);
}
.icon-swap-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.6);
}
.icon-swap-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.6);
}
</style>
