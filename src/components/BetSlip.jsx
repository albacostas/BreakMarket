import { X, Minus, Plus, Sparkles } from 'lucide-react'
import { useBetSlip } from '../context/BetSlipContext'
import { useAuth } from '../context/AuthContext'
import { betService } from '../services/api'
import { useState } from 'react'

export default function BetSlip() {
  const { selection, amount, setAmount, closeSlip } = useBetSlip()
  const { user, refreshUser } = useAuth()
  const [submitting, setSubmitting] = useState(false)
  const [confirmed, setConfirmed] = useState(false)
  const [error, setError] = useState('')
  if (!selection) return null
  const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i
  const hasValidUser = Boolean(user?.id && uuidPattern.test(user.id))

  const quota = selection.option.odds || (selection.option.label?.toLowerCase().startsWith('sí') ? selection.market.yesOdds : selection.market.noOdds)
  const balance = user?.balance || 0
  const safeAmount = Math.max(0, Number(amount) || 0)
  const cost = safeAmount * quota
  const returnAmount = cost.toFixed(2)
  const maxAmount = quota > 0 ? Math.floor(balance / quota) : 0

  const confirm = async () => {
    if (!hasValidUser || safeAmount < 1 || cost > balance) {
      setError('Cierra sesión y entra de nuevo. Tu usuario actual no tiene un UUID válido.')
      return
    }
    setSubmitting(true)
    setError('')
    try {
      await betService.place({ market_id: selection.market.id, option_id: selection.option.id, user_id: user.id, amount: safeAmount })
      await refreshUser()
      window.dispatchEvent(new CustomEvent('bm-bet-placed'))
      setConfirmed(true)
      setTimeout(() => { setConfirmed(false); closeSlip() }, 900)
    } catch (err) {
      setError(err.response?.data?.message || 'La apuesta no pudo guardarse. Comprueba tu saldo y vuelve a intentarlo.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <button className="drawer-backdrop" onClick={closeSlip} aria-label="Cerrar" />
      <aside className="bet-slip">
        {confirmed ? (
          <div className="confirmed"><Sparkles size={38} /><h3>¡Salseo confirmado!</h3></div>
        ) : (
          <>
            <div className="slip-header"><div><span>Tu apuesta</span><h3>{selection.market.title}</h3></div><button onClick={closeSlip}><X size={20} /></button></div>
            <div className="selected-option"><div><small>Selección</small><strong>{selection.option.label}</strong></div><b>x{quota.toFixed(2)}</b></div>
            <label className="amount-label">Monto de la apuesta</label>
            <div className="amount-control">
              <button onClick={() => setAmount(Math.max(0, safeAmount - 10))}><Minus size={17} /></button>
              <input type="number" value={amount} min="0" max={maxAmount} onChange={(e) => setAmount(e.target.value)} />
              <button onClick={() => setAmount(safeAmount + 10)}><Plus size={17} /></button>
            </div>
            <div className="quick-amounts">
              {[10, 50, 100].map(v => <button key={v} onClick={() => setAmount(safeAmount + v)}>+{v}</button>)}
              <button onClick={() => setAmount(maxAmount)}>MAX</button>
            </div>
            <div className="return-box"><span>Retorno estimado</span><strong>{returnAmount} <b>💔</b></strong></div>
            {error && <div className="form-error">{error}</div>}
            <button className="confirm-bet" disabled={submitting || !hasValidUser || safeAmount < 1 || cost > balance} onClick={confirm}>
              {submitting ? 'Confirmando...' : hasValidUser ? 'Confirmar Salseo' : 'Inicia sesión con un usuario real'}
            </button>
          </>
        )}
      </aside>
    </>
  )
}
