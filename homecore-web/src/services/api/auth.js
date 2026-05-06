import { request } from './client'

export const register = (data) => request('POST', '/users/register', data)
export const login = (email, password) => request('POST', '/users/login', { email, password })
export const getUserProfile = () => request('GET', '/users/profile')
export const updateProfile = (data) => request('POST', '/users/profile', data)

/*Password management*/
export const changePassword = (currentPassword, newPassword) =>
  request('POST', '/users/change-password', { currentPassword, newPassword })
export const forgotPassword = (email) => request('POST', '/users/forgot-password', { email })
export const resetPassword = (code, newPassword) =>
  request('POST', '/users/reset-password', { code, newPassword })
export const logout = () => request('POST', '/users/logout')

/*Mailer Config*/
export const verifyAccount = (code) => request('POST', '/users/verify-account', { code })
export const sendVerification = (email) => request('POST', '/users/send-verification', { email })
export const getMailerConfig = () => request('GET', '/mailerconfig')
export const createMailerConfig = (config) => request('POST', '/mailerconfig', config)
export const updateMailerConfig = (config) => request('PUT', '/mailerconfig', config)
export const deleteMailerConfig = () => request('DELETE', '/mailerconfig')

/*Mailer-Template*/
export const postMailerTemplate = () => request('POST', '/emailtemplates')
export const getAllMailerTemplates = () => request('GET', '/emailtemplates')
export const getMailerTemplate = (templateId) => request('POST', `/emailtemplates/${templateId}`)
export const updateEmailTemplate = (templateId, data) => request('POST', `/emailtemplates/${templateId}`, data)
export const deleteEmailTemplate = (templateId) => request('DELETE', `/emailtemplates/${templateId}`)



