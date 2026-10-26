// v-reveal: fades/slides an element in once it scrolls into view.
// Optional numeric binding sets a stagger delay in ms, e.g. v-reveal="i * 70".
const prefersReducedMotion =
  typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches

export const reveal = {
  mounted(el, binding) {
    el.classList.add('reveal')

    if (prefersReducedMotion) {
      el.classList.add('is-visible')
      return
    }

    const delay = typeof binding.value === 'number' ? binding.value : 0
    if (delay) el.style.transitionDelay = `${delay}ms`

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          el.classList.add('is-visible')
          observer.unobserve(el)
        }
      },
      { threshold: 0.15, rootMargin: '0px 0px -60px 0px' },
    )
    observer.observe(el)
    el._revealObserver = observer
  },
  unmounted(el) {
    el._revealObserver?.disconnect()
  },
}
