import { NavLink } from 'react-router-dom'
import { Flame, PlusCircle, WalletCards, Trophy, LogIn } from 'lucide-react'

const items = [
  { to: '/', label: 'El Salseo', icon: Flame },
  { to: '/create', label: 'Crear Mesa', icon: PlusCircle },
  { to: '/portfolio', label: 'Cartera', icon: WalletCards },
  { to: '/leaderboard', label: 'Ranking', icon: Trophy },
]

export function DesktopNav() {
  return (
    <aside className="desktop-nav" aria-label="Navegación principal">
      {items.map(({ to, label, icon: Icon }) => (
        <NavLink key={to} to={to} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Icon size={20} /><span>{label}</span>
        </NavLink>
      ))}
      <NavLink to="/auth" className="nav-profile">
        <LogIn size={18} /> Acceso VIP
      </NavLink>
    </aside>
  )
}

export function BottomNav() {
  return (
    <nav className="bottom-nav" aria-label="Navegación móvil">
      {items.map(({ to, label, icon: Icon }) => (
        <NavLink key={to} to={to} className={({ isActive }) => `bottom-item ${isActive ? 'active' : ''}`}>
          <Icon size={21} /><span>{label}</span>
        </NavLink>
      ))}
    </nav>
  )
}
