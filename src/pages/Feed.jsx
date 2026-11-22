import { useEffect, useState } from 'react'
import { Flame, ChevronRight } from 'lucide-react'
import MarketCard from '../components/MarketCard'
import { marketService } from '../services/api'

const filters = [
  ['todos', 'Todos'], ['rupturas', '💔 Rupturas'], ['cuernos', '🐍 Cuernos'],
  ['lios', '💋 Líos/Besos'], ['fechas', '⏳ Fechas límite'],
]

export default function Feed() {
  const [markets, setMarkets] = useState([])
  const [filter, setFilter] = useState('todos')
  const [search, setSearch] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [reload, setReload] = useState(0)

  useEffect(() => {
    setError('')
    setLoading(true)
    marketService.list({ category: filter, search })
      .then((data) => setMarkets(data))
      .catch(() => {
        setMarkets([])
        setError('No se pudo conectar con la API. Comprueba que Spring Boot está ejecutándose en el puerto 8080.')
      })
      .finally(() => setLoading(false))
  }, [filter, search, reload])
  useEffect(() => {
    const handler = (event) => setSearch(event.detail)
    window.addEventListener('bm-search', handler)
    return () => window.removeEventListener('bm-search', handler)
  }, [])
  useEffect(() => {
    const refresh = () => marketService.list({ category: filter, search }).then(setMarkets).catch(() => setMarkets([]))
    window.addEventListener('bm-bet-placed', refresh)
    return () => window.removeEventListener('bm-bet-placed', refresh)
  }, [filter, search])

  return (
    <>
      <div className="feed-page">
        <section className="flames-section">
          <div className="section-heading"><div><Flame size={20} fill="currentColor" /><h2>En llamas</h2></div><button>Ver todos <ChevronRight size={16} /></button></div>
          <div className="flames-row">
            {markets.filter(m => m.trending).slice(0, 3).map(m => <MarketCard key={m.id} market={m} featured />)}
          </div>
        </section>
        <section className="filters" aria-label="Filtros">
          {filters.map(([value, label]) => <button key={value} className={filter === value ? 'active' : ''} onClick={() => setFilter(value)}>{label}</button>)}
        </section>
        <section className="market-grid">
          {loading ? (
            [1, 2, 3, 4, 5, 6].map((item) => <div className="market-card skeleton-card" key={item}><div className="skeleton skeleton-tag" /><div className="skeleton skeleton-avatar" /><div className="skeleton skeleton-title" /><div className="skeleton skeleton-line" /><div className="skeleton skeleton-bets" /></div>)
          ) : error ? (
            <div className="api-error"><strong>La pista no llegó</strong><span>{error}</span><button onClick={() => setReload((value) => value + 1)}>Reintentar</button></div>
          ) : markets.length === 0 ? (
            <div className="empty-state">No hay mercados que coincidan con tu búsqueda.</div>
          ) : markets.map(m => <MarketCard key={m.id} market={m} />)}
        </section>
      </div>
    </>
  )
}
