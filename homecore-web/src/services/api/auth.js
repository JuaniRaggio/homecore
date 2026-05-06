import { request } from './client'

export const register = (data) => request('POST', '/users/register', data)
export const login = (email, password) => request('POST', '/users/login', { email, password })
export const getUserProfile = () => request('GET', '/users/profile')
export const updateProfile = (data) => request('PUT', '/users/profile', data)

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
export const createMailerConfig = (config) => request('POST', '/mailerconfig', data)
export const updateMailerConfig = (config) => request('PUT', '/mailerconfig', data)
export const deleteMailerConfig = () => request('DELETE', '/mailerconfig')




