import { NavLink } from 'react-router-dom'
import { Flame, PlusCircle, WalletCards, Trophy, LogIn, ClipboardCheck } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

const items = [
  { to: '/', label: 'El Salseo', icon: Flame },
  { to: '/create', label: 'Crear Mesa', icon: PlusCircle },
  { to: '/portfolio', label: 'Cartera', icon: WalletCards },
  { to: '/leaderboard', label: 'Ranking', icon: Trophy },
]

export function DesktopNav() {
  const { user } = useAuth()
  return (
    <aside className="desktop-nav" aria-label="Navegación principal">
      {items.map(({ to, label, icon: Icon }) => (
        <NavLink key={to} to={to} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Icon size={20} /><span>{label}</span>
        </NavLink>
      ))}
      {user?.role === 'ADMIN' && <NavLink to="/evidence-review" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}><ClipboardCheck size={20} /><span>Revisar pruebas</span></NavLink>}
      <NavLink to="/auth" className="nav-profile">
        <LogIn size={18} /> Acceso VIP
      </NavLink>
    </aside>
  )
}

export function BottomNav() {
  const { user } = useAuth()
  return (
    <nav className={`bottom-nav ${user?.role === 'ADMIN' ? 'has-review-link' : ''}`} aria-label="Navegación móvil">
      {items.map(({ to, label, icon: Icon }) => (
        <NavLink key={to} to={to} className={({ isActive }) => `bottom-item ${isActive ? 'active' : ''}`}>
          <Icon size={21} /><span>{label}</span>
        </NavLink>
      ))}
      {user?.role === 'ADMIN' && <NavLink to="/evidence-review" className={({ isActive }) => `bottom-item ${isActive ? 'active' : ''}`}><ClipboardCheck size={21} /><span>Pruebas</span></NavLink>}
    </nav>
  )
}
