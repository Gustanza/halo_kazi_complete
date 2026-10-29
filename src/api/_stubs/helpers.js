// Shared helpers for the stub adapters. Stubs mimic network latency so loading states
// (skeletons, spinners) get exercised the same way they will against the real backend.
export function delay(ms = 400) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

let nextId = 1000
export function generateId() {
  return nextId++
}

/** Deep-clones a stub record so callers can't accidentally mutate the in-memory "table". */
export function clone(value) {
  return JSON.parse(JSON.stringify(value))
}
