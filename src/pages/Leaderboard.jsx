import { useEffect, useState } from 'react'
import { Crown, TrendingUp, Target } from 'lucide-react'
import { userService } from '../services/api'

export default function Leaderboard() {
  const [users, setUsers] = useState([])
  const [error, setError] = useState('')
  useEffect(() => {
    userService.leaderboard().then(setUsers).catch(() => setError('No se pudo cargar el ranking porque la API no está disponible.'))
  }, [])
  const podium = [users[1], users[0], users[2]].filter(Boolean)

  return (
    <div className="leaderboard-page">
      <div className="leaderboard-heading"><div><span>CLUB BREAKMARKET</span><h1>Los mejores cotillas</h1></div><button>Esta semana</button></div>
      <section className="podium">
        {podium.map((user, index) => {
          const place = index === 0 ? 2 : index === 1 ? 1 : 3
          return <article key={user.rank} className={`podium-user place-${place}`}><div className="podium-crown">{place === 1 && <Crown size={27} fill="currentColor"/>}<span>{place}</span></div><img src={user.avatar} alt="" /><h3>{user.username}</h3><strong>{user.balance.toLocaleString('es-ES')} 💔</strong><small>{user.roi} ROI</small></article>
        })}
      </section>
      <section className="ranking-table glass">
        {error && <div className="api-error">{error}</div>}
        <div className="ranking-head"><span>RANKING</span><span>USUARIO</span><span>SALDO</span><span>ROI</span><span>ACIERTOS</span></div>
        {users.map(user => <article key={user.rank}><b>#{String(user.rank).padStart(2,'0')}</b><div><img src={user.avatar} alt="" /><strong>{user.username}</strong></div><strong>{user.balance.toLocaleString('es-ES')} <small>💔</small></strong><span className="positive"><TrendingUp size={14}/>{user.roi}</span><span><Target size={14}/>{user.accuracy}</span></article>)}
      </section>
    </div>
  )
}
