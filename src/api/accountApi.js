// The signed-in user's own account - back_resources/docs/Halokazi_API.pdf.
import { getJson, request } from './http'

const put = (path, body) => request('PUT', path, { body, auth: true })

/** { account, notifications, privacy, preferences } */
export const getSettings = () => getJson('/api/me/settings', { auth: true })

/** { email, applicationUpdates, jobAlerts, newsletter } -> updated settings */
export const updateNotifications = (choice) => put('/api/me/settings/notifications', choice)

/** { profileVisible, showEmail, showPhone } -> updated settings */
export const updatePrivacy = (choice) => put('/api/me/settings/privacy', choice)

/** 'EN' | 'SW' -> updated settings */
export const updateLanguage = (language) => put('/api/me/settings/language', { language })

/** Signs out the user's other devices. */
export const changePassword = (currentPassword, newPassword, confirmPassword) =>
  request('POST', '/api/me/password', { body: { currentPassword, newPassword, confirmPassword }, auth: true })
