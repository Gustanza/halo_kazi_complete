<script setup>
import { onMounted, ref } from 'vue'
import { BellRing, CheckCheck } from 'lucide-vue-next'
import DashboardLayout from '../components/layout/DashboardLayout.vue'
import PageHeader from '../../../components/ui/PageHeader.vue'
import AppButton from '../../../components/ui/AppButton.vue'
import EmptyState from '../../../components/ui/EmptyState.vue'
import Skeleton from '../../../components/ui/Skeleton.vue'
import * as notificationsApi from '../../../api/notifications'
import { useToast } from '../../../composables/useToast'

const toast = useToast()
const loading = ref(true)
const notifications = ref([])
const markingAll = ref(false)

async function load() {
  loading.value = true
  try {
    const result = await notificationsApi.listNotifications({ pageSize: 50 })
    notifications.value = result.items
  } catch {
    toast.error('Could not load notifications.')
  } finally {
    loading.value = false
  }
}

async function markRead(notification) {
  if (notification.readAt) return
  notification.readAt = new Date().toISOString()
  await notificationsApi.markNotificationRead(notification.id)
}

async function markAllRead() {
  markingAll.value = true
  try {
    await notificationsApi.markAllNotificationsRead()
    notifications.value = notifications.value.map((n) => ({ ...n, readAt: n.readAt ?? new Date().toISOString() }))
  } finally {
    markingAll.value = false
  }
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}

onMounted(load)
</script>

<template>
  <DashboardLayout>
    <PageHeader title="Notifications" description="Updates about your applications, saved jobs and job alerts.">
      <template #actions>
        <AppButton v-if="notifications.some((n) => !n.readAt)" variant="outline" size="sm" :loading="markingAll" @click="markAllRead">
          <CheckCheck :size="15" /> Mark all as read
        </AppButton>
      </template>
    </PageHeader>

    <div class="mt-6 space-y-3">
      <template v-if="loading">
        <Skeleton v-for="i in 4" :key="i" variant="card" height="4.5rem" />
      </template>

      <EmptyState
        v-else-if="!notifications.length"
        :icon="BellRing"
        title="No notifications yet"
        description="You're all caught up. We'll let you know when something needs your attention."
      />

      <button
        v-for="notification in notifications"
        v-else
        :key="notification.id"
        type="button"
        class="card-surface flex w-full items-start gap-3 p-4 text-left transition hover:border-primary"
        @click="markRead(notification)"
      >
        <span
          class="mt-1 h-2 w-2 shrink-0 rounded-full"
          :style="notification.readAt ? 'background-color: var(--color-border)' : 'background-color: #FD5C20'"
        />
        <div class="min-w-0 flex-1">
          <p class="text-sm font-semibold" style="color: var(--color-text)">{{ notification.title }}</p>
          <p class="mt-0.5 text-sm" style="color: var(--color-text-muted)">{{ notification.body }}</p>
          <p class="mt-1.5 text-xs" style="color: var(--color-text-muted)">{{ formatDate(notification.createdAt) }}</p>
        </div>
      </button>
    </div>
  </DashboardLayout>
</template>
