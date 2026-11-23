import { Search, Bell, LogIn, Spade } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'

export default function Header({ search, onSearch }) {
  const { user } = useAuth()
  const [localSearch, setLocalSearch] = useState(search || '')
  useEffect(() => setLocalSearch(search || ''), [search])

  const updateSearch = (value) => {
    setLocalSearch(value)
    onSearch?.(value)
    window.dispatchEvent(new CustomEvent('bm-search', { detail: value }))
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
        <Link to="/auth" aria-label={user ? 'Abrir perfil' : 'Iniciar sesión'}>
          {user?.avatar_url ? <img className="header-avatar" src={user.avatar_url} alt={user.username || 'Perfil'} /> : <span className="header-avatar header-avatar-empty"><LogIn size={17} /></span>}
        </Link>
      </div>
    </header>
  )
}
