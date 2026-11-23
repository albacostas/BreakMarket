import { createContext, useContext, useEffect, useState } from 'react'
import { authService, userService } from '../services/api'

const AuthContext = createContext(null)
const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i

const validStoredUser = () => {
  try {
    const saved = JSON.parse(localStorage.getItem('bm_user') || 'null')
    return saved && uuidPattern.test(saved.id || saved.uuid || '') ? saved : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(validStoredUser)
  const [token, setToken] = useState(() => localStorage.getItem('bm_token') || '')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (token) localStorage.setItem('bm_token', token)
  }, [token])

  useEffect(() => {
    localStorage.setItem('bm_user', JSON.stringify(user))
  }, [user])

  const login = async (credentials) => {
    setLoading(true)
    try {
      const data = await authService.login(credentials)
      setToken(data.access_token)
      setUser(data.user)
      return data
    } finally {
      setLoading(false)
    }
  }

  const register = async (details) => {
    setLoading(true)
    try {
      const data = await authService.register(details)
      setToken(data.access_token)
      setUser(data.user)
      return data
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('bm_token')
    localStorage.removeItem('bm_user')
    setToken('')
    setUser(null)
  }

  const updateBalance = (nextBalance) => setUser((current) => current ? ({ ...current, balance: nextBalance }) : current)

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, updateBalance, refreshUser: () => userService.me().then(setUser) }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
