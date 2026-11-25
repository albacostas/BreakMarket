import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import { authService, setAccessTokenProvider, userService } from '../services/api'

const AuthContext = createContext(null)
const audience = import.meta.env.VITE_AUTH0_API_AUDIENCE
const scope = 'openid profile email'

export function AuthProvider({ children }) {
  const {
    isAuthenticated,
    isLoading: authLoading,
    getAccessTokenSilently,
    loginWithRedirect,
    logout: auth0Logout,
  } = useAuth0()
  const [user, setUser] = useState(null)
  const [profileLoading, setProfileLoading] = useState(false)
  const [error, setError] = useState('')

  const accessToken = useCallback(
    () => isAuthenticated
      ? getAccessTokenSilently({ authorizationParams: { audience, scope } })
      : Promise.resolve(''),
    [getAccessTokenSilently, isAuthenticated],
  )

  useEffect(() => {
    setAccessTokenProvider(accessToken)
    return () => setAccessTokenProvider(null)
  }, [accessToken])

  useEffect(() => {
    let active = true
    setError('')

    if (authLoading) {
      setProfileLoading(true)
      return () => { active = false }
    }

    if (!isAuthenticated) {
      setUser(null)
      setProfileLoading(false)
      return () => { active = false }
    }

    setProfileLoading(true)
    authService.me()
      .then((currentUser) => {
        if (active) setUser(currentUser)
      })
      .catch(() => {
        if (active) setError('No pudimos cargar tu perfil. Inténtalo de nuevo más tarde.')
      })
      .finally(() => {
        if (active) setProfileLoading(false)
      })

    return () => { active = false }
  }, [authLoading, isAuthenticated])

  const login = () => loginWithRedirect()
  const register = () => loginWithRedirect({ authorizationParams: { screen_hint: 'signup' } })
  const logout = () => {
    setUser(null)
    return auth0Logout({ logoutParams: { returnTo: window.location.origin } })
  }
  const updateBalance = (nextBalance) => setUser((current) => current ? ({ ...current, balance: nextBalance }) : current)
  const refreshUser = async () => {
    const currentUser = await userService.me()
    setUser(currentUser)
    return currentUser
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, loading: authLoading || profileLoading, error, login, register, logout, updateBalance, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
