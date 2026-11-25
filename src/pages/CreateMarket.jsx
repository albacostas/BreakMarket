import { useEffect, useMemo, useState } from 'react'
import { ArrowLeft, ArrowRight, Plus, Trash2, Heart } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { coupleService, marketService, personService } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function CreateMarket() {
  const [step, setStep] = useState(1)
  const [type, setType] = useState('YES_NO')
  const [form, setForm] = useState({ title: '', person_a: '', person_b: '', closes_at: '' })
  const [coupleMode, setCoupleMode] = useState('existing')
  const [couples, setCouples] = useState([])
  const [couplesLoaded, setCouplesLoaded] = useState(false)
  const [couplesLoading, setCouplesLoading] = useState(false)
  const [couplesError, setCouplesError] = useState('')
  const [coupleSearch, setCoupleSearch] = useState('')
  const [selectedCouple, setSelectedCouple] = useState(null)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const { user } = useAuth()
  const [options, setOptions] = useState(['Con su ex', 'Compañero/a'])
  const navigate = useNavigate()

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })
  const filteredCouples = useMemo(() => {
    const query = coupleSearch.trim().toLocaleLowerCase()
    return couples.filter((couple) => couple.members.some((member) => member.fullName.toLocaleLowerCase().includes(query)))
  }, [couples, coupleSearch])

  useEffect(() => {
    if (step !== 2 || couplesLoaded || couplesLoading || couplesError) return
    setCouplesLoading(true)
    coupleService.list()
      .then((results) => {
        setCouples(results)
        setCouplesLoaded(true)
      })
      .catch(() => setCouplesError('No se pudieron cargar las parejas existentes. Puedes volver a intentarlo o crear una nueva.'))
      .finally(() => setCouplesLoading(false))
  }, [step, couplesLoaded, couplesLoading, couplesError])

  const goNext = () => {
    setError('')
    if (step === 2) {
      if (!form.title.trim()) {
        setError('Escribe la pregunta del mercado.')
        return
      }
      if (coupleMode === 'existing' && !selectedCouple) {
        setError('Busca y selecciona la pareja sobre la que se hará la apuesta.')
        return
      }
      if (coupleMode === 'new' && (!form.person_a.trim() || !form.person_b.trim())) {
        setError('Escribe el nombre de las dos personas de la nueva pareja.')
        return
      }
      if (coupleMode === 'new' && form.person_a.trim().toLocaleLowerCase() === form.person_b.trim().toLocaleLowerCase()) {
        setError('Una pareja debe estar formada por dos personas distintas.')
        return
      }
    }
    setStep(step + 1)
  }

  const submit = async () => {
    setError('')
    setSubmitting(true)
    try {
      if (!user?.id) throw new Error('Debes iniciar sesión antes de crear un mercado.')
      if (!form.title.trim()) throw new Error('Escribe la pregunta del mercado.')
      let couple = selectedCouple
      if (coupleMode === 'new') {
        const names = [form.person_a.trim(), form.person_b.trim()]
        const alreadyExists = couples.find((candidate) => {
          const members = candidate.members.map((member) => member.fullName.toLocaleLowerCase())
          return members.length === 2 && names.every((name) => members.includes(name.toLocaleLowerCase()))
        })
        if (alreadyExists) throw new Error('Esa pareja ya existe. Selecciónala desde la búsqueda de parejas existentes.')

        const [personA, personB] = await Promise.all(names.map((name) => personService.findOrCreate(name)))
        couple = await coupleService.create([personA.uuid, personB.uuid])
      }
      await marketService.create({
        title: form.title,
        type,
        options: type === 'DATE' ? options : type === 'YES_NO' ? ['Sí', 'No'] : options,
        closes_at: form.closes_at,
        creatorUuid: user.id,
        coupleUuid: couple.uuid,
      })
      navigate('/')
    } catch (submitError) {
      setError(submitError.message || 'No se pudo crear el mercado.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="create-page">
      <div className="create-heading">
        <button type="button" aria-label="Volver" onClick={() => (step > 1 ? setStep(step - 1) : navigate('/'))}><ArrowLeft size={18} /></button>
        <div><span>Paso {step} de 3</span><div className="step-line"><i style={{ width: `${step * 33.33}%` }} /></div></div>
      </div>
      <div className="create-card glass">
        {step === 1 && <>
          <div className="create-title"><h1>Abre una nueva mesa</h1><p>Elige el formato del mercado y prepara la jugada.</p></div>
          <div className="type-grid">
            {[['YES_NO', 'Sí / No', 'Dos resultados posibles', '⚖️'], ['PERSON', 'Por persona', 'Elige entre varias personas', '🐍'], ['DATE', 'Por fecha', 'Elige entre varias fechas', '⏳']].map(([value, title, desc, icon]) => (
              <button key={value} type="button" className={type === value ? 'active' : ''} onClick={() => { setType(value); if (value === 'DATE') setOptions(['2026-12-01', '2026-12-31']); else if (value === 'PERSON') setOptions(['Con su ex', 'Compañero/a']) }}>
                <span aria-hidden="true">{icon}</span><strong>{title}</strong><small>{desc}</small>
              </button>
            ))}
          </div>
        </>}
        {step === 2 && <>
          <div className="create-title"><h1>Elige la pareja</h1><p>Cada apuesta se crea sobre una pareja. Busca una existente o registra una nueva.</p></div>
          <div className="create-form">
            <label>Título / Pregunta<input value={form.title} onChange={set('title')} placeholder="¿Romperán antes de fin de año?" /></label>
            <div className="couple-mode-tabs" role="tablist" aria-label="Cómo elegir la pareja">
              <button type="button" role="tab" aria-selected={coupleMode === 'existing'} className={coupleMode === 'existing' ? 'active' : ''} onClick={() => { setCoupleMode('existing'); setSelectedCouple(null); setError('') }}>Pareja existente</button>
              <button type="button" role="tab" aria-selected={coupleMode === 'new'} className={coupleMode === 'new' ? 'active' : ''} onClick={() => { setCoupleMode('new'); setSelectedCouple(null); setError('') }}>Crear pareja</button>
            </div>
            {coupleMode === 'existing' ? <>
              <label>Buscar por nombre<input value={coupleSearch} onChange={(event) => setCoupleSearch(event.target.value)} placeholder="Escribe el nombre de una persona" /></label>
              {couplesLoading ? <p className="couple-state">Cargando parejas...</p> : couplesError ? <div className="form-error" role="alert">{couplesError}<button type="button" onClick={() => setCouplesError('')}>Reintentar</button></div> : (
                <div className="couple-results">
                  {filteredCouples.map((couple) => (
                    <button type="button" key={couple.uuid} className={`couple-option ${selectedCouple?.uuid === couple.uuid ? 'selected' : ''}`} onClick={() => setSelectedCouple(couple)}>
                      <span>{couple.members.map((member) => member.fullName).join(' ♥ ')}</span>
                      <small>{couple.status.replace(/_/g, ' ')}</small>
                    </button>
                  ))}
                  {!filteredCouples.length && <p className="couple-state">{coupleSearch ? 'No hay parejas con ese nombre.' : 'No hay parejas registradas todavía. Puedes crear una.'}</p>}
                </div>
              )}
            </> : (
              <div className="person-fields">
                <label>Persona A<input value={form.person_a} onChange={set('person_a')} placeholder="Nombre" /></label>
                <div aria-hidden="true"><Heart size={17} fill="currentColor" /></div>
                <label>Persona B<input value={form.person_b} onChange={set('person_b')} placeholder="Nombre" /></label>
              </div>
            )}
          </div>
        </>}
        {step === 3 && <>
          <div className="create-title"><h1>Últimos detalles</h1><p>Configura las opciones y la hora de cierre de la mesa.</p></div>
          <div className="create-form">
            {type !== 'YES_NO' && <div className="dynamic-options"><label>Opciones</label>{options.map((option, i) => <div key={i}><input value={option} onChange={(e) => setOptions(options.map((o, j) => (j === i ? e.target.value : o)))} /><button type="button" aria-label={`Eliminar opción ${i + 1}`} onClick={() => setOptions(options.filter((_, j) => j !== i))}><Trash2 size={16} /></button></div>)}<button type="button" className="add-option" onClick={() => setOptions([...options, 'Nueva opción'])}><Plus size={16} /> Añadir opción</button></div>}
            <label>Fecha y hora de cierre<input type="datetime-local" value={form.closes_at} onChange={set('closes_at')} /></label>
          </div>
        </>}
        {error && <div className="form-error" role="alert">{error}</div>}
        <div className="create-actions">
          {step > 1 && <button type="button" className="back-button" onClick={() => setStep(step - 1)}>Atrás</button>}
          <button type="button" className="next-button" disabled={submitting} onClick={() => (step < 3 ? goNext() : submit())}>{submitting ? 'Publicando...' : step < 3 ? 'Continuar' : 'Publicar mercado'}<ArrowRight size={18} /></button>
        </div>
      </div>
    </div>
  )
}
