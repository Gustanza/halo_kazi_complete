<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Moon, Sun, Menu, X, LogOut } from 'lucide-vue-next'
import { useTheme } from '../../../../composables/useTheme'
import { useScrollSpy } from '../../../../composables/useScrollSpy'
import BrandLogo from '../../../../components/common/BrandLogo.vue'
import { useSession } from '../../../../auth/session'

const { theme, toggleTheme } = useTheme()
const session = useSession()
const accountHome = computed(() => session.homeFor(session.user.value))
const router = useRouter()

async function logOut() {
  mobileOpen.value = false
  await session.logout()
  router.replace('/')
}
const mobileOpen = ref(false)
const route = useRoute()

const scrolled = ref(false)
function onScroll() {
  scrolled.value = window.scrollY > 8
}
onMounted(() => {
  onScroll()
  window.addEventListener('scroll', onScroll, { passive: true })
})
onUnmounted(() => window.removeEventListener('scroll', onScroll))

const navLinks = [
  { label: 'Home', id: 'home' },
  { label: 'Jobs', id: 'jobs' },
  { label: 'Employers', id: 'employers' },
  { label: 'CV builder', id: 'cv-builder' },
  { label: 'About us', id: 'about' },
]

const sectionIds = navLinks.map((link) => link.id)
const { activeId } = useScrollSpy(sectionIds)

const isHome = computed(() => route.path === '/')

function isActive(id) {
  return isHome.value && activeId.value === id
}
</script>

<template>
  <header
    class="sticky top-0 z-40 border-b transition-shadow duration-300"
    :class="scrolled ? 'shadow-md' : ''"
    style="background-color: var(--color-surface); border-color: var(--color-border)"
  >
    <div class="container-bleed flex h-20 items-center justify-between">
      <RouterLink :to="{ path: '/', hash: '#home' }" class="flex items-center gap-2.5">
        <BrandLogo :size="40" />
        <span class="flex flex-col leading-tight">
          <span class="text-2xl font-extrabold text-primary">Halokazi</span>
          <span class="text-[11px] font-medium" style="color: var(--color-text-muted)">Kazi bora, maisha bora</span>
        </span>
      </RouterLink>

      <nav class="hidden items-center gap-1 lg:flex">
        <RouterLink
          v-for="(link, i) in navLinks"
          :key="link.id"
          :to="{ path: '/', hash: '#' + link.id }"
          class="rounded-full px-3.5 py-2 text-sm font-medium transition-colors hover:text-primary"
          :class="isActive(link.id) ? 'bg-primary-50 text-primary dark:bg-white/10' : ''"
          :style="isActive(link.id) ? '' : 'color: var(--color-text)'"
        >
          {{ link.label }}
        </RouterLink>
        <span
          class="cursor-default rounded-full px-3.5 py-2 text-sm font-medium opacity-50"
          style="color: var(--color-text)"
          title="Coming soon"
        >
          Halokazi
        </span>
      </nav>

      <div class="flex items-center gap-3">
        <button
          type="button"
          class="press hidden h-10 w-10 items-center justify-center overflow-hidden rounded-full border transition-colors hover:border-primary hover:text-primary sm:flex"
          style="border-color: var(--color-border); color: var(--color-text)"
          :aria-label="theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'"
          @click="toggleTheme"
        >
          <Transition name="icon-swap" mode="out-in">
            <Sun v-if="theme === 'dark'" key="sun" :size="18" />
            <Moon v-else key="moon" :size="18" />
          </Transition>
        </button>
        <template v-if="session.isAuthenticated.value">
          <RouterLink :to="accountHome" class="press hidden rounded-full bg-primary px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-primary-600 sm:inline-flex">
            My dashboard
          </RouterLink>
          <button
            type="button"
            class="press hidden h-10 w-10 items-center justify-center rounded-full border transition-colors hover:border-primary hover:text-primary sm:inline-flex"
            style="border-color: var(--color-border); color: var(--color-text)"
            aria-label="Log out"
            title="Log out"
            @click="logOut"
          >
            <LogOut :size="17" />
          </button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="press hidden rounded-full border px-5 py-2.5 text-sm font-semibold transition-colors hover:border-primary hover:text-primary sm:inline-flex" style="border-color: var(--color-border); color: var(--color-text)">
            Login
          </RouterLink>
          <RouterLink to="/register" class="press hidden rounded-full bg-primary px-5 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-primary-600 sm:inline-flex">
            Register
          </RouterLink>
        </template>
        <button
          type="button"
          class="press flex h-10 w-10 items-center justify-center rounded-full border lg:hidden"
          style="border-color: var(--color-border); color: var(--color-text)"
          aria-label="Toggle menu"
          @click="mobileOpen = !mobileOpen"
        >
          <X v-if="mobileOpen" :size="20" />
          <Menu v-else :size="20" />
        </button>
      </div>
    </div>

    <Transition name="menu">
      <div v-if="mobileOpen" class="border-t px-4 pb-4 lg:hidden" style="border-color: var(--color-border)">
        <nav class="flex flex-col gap-1 pt-2">
          <RouterLink
            v-for="link in navLinks"
            :key="link.id"
            :to="{ path: '/', hash: '#' + link.id }"
            class="rounded-lg px-3 py-2.5 text-sm font-medium transition-colors hover:bg-primary-50 hover:text-primary"
            :class="isActive(link.id) ? 'bg-primary-50 text-primary dark:bg-white/10' : ''"
            :style="isActive(link.id) ? '' : 'color: var(--color-text)'"
            @click="mobileOpen = false"
          >
            {{ link.label }}
          </RouterLink>
          <span class="rounded-lg px-3 py-2.5 text-sm font-medium opacity-50" style="color: var(--color-text)">Halokazi</span>
          <div class="mt-2 flex items-center gap-3 border-t pt-3" style="border-color: var(--color-border)">
            <button
              type="button"
              class="press flex h-10 w-10 shrink-0 items-center justify-center overflow-hidden rounded-full border"
              style="border-color: var(--color-border); color: var(--color-text)"
              @click="toggleTheme"
            >
              <Transition name="icon-swap" mode="out-in">
                <Sun v-if="theme === 'dark'" key="sun" :size="18" />
                <Moon v-else key="moon" :size="18" />
              </Transition>
            </button>
            <template v-if="session.isAuthenticated.value">
              <RouterLink :to="accountHome" class="press flex-1 rounded-full bg-primary px-5 py-2.5 text-center text-sm font-semibold text-white" @click="mobileOpen = false">
                My dashboard
              </RouterLink>
              <button type="button" class="press flex-1 rounded-full border px-5 py-2.5 text-center text-sm font-semibold" style="border-color: var(--color-border); color: var(--color-text)" @click="logOut">
                Log out
              </button>
            </template>
            <template v-else>
              <RouterLink to="/login" class="press flex-1 rounded-full border px-5 py-2.5 text-center text-sm font-semibold" style="border-color: var(--color-border); color: var(--color-text)" @click="mobileOpen = false">
                Login
              </RouterLink>
              <RouterLink to="/register" class="press flex-1 rounded-full bg-primary px-5 py-2.5 text-center text-sm font-semibold text-white" @click="mobileOpen = false">
                Register
              </RouterLink>
            </template>
          </div>
        </nav>
      </div>
    </Transition>
  </header>
</template>

<style scoped>
.menu-enter-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.menu-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.menu-enter-from,
.menu-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

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
