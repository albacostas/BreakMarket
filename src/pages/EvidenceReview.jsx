import { useCallback, useEffect, useState } from 'react'
import { Check, RefreshCw, X } from 'lucide-react'
import { Link } from 'react-router-dom'
import { evidenceService } from '../services/api'
import { useAuth } from '../context/AuthContext'

const formatDate = (value) => new Intl.DateTimeFormat('es-ES', {
  dateStyle: 'medium',
  timeStyle: 'short',
}).format(new Date(value))

export default function EvidenceReview() {
  const { user, loading } = useAuth()
  const [evidence, setEvidence] = useState([])
  const [loadingEvidence, setLoadingEvidence] = useState(true)
  const [error, setError] = useState('')
  const [reviewing, setReviewing] = useState('')
  const [notice, setNotice] = useState('')

  const loadEvidence = useCallback(async () => {
    setError('')
    setLoadingEvidence(true)
    try {
      setEvidence(await evidenceService.pending())
    } catch {
      setError('No se pudieron cargar las pruebas pendientes. Inténtalo de nuevo.')
    } finally {
      setLoadingEvidence(false)
    }
  }, [])

  useEffect(() => {
    if (!loading && user?.role === 'ADMIN') loadEvidence()
  }, [loading, user?.role, loadEvidence])

  const review = async (item, state) => {
    setReviewing(item.uuid)
    setError('')
    setNotice('')
    try {
      await evidenceService.review(item.uuid, user.id, state)
      setEvidence((current) => current.filter((evidenceItem) => evidenceItem.uuid !== item.uuid))
      setNotice(state === 'VALIDATED' ? 'Prueba validada.' : 'Prueba rechazada.')
    } catch {
      setError('No se pudo guardar la revisión. Comprueba tus permisos e inténtalo de nuevo.')
    } finally {
      setReviewing('')
    }
  }

  if (loading) return <div className="loading-page">Comprobando permisos...</div>
  if (user?.role !== 'ADMIN') {
    return <div className="evidence-review-page"><div className="api-error" role="alert"><strong>Acceso restringido</strong><span>Solo las personas revisoras pueden revisar pruebas.</span></div></div>
  }

  return (
    <div className="evidence-review-page">
      <header className="review-heading">
        <div><span>Moderación</span><h1>Revisar pruebas</h1><p>Valida o rechaza las pruebas enviadas a los mercados.</p></div>
        <button type="button" className="review-refresh" onClick={loadEvidence} disabled={loadingEvidence}><RefreshCw size={17} /> Actualizar</button>
      </header>
      {error && <div className="form-error" role="alert">{error}</div>}
      {notice && <p className="review-notice" role="status">{notice}</p>}
      {loadingEvidence ? <div className="loading-page">Cargando pruebas pendientes...</div> : evidence.length ? (
        <div className="evidence-review-list">
          {evidence.map((item) => (
            <article className="evidence-review-card glass" key={item.uuid}>
              <div className="evidence-review-meta">
                <span>Pendiente</span>
                <time dateTime={item.submittedDate}>{formatDate(item.submittedDate)}</time>
              </div>
              <p>{item.comment}</p>
              <div className="evidence-review-footer">
                <span>Enviada por {item.authorUuid}</span>
                <Link to={`/market/${item.betUuid}`}>Ver mercado</Link>
              </div>
              <div className="evidence-review-actions">
                <button type="button" className="evidence-approve" disabled={reviewing === item.uuid} onClick={() => review(item, 'VALIDATED')}><Check size={16} /> Validar</button>
                <button type="button" className="evidence-reject" disabled={reviewing === item.uuid} onClick={() => review(item, 'NOT_ACCEPTED')}><X size={16} /> Rechazar</button>
              </div>
            </article>
          ))}
        </div>
      ) : <div className="empty-state">No hay pruebas pendientes de revisión.</div>}
    </div>
  )
}
