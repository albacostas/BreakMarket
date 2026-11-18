import { useEffect, useState } from 'react'
import { Flame, ChevronRight } from 'lucide-react'
import Header from '../components/Header'
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

  useEffect(() => {
    setError('')
    marketService.list({ category: filter, search })
      .then(setMarkets)
      .catch(() => {
        setMarkets([])
        setError('No se pudo conectar con la API. Comprueba que Spring Boot está ejecutándose en el puerto 8080.')
      })
  }, [filter, search])
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
      <Header search={search} onSearch={setSearch} />
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
          {error ? <div className="api-error">{error}</div> : markets.map(m => <MarketCard key={m.id} market={m} />)}
        </section>
      </div>
    </>
  )
}
