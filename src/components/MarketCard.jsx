import { Link } from 'react-router-dom'
import { ArrowUpRight, Heart, Flame } from 'lucide-react'
import { useBetSlip } from '../context/BetSlipContext'

export default function MarketCard({ market, featured = false }) {
  const { openSlip } = useBetSlip()
  const multiple = market.market_type === 'multiple'
  return (
    <article className={`market-card ${featured ? 'featured-card' : ''}`}>
      <div className="card-top">
        <span className="category-tag">{market.categoryLabel}</span>
        <span className="volume"><ArrowUpRight size={13} /> {market.volume.toLocaleString('es-ES')} 💔</span>
      </div>
      <div className="couple-avatars">
        <img src={market.personA.avatar} alt={market.personA.name} />
        <div className="heart-seal"><Heart size={16} fill="currentColor" /></div>
        <img src={market.personB.avatar} alt={market.personB.name} />
      </div>
      <Link to={`/market/${market.id}`} className="market-question">
        <h3>{market.title}</h3>
      </Link>
      <div className="probability-row"><strong>{market.probability}%</strong><span>{market.closing}</span></div>
      {multiple ? (
        <button className="multiple-button" onClick={() => openSlip(market, market.options[0])}>Ver {market.options.length} opciones <ArrowUpRight size={16} /></button>
      ) : (
        <>
          <div className="probability-bar"><div style={{ width: `${market.probability}%` }} /><div /></div>
          <div className="quick-bets">
            <button className="bet-yes" onClick={() => openSlip(market, market.options[0])}><span>SÍ</span><b>x{market.yesOdds.toFixed(2)}</b></button>
            <button className="bet-no" onClick={() => openSlip(market, market.options[1])}><span>NO</span><b>x{market.noOdds.toFixed(2)}</b></button>
          </div>
        </>
      )}
    </article>
  )
}
