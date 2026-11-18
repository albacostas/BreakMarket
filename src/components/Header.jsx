import { Search, Bell, Heart } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { Link } from 'react-router-dom'

export default function Header({ search, onSearch }) {
  const { user } = useAuth()
  return (
    <header className="app-header">
      <Link to="/" className="brand">
        <span className="brand-mark"><Heart size={18} fill="currentColor" /></span>
        <span>Break<span>Market</span></span>
      </Link>
      <label className="search-box">
        <Search size={17} />
        <input value={search || ''} onChange={(e) => { onSearch?.(e.target.value); window.dispatchEvent(new CustomEvent('bm-search', { detail: e.target.value })) }} placeholder="Buscar salseo..." />
      </label>
      <div className="header-actions">
        <button className="icon-button" aria-label="Notificaciones"><Bell size={19} /><i /></button>
        <Link to="/portfolio" className="balance-badge"><span>Saldo</span><strong>{user?.balance?.toLocaleString('es-ES')} <b>💔</b></strong></Link>
        <Link to="/auth"><img className="header-avatar" src={user?.avatar_url} alt="" /></Link>
      </div>
    </header>
  )
}
