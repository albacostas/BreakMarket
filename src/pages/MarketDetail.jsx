import { useCallback, useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import { CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { Clock3, CircleDollarSign, Send, TrendingUp } from 'lucide-react'
import { evidenceService, marketService, ticketService } from '../services/api'
import { useBetSlip } from '../context/BetSlipContext'
import { useAuth } from '../context/AuthContext'

const chartTime = (timestamp) => new Intl.DateTimeFormat('es-ES', {
  day: '2-digit',
  month: 'short',
  hour: '2-digit',
  minute: '2-digit',
}).format(new Date(timestamp))

export default function MarketDetail() {
  const { id } = useParams()
  const [market, setMarket] = useState(null)
  const [evidence, setEvidence] = useState([])
  const [evidenceLoading, setEvidenceLoading] = useState(true)
  const [evidenceError, setEvidenceError] = useState('')
  const [activity, setActivity] = useState([])
  const [message, setMessage] = useState('')
  const [submittingEvidence, setSubmittingEvidence] = useState(false)
  const [error, setError] = useState('')
  const { openSlip } = useBetSlip()
  const { user } = useAuth()
  const [activityLoading, setActivityLoading] = useState(true)
  const [activityError, setActivityError] = useState('')

  const rateHistory = useMemo(() => {
    if (!market) return []
    const tickets = activity
      .map((ticket) => ({
        ...ticket,
        timestamp: Date.parse(ticket.createdDate),
      }))
      .filter((ticket) => Number.isFinite(ticket.timestamp)
        && Number.isFinite(ticket.amount)
        && ticket.amount > 0
        && market.options.some((option) => option.id === ticket.option))
      .sort((a, b) => a.timestamp - b.timestamp || a.id.localeCompare(b.id))

    const pools = Object.fromEntries(market.options.map((option) => [option.id, 0]))
    return tickets.map((ticket) => {
      pools[ticket.option] += ticket.amount
      const totalVolume = Object.values(pools).reduce((total, volume) => total + volume, 0)
      return {
        timestamp: ticket.timestamp,
        ...Object.fromEntries(market.options.map((option) => [
          option.id,
          totalVolume > 0 ? pools[option.id] / totalVolume : 0,
        ])),
      }
    })
  }, [activity, market])

  const chartDomain = useMemo(() => {
    if (!rateHistory.length) return undefined
    const timestamps = rateHistory.map((point) => point.timestamp)
    const min = Math.min(...timestamps)
    const max = Math.max(...timestamps)
    const padding = Math.max((max - min) * 0.05, 60_000)
    return [min - padding, max + padding]
  }, [rateHistory])

  const loadActivity = useCallback(() => {
    setActivityLoading(true)
    setActivityError('')
    ticketService.listForBet(id)
      .then(setActivity)
      .catch(() => {
        setActivity([])
        setActivityError('No se pudo cargar el historial de tickets.')
      })
      .finally(() => setActivityLoading(false))
  }, [id])

  useEffect(() => {
    marketService.get(id).then(setMarket).catch(() => setError('No se pudo cargar este mercado desde la API.'))
    setEvidence([])
    setEvidenceError('')
    setEvidenceLoading(true)
    evidenceService.listForBet(id)
      .then(setEvidence)
      .catch(() => setEvidenceError('No se pudieron cargar las pruebas de este mercado.'))
      .finally(() => setEvidenceLoading(false))
    loadActivity()
  }, [id, loadActivity])
  useEffect(() => {
    const refresh = () => {
      marketService.get(id).then(setMarket).catch(() => setError('No se pudo cargar este mercado desde la API.'))
      loadActivity()
    }
    window.addEventListener('bm-bet-placed', refresh)
    return () => window.removeEventListener('bm-bet-placed', refresh)
  }, [id, loadActivity])

  if (error) return <div className="loading-page api-error" role="alert">{error}</div>
  if (!market) return <div className="loading-page">Cargando el salseo...</div>
  const submitComment = async (event) => {
    event.preventDefault()
    if (!message.trim() || !user?.id || submittingEvidence) return
    setSubmittingEvidence(true)
    setEvidenceError('')
    try {
      const submitted = await evidenceService.submit(id, message.trim(), user.id)
      setEvidence((current) => [submitted, ...current])
      setMessage('')
    } catch {
      setEvidenceError('No se pudo enviar la prueba. Inténtalo de nuevo.')
    } finally {
      setSubmittingEvidence(false)
    }
  }

  const evidenceStateLabel = { PENDING: 'Pendiente de revisión', VALIDATED: 'Validada', NOT_ACCEPTED: 'No aceptada' }

  return (
    <div className="detail-page">
      <section className="detail-header glass">
        <div className="detail-avatars"><img src={market.personA.avatar} alt={market.personA.name} /><div aria-hidden="true">VS</div><img src={market.personB.avatar} alt={market.personB.name} /></div>
        <div className="detail-title"><span className="bet-type-tag">{market.typeLabel}</span><h1>{market.title}</h1><div className="detail-meta"><b className="open-status">{['ACTIVE', 'OPEN'].includes(market.status) ? 'Abierto' : 'Cerrado'}</b><span><Clock3 size={14} /> {market.closing}</span><span><CircleDollarSign size={14} /> {market.volume.toLocaleString('es-ES')} 💔</span></div></div>
        <span className="api-note">La API permite resolver tickets, no mercados</span>
      </section>

      <div className="detail-grid">
        <section className="chart-card glass" aria-labelledby="chart-title">
          <div className="section-heading"><div><TrendingUp size={18} /><h2 id="chart-title">Cotización</h2></div><span className="chart-caption">Historial de cuotas por tickets</span></div>
          {activityLoading ? <p className="couple-state">Cargando historial de cotización...</p> : activityError ? (
            <p className="form-error" role="alert">{activityError}</p>
          ) : rateHistory.length ? (
            <div className="chart-wrap">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={rateHistory} margin={{ top: 12, right: 14, bottom: 4, left: 8 }}>
                  <CartesianGrid stroke="rgba(212,175,55,.14)" strokeDasharray="3 3" />
                  <XAxis
                    type="number"
                    dataKey="timestamp"
                    domain={chartDomain}
                    tickFormatter={chartTime}
                    tick={{ fill: '#C7B284', fontSize: 10 }}
                    axisLine={false}
                    tickLine={false}
                  />
                  <YAxis
                    domain={['auto', 'auto']}
                    tickFormatter={(value) => `x${Number(value).toFixed(2)}`}
                    tick={{ fill: '#C7B284', fontSize: 10 }}
                    axisLine={false}
                    tickLine={false}
                    width={58}
                  />
                  <Tooltip
                    labelFormatter={chartTime}
                    formatter={(value, name) => [`x${Number(value).toFixed(2)}`, name]}
                    contentStyle={{ background: '#140307', border: '1px solid rgba(212,175,55,.45)', borderRadius: 12, color: '#FAF6EE' }}
                    labelStyle={{ color: '#C7B284' }}
                  />
                  <Legend wrapperStyle={{ color: '#C7B284', fontSize: 11 }} />
                  {market.options.map((option, index) => (
                    <Line
                      key={option.id}
                      type="monotone"
                      dataKey={option.id}
                      name={option.label}
                      stroke={['#FF476C', '#F6E27F', '#64D69A', '#8DA7FF', '#D98CFF'][index % 5]}
                      strokeWidth={2.5}
                      dot={{ r: 3 }}
                      activeDot={{ r: 5 }}
                      connectNulls
                      isAnimationActive={false}
                    />
                  ))}
                </LineChart>
              </ResponsiveContainer>
            </div>
          ) : <div className="empty-state chart-empty-state">Aún no hay tickets para mostrar la evolución de las cuotas.</div>}
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

      <section className="social-card glass" aria-labelledby="evidence-title">
        <div className="section-heading"><div><h2 id="evidence-title">Pruebas del mercado</h2></div></div>
        <p className="evidence-intro">Aporta información que ayude a verificar el resultado. Las pruebas quedan pendientes hasta que una persona revisora las evalúe.</p>
        <form className="evidence-form" onSubmit={submitComment}>
          <label htmlFor="evidence-comment">Añadir una prueba</label>
          <textarea id="evidence-comment" value={message} onChange={(event) => setMessage(event.target.value)} placeholder={user?.id ? 'Describe la prueba y su relación con este mercado...' : 'Inicia sesión para aportar una prueba.'} disabled={!user?.id || submittingEvidence} required maxLength={2000} />
          <button type="submit" className="next-button" disabled={!user?.id || !message.trim() || submittingEvidence}><Send size={16} /> {submittingEvidence ? 'Enviando...' : 'Enviar prueba'}</button>
        </form>
        {evidenceError && <p className="form-error" role="alert">{evidenceError}</p>}
        {evidenceLoading ? <p className="couple-state">Cargando pruebas...</p> : evidence.length ? (
          <div className="evidence-list">{evidence.map((item) => (
            <article key={item.id} className="evidence-item">
              <div className="evidence-item-heading"><strong>{item.author === user?.id ? 'Tu prueba' : 'Prueba enviada'}</strong><span className={`evidence-state evidence-state-${item.state.toLowerCase()}`}>{evidenceStateLabel[item.state] || item.state}</span></div>
              <p>{item.text}</p>
              <time>{item.time}</time>
            </article>
          ))}</div>
        ) : <div className="empty-state">Todavía no hay pruebas para este mercado.</div>}
      </section>

      <section className="social-card glass" aria-labelledby="activity-title">
        <div className="section-heading"><div><h2 id="activity-title">Actividad del mercado</h2></div></div>
        {activity.length ? <div className="activity-list">{activity.map((a) => {
          const optionLabel = market.options.find((option) => option.id === a.option)?.label || a.option
          return <article key={a.id}><div className="activity-avatar" aria-hidden="true">{a.user?.[0] || 'U'}</div><p><strong>{a.user}</strong> apostó <b>{a.amount} 💔</b> a <span>{optionLabel}</span> · x{a.rate.toFixed(2)}</p><small>{a.time}</small></article>
        })}</div> : <div className="empty-state">Aún no hay actividad de apuestas.</div>}
      </section>
    </div>
  )
}
