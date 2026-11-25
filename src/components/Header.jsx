import { Search, Bell, LogIn, LogOut, Spade, UserRound } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'

export default function Header({ search, onSearch }) {
  const { user, isAuthenticated, logout } = useAuth()
  const [localSearch, setLocalSearch] = useState(search || '')
  const [logoutError, setLogoutError] = useState('')
  useEffect(() => setLocalSearch(search || ''), [search])

  const updateSearch = (value) => {
    setLocalSearch(value)
    onSearch?.(value)
    window.dispatchEvent(new CustomEvent('bm-search', { detail: value }))
  }

  const signOut = async () => {
    setLogoutError('')
    try {
      await logout()
    } catch {
      setLogoutError('No se pudo cerrar la sesión.')
    }
  }

  return (
    <header className="app-header">
      <Link to="/" className="brand" aria-label="Ir al inicio de BreakMarket">
        <span className="brand-mark"><Spade size={20} fill="currentColor" /></span>
        <span className="brand-copy">
          <strong className="gold-text">BreakMarket</strong>
          <small>Las Vegas · Sala del Salseo</small>
        </span>
      </Link>
      <label className="search-box" aria-label="Buscar mercados">
        <Search size={17} />
        <input value={localSearch} onChange={(e) => updateSearch(e.target.value)} placeholder="Buscar salseo, parejas o rumores..." />
      </label>
      <div className="header-actions">
        <button className="icon-button" aria-label="Notificaciones"><Bell size={19} /><i /></button>
        <Link to="/portfolio" className="balance-badge" aria-label="Ver cartera y saldo">
          <span>Créditos VIP</span>
          <strong>{(user?.balance ?? 0).toLocaleString('es-ES')} <b>💔</b></strong>
        </Link>
        <Link
          to="/auth"
          className={`account-link ${isAuthenticated ? 'authenticated' : ''}`}
          aria-label={isAuthenticated ? 'Ver cuenta y detalles de sesión' : 'Abrir portal de inicio de sesión'}
          title={isAuthenticated ? 'Cuenta' : 'Iniciar sesión'}
        >
          {user?.avatar_url
            ? <img className="header-avatar" src={user.avatar_url} alt={user.username || 'Perfil'} />
            : <span className="header-avatar header-avatar-empty">{isAuthenticated ? <UserRound size={19} /> : <LogIn size={17} />}</span>}
          {isAuthenticated && <span className="account-status-dot" aria-label="Sesión iniciada" />}
        </Link>
        {isAuthenticated && <button className="icon-button header-logout" type="button" aria-label="Cerrar sesión" title="Cerrar sesión" onClick={signOut}><LogOut size={18} /></button>}
      </div>
      {logoutError && <span className="header-auth-error" role="alert">{logoutError}</span>}
    </header>
  )
}
