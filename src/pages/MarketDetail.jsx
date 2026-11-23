import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { Clock3, CircleDollarSign, Flame, Send, TrendingUp } from 'lucide-react'
import { commentService, marketService } from '../services/api'
import { useBetSlip } from '../context/BetSlipContext'
import { useAuth } from '../context/AuthContext'

export default function MarketDetail() {
  const { id } = useParams()
  const [market, setMarket] = useState(null)
  const [tab, setTab] = useState('rumors')
  const [comments, setComments] = useState([])
  const [activity, setActivity] = useState([])
  const [message, setMessage] = useState('')
  const [resolveOpen, setResolveOpen] = useState(false)
  const [error, setError] = useState('')
  const { openSlip } = useBetSlip()
  const { user } = useAuth()

  useEffect(() => {
    marketService.get(id).then(setMarket).catch(() => setError('No se pudo cargar este mercado desde la API.'))
    commentService.list(id).then(setComments).catch(() => setComments([]))
    commentService.activity(id).then(setActivity).catch(() => setActivity([]))
  }, [id])
  useEffect(() => {
    const refresh = () => {
      marketService.get(id).then(setMarket).catch(() => setError('No se pudo cargar este mercado desde la API.'))
      commentService.activity(id).then(setActivity).catch(() => setActivity([]))
    }
    window.addEventListener('bm-bet-placed', refresh)
    return () => window.removeEventListener('bm-bet-placed', refresh)
  }, [id])

  if (error) return <div className="loading-page api-error" role="alert">{error}</div>
  if (!market) return <div className="loading-page">Cargando el salseo...</div>
  const submitComment = async (event) => {
    event.preventDefault()
    if (!message.trim() || !user?.id) return
    const comment = await commentService.add(id, message.trim(), user.id)
    setComments([comment, ...comments])
    setMessage('')
  }

  return (
    <div className="detail-page">
      <section className="detail-header glass">
        <div className="detail-avatars"><img src={market.personA.avatar} alt={market.personA.name} /><div aria-hidden="true">VS</div><img src={market.personB.avatar} alt={market.personB.name} /></div>
        <div className="detail-title"><span className="category-tag">{market.categoryLabel}</span><h1>{market.title}</h1><div className="detail-meta"><b className="open-status">Abierto</b><span><Clock3 size={14} /> {market.closing}</span><span><CircleDollarSign size={14} /> {market.volume.toLocaleString('es-ES')} 💔</span></div></div>
        <span className="api-note">La API actual no permite resolver mercados</span>
      </section>

      <div className="detail-grid">
        <section className="chart-card glass" aria-labelledby="chart-title">
          <div className="section-heading"><div><TrendingUp size={18} /><h2 id="chart-title">Cotización</h2></div><strong>{market.probability}% SÍ</strong></div>
          <div className="chart-wrap">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={market.history} margin={{ top: 10, right: 2, bottom: 0, left: -25 }}>
                <defs><linearGradient id="probability" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#FF2A55" stopOpacity={0.38} /><stop offset="100%" stopColor="#D4AF37" stopOpacity={0} /></linearGradient></defs>
                <XAxis dataKey="time" tick={{ fill: '#C7B284', fontSize: 10 }} axisLine={false} tickLine={false} />
                <YAxis domain={[0, 100]} tick={{ fill: '#C7B284', fontSize: 10 }} axisLine={false} tickLine={false} />
                <Tooltip contentStyle={{ background: '#140307', border: '1px solid rgba(212,175,55,.45)', borderRadius: 12, color: '#FAF6EE' }} labelStyle={{ color: '#C7B284' }} />
                <Area type="monotone" dataKey="probability" stroke="#FF2A55" strokeWidth={3} fill="url(#probability)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </section>

        <section className="options-card glass" aria-labelledby="options-title">
          <div className="section-heading"><h2 id="options-title">Opciones</h2></div>
          {market.options.map((option) => (
            <button key={option.id} className="option-row" onClick={() => openSlip(market, option)}>
              {option.avatar && <img src={option.avatar} alt="" />}
              <div className="option-info"><div><strong>{option.label}</strong><b>{option.probability}%</b></div><div className="option-progress"><i style={{ width: `${option.probability}%` }} /></div></div>
              <span>x{option.odds.toFixed(2)}</span>
            </button>
          ))}
        </section>
      </div>

      <section className="social-card glass">
        <div className="tabs" role="tablist" aria-label="Actividad del mercado">
          <button type="button" role="tab" aria-selected={tab === 'rumors'} className={tab === 'rumors' ? 'active' : ''} onClick={() => setTab('rumors')}><Flame size={17} /> Pruebas y rumores</button>
          <button type="button" role="tab" aria-selected={tab === 'activity'} className={tab === 'activity' ? 'active' : ''} onClick={() => setTab('activity')}>Actividad</button>
        </div>
        {tab === 'rumors' ? (
          <>
            <form className="comment-form" onSubmit={submitComment}>
              <input value={message} onChange={(e) => setMessage(e.target.value)} placeholder={user?.id ? 'Deja una pista o rumor...' : 'Inicia sesión para dejar una pista...'} aria-label="Añadir comentario o rumor" />
              <button type="submit" aria-label="Enviar comentario"><Send size={17} /></button>
            </form>
            <div className="comments">{comments.map((c) => <article key={c.id}><img src={c.avatar} alt="" /><div><div><strong>@{c.author}</strong><span>{c.time}</span></div><p>{c.text}</p><button type="button" onClick={() => { c.upvotes += 1; setComments([...comments]) }}>🔥 {c.upvotes}</button></div></article>)}</div>
          </>
        ) : (
          <div className="activity-list">{activity.map((a) => <article key={a.id}><div className="activity-avatar" aria-hidden="true">{a.user?.[0] || 'U'}</div><p><strong>{a.user}</strong> apostó <b>{a.amount} 💔</b> a <span>{a.option}</span></p><small>{a.time}</small></article>)}</div>
        )}
      </section>

      {resolveOpen && <div className="modal-backdrop" onClick={() => setResolveOpen(false)}><div className="resolve-modal glass" onClick={(e) => e.stopPropagation()}><h2>Resolver mercado</h2><p>Selecciona la opción ganadora.</p>{market.options.map((o) => <button key={o.id} onClick={async () => { await marketService.resolve(id, o.id); setResolveOpen(false) }}>{o.label}</button>)}</div></div>}
    </div>
  )
}
