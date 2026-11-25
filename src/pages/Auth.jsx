import { useState } from 'react'
import { Spade, ArrowRight, LogOut, UserPlus } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useLocation } from 'react-router-dom'

export default function Auth() {
  const { user, isAuthenticated, loading, error: sessionError, login, register, logout } = useAuth()
  const [actionError, setActionError] = useState('')
  const location = useLocation()
  const oauthError = new URLSearchParams(location.search).has('error')

  const signOut = async () => {
    setActionError('')
    try {
      await logout()
    } catch {
      setActionError('No pudimos cerrar la sesión. Inténtalo de nuevo.')
    }
  }

  const signIn = async (createAccount = false) => {
    setActionError('')
    try {
      await (createAccount ? register() : login())
    } catch {
      setActionError('No pudimos iniciar el acceso con tu proveedor. Inténtalo de nuevo.')
    }
  }

  return (
    <div className="auth-page">
      <section className="auth-card casino-panel">
        <div className="auth-logo"><Spade size={28} fill="currentColor" /></div>
        <h1 className="gold-text">{isAuthenticated ? (user ? 'Sesión activa' : 'Cuenta conectada') : 'Acceso VIP'}</h1>
        <p>{user ? `Bienvenido, ${user.username}.` : isAuthenticated ? 'Tu cuenta de OAuth está conectada.' : 'Entra a la sala, apuesta fichas y sigue el salseo en directo.'}</p>
        {oauthError && <div className="form-error" role="alert">No pudimos completar el acceso con tu proveedor. Inténtalo de nuevo.</div>}
        {isAuthenticated && sessionError && <div className="form-error" role="alert">{sessionError}</div>}
        {actionError && <div className="form-error" role="alert">{actionError}</div>}
        {isAuthenticated ? (
          <button className="auth-submit" onClick={signOut}>Cerrar sesión <LogOut size={18} /></button>
        ) : (
          <>
            <button className="auth-submit" disabled={loading} onClick={() => signIn()}>
              {loading ? 'Comprobando sesión...' : 'Iniciar sesión'} <ArrowRight size={18} />
            </button>
            <button className="auth-submit" disabled={loading} onClick={() => signIn(true)}>
              Crear cuenta <UserPlus size={18} />
            </button>
          </>
        )}
        <small>El acceso se gestiona de forma segura con nuestro proveedor de identidad.</small>
      </section>
    </div>
  )
}
