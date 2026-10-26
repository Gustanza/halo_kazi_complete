<script setup>
import { ref } from 'vue'
import AppTopbar from './AppTopbar.vue'
import SidebarNav from './SidebarNav.vue'

const sidebarOpen = ref(false)
</script>

<template>
  <div class="min-h-screen" style="background-color: var(--color-bg)">
    <AppTopbar @toggle-sidebar="sidebarOpen = !sidebarOpen" />
    <div class="flex">
      <SidebarNav :open="sidebarOpen" @close="sidebarOpen = false" />
      <main class="min-w-0 flex-1 px-4 py-8 sm:px-6 lg:px-8">
        <slot />
      </main>
    </div>
  </div>
</template>
