<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Moon, Sun, Bell, ChevronDown, Menu, UserRound, Settings, LogOut } from 'lucide-vue-next'
import { useTheme } from '../../../../composables/useTheme'
import { currentUser } from '../../data/dashboard'

defineEmits(['toggle-sidebar'])

const { theme, toggleTheme } = useTheme()
const userMenuOpen = ref(false)
const notifOpen = ref(false)
const topbarRef = ref(null)

function closeMenus() {
  userMenuOpen.value = false
  notifOpen.value = false
}

function onClickOutside(event) {
  if (topbarRef.value && !topbarRef.value.contains(event.target)) closeMenus()
}

onMounted(() => document.addEventListener('click', onClickOutside))
onUnmounted(() => document.removeEventListener('click', onClickOutside))

const initials = currentUser.fullName
  .split(' ')
  .map((part) => part[0])
  .join('')
  .slice(0, 2)
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
            <span class="absolute right-2 top-2 h-2 w-2 rounded-full bg-primary" />
          </button>
          <Transition name="popover">
            <div
              v-if="notifOpen"
              class="card-surface absolute right-0 mt-2 w-72 p-4 text-sm"
              style="color: var(--color-text-muted)"
            >
              <p class="font-semibold" style="color: var(--color-text)">Notifications</p>
              <p class="mt-2">You're all caught up — no new notifications right now.</p>
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
            <span class="hidden text-sm font-medium sm:inline" style="color: var(--color-text)">{{ currentUser.fullName }}</span>
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
                @click="userMenuOpen = false"
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
