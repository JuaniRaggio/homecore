const BASE_URL = import.meta.env.VITE_API_BASE_URL
const API_KEY = import.meta.env.VITE_API_KEY

function headers() {
  const h = {
    'Content-Type': 'application/json',
    'X-API-Key': API_KEY
  }
  const token = localStorage.getItem('auth_token')
  if (token) h['Authorization'] = `Bearer ${token}`
  return h
}

export async function request(method, path, body = null) {
  const options = { method, headers: headers() }
  if (body !== null) options.body = JSON.stringify(body)

  const res = await fetch(`${BASE_URL}${path}`, options)
  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}))
    const err = new Error(errorData.error?.description || errorData.message || 'API request failed')
    err.status = res.status
    throw err
  }
  const text = await res.text()
  if (!text) return null
  const json = JSON.parse(text)
  return json.result !== undefined ? json.result : json
}
