/** 45 -> "45s", 900 -> "15 min", 1830 -> "31 min" (for lockout and resend waits). */
export function formatWait(seconds) {
  if (!seconds || seconds < 60) return `${Math.max(1, seconds || 0)}s`
  return `${Math.ceil(seconds / 60)} min`
}

/** "+255712345678" or "712345678" -> "+255 •••••5678". */
export function maskPhone(phone) {
  const local = String(phone).replace(/^\+?255/, '').replace(/\D/g, '')
  if (local.length < 4) return phone
  return `+255 ${'•'.repeat(local.length - 4)}${local.slice(-4)}`
}
