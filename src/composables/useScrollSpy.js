import { ref, onMounted, onUnmounted } from 'vue'

export function useScrollSpy(ids, offset = 110) {
  const activeId = ref(ids[0])
  let ticking = false

  function update() {
    ticking = false
    const scrollPos = window.scrollY + offset
    let current = ids[0]
    for (const id of ids) {
      const el = document.getElementById(id)
      if (el && el.offsetTop <= scrollPos) {
        current = id
      }
    }
    activeId.value = current
  }

  function onScroll() {
    if (!ticking) {
      window.requestAnimationFrame(update)
      ticking = true
    }
  }

  onMounted(() => {
    update()
    window.addEventListener('scroll', onScroll, { passive: true })
    window.addEventListener('resize', onScroll, { passive: true })
  })
  onUnmounted(() => {
    window.removeEventListener('scroll', onScroll)
    window.removeEventListener('resize', onScroll)
  })

  return { activeId, refresh: update }
}
