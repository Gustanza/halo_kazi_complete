import { computed, ref, watchEffect } from 'vue'

const STORAGE_KEY = 'halokazi-theme'
const PREFERENCES = ['light', 'dark', 'system']

// 'system' follows the operating system's light/dark setting.
function getInitialPreference() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (PREFERENCES.includes(stored)) return stored
  } catch {
    // localStorage unavailable (private browsing, etc.) - fall through
  }
  return 'system'
}

const systemQuery = window.matchMedia?.('(prefers-color-scheme: dark)')
const systemDark = ref(Boolean(systemQuery?.matches))
systemQuery?.addEventListener?.('change', (event) => (systemDark.value = event.matches))

const preference = ref(getInitialPreference())
const theme = computed(() => (preference.value === 'system' ? (systemDark.value ? 'dark' : 'light') : preference.value))

watchEffect(() => {
  document.documentElement.classList.toggle('dark', theme.value === 'dark')
  try {
    localStorage.setItem(STORAGE_KEY, preference.value)
  } catch {
    // ignore persistence failures
  }
})

export function useTheme() {
  function toggleTheme() {
    preference.value = theme.value === 'dark' ? 'light' : 'dark'
  }
  /** 'light' | 'dark' | 'system' */
  function setPreference(value) {
    if (PREFERENCES.includes(value)) preference.value = value
  }
  return { theme, preference, toggleTheme, setPreference }
}
