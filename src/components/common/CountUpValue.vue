<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  value: { type: String, required: true }, // e.g. "10K+", "98%", "24/7"
  duration: { type: Number, default: 1100 },
})

const el = ref(null)
const display = ref(props.value)
const prefersReducedMotion =
  typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches
let observer
let frame

function animate() {
  const match = props.value.match(/^(\d+(?:\.\d+)?)(.*)$/)
  if (!match) return
  const target = parseFloat(match[1])
  const suffix = match[2]
  const start = performance.now()

  function tick(now) {
    const progress = Math.min((now - start) / props.duration, 1)
    const eased = 1 - Math.pow(1 - progress, 3)
    const current = Math.round(target * eased)
    display.value = `${current}${suffix}`
    if (progress < 1) {
      frame = requestAnimationFrame(tick)
    } else {
      display.value = props.value
    }
  }
  frame = requestAnimationFrame(tick)
}

onMounted(() => {
  if (prefersReducedMotion || !el.value) return
  observer = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting) {
        animate()
        observer.disconnect()
      }
    },
    { threshold: 0.4 },
  )
  observer.observe(el.value)
})

onUnmounted(() => {
  observer?.disconnect()
  if (frame) cancelAnimationFrame(frame)
})
</script>

<template>
  <span ref="el">{{ display }}</span>
</template>
