import { Search, Bell, Heart, LogIn } from 'lucide-react'
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
      <Link to="/" className="brand">
        <span className="brand-mark"><Heart size={18} fill="currentColor" /></span>
        <span>Break<span>Market</span></span>
      </Link>
      <label className="search-box">
        <Search size={17} />
        <input value={localSearch} onChange={(e) => updateSearch(e.target.value)} placeholder="Buscar salseo..." />
      </label>
      <div className="header-actions">
        <button className="icon-button" aria-label="Notificaciones"><Bell size={19} /><i /></button>
        <Link to="/portfolio" className="balance-badge"><span>Saldo</span><strong>{user?.balance?.toLocaleString('es-ES')} <b>💔</b></strong></Link>
        <Link to="/auth" aria-label={user ? 'Abrir perfil' : 'Iniciar sesión'}>
          {user?.avatar_url ? <img className="header-avatar" src={user.avatar_url} alt="" /> : <span className="header-avatar header-avatar-empty"><LogIn size={17} /></span>}
        </Link>
      </div>
    </header>
  )
}
