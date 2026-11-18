import { useEffect, useState } from 'react'
import { Coins, TrendingUp, WalletCards, Crown } from 'lucide-react'
import { betService } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function Portfolio() {
  const [tab, setTab] = useState('active')
  const [bets, setBets] = useState([])
  const { user } = useAuth()
  useEffect(() => { betService.portfolio(tab, user?.id).then(setBets).catch(() => setBets([])) }, [tab, user?.id])

  return (
    <div className="portfolio-page">
      <h1>Mi cartera</h1>
      <section className="stats-grid">
        <article className="balance-card"><div><span>Balance disponible</span><button><WalletCards size={17}/></button></div><strong>{(user?.balance || 0).toLocaleString('es-ES')} <b>💔</b></strong></article>
        <article><span>Total invertido</span><strong>1.130 <b>💔</b></strong><small><TrendingUp size={14}/> Esta semana</small></article>
        <article><span>Beneficio neto</span><strong className="positive">+320 <b>💔</b></strong><small>ROI +28.3%</small></article>
      </section>
      <section className="portfolio-list glass">
        <div className="tabs"><button className={tab === 'active' ? 'active' : ''} onClick={() => setTab('active')}>Activas</button><button className={tab === 'resolved' ? 'active' : ''} onClick={() => setTab('resolved')}>Resueltas</button></div>
        {bets.length ? bets.map(bet => (
          <article className="bet-row" key={bet.id}>
            <div className="bet-status-icon"><Coins size={18}/></div>
            <div className="bet-row-main"><h3>{bet.market}</h3><p>{bet.option} · x{bet.odds.toFixed(2)}</p></div>
            <div className="bet-amount"><span>Apostado</span><strong>{bet.amount} 💔</strong></div>
            {bet.status === 'RESOLVED' ? <b className={`result ${bet.result.toLowerCase()}`}>{bet.result === 'WON' ? 'GANADA' : 'PERDIDA'}</b> : <div className="potential"><span>Retorno</span><strong>{bet.potential} 💔</strong></div>}
          </article>
        )) : <div className="empty-state">No tienes apuestas en esta sección.</div>}
      </section>
    </div>
  )
}
