import { useState } from 'react'
import { ArrowLeft, ArrowRight, Plus, Trash2, Heart } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { coupleService, marketService, personService } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function CreateMarket() {
  const [step, setStep] = useState(1)
  const [type, setType] = useState('binary')
  const [form, setForm] = useState({ title: '', category: 'rupturas', person_a: '', person_b: '', closes_at: '' })
  const [error, setError] = useState('')
  const { user } = useAuth()
  const [options, setOptions] = useState(['Con su ex', 'Compañero/a'])
  const navigate = useNavigate()

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value })
  const submit = async () => {
    try {
      if (!user?.id) throw new Error('Debes iniciar sesión antes de crear un mercado.')
      const [personA, personB] = await Promise.all([personService.create(form.person_a), personService.create(form.person_b)])
      const couple = await coupleService.create([personA.uuid, personB.uuid])
      await marketService.create({
        title: form.title,
        market_type: type,
        options: type === 'date' ? options.map((option) => option) : type === 'binary' ? ['Sí', 'No'] : options,
        closes_at: form.closes_at,
        creatorUuid: user.id,
        coupleUuid: couple.uuid,
      })
      navigate('/')
    } catch (submitError) {
      setError(submitError.message || 'No se pudo crear el mercado.')
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
            {[['binary', 'Sí / No', 'Sí, no, o nada', '⚖️'], ['person', 'Tercero en discordia', '¿Con quién se liará?', '🐍'], ['date', 'Fecha de ruptura', '¿Cuándo ocurrirá?', '⏳']].map(([value, title, desc, icon]) => (
              <button key={value} type="button" className={type === value ? 'active' : ''} onClick={() => { setType(value); if (value === 'date') setOptions(['2026-12-01', '2026-12-31']); else if (value === 'person') setOptions(['Con su ex', 'Compañero/a']) }}>
                <span aria-hidden="true">{icon}</span><strong>{title}</strong><small>{desc}</small>
              </button>
            ))}
          </div>
        </>}
        {step === 2 && <>
          <div className="create-title"><h1>El reparto de personajes</h1><p>Dale contexto al cotilleo antes de abrir apuestas.</p></div>
          <div className="create-form">
            <label>Título / Pregunta<input value={form.title} onChange={set('title')} placeholder="¿Romperán antes de fin de año?" /></label>
            <label>Categoría<select value={form.category} onChange={set('category')}><option value="rupturas">💔 Rupturas</option><option value="cuernos">🐍 Cuernos</option><option value="lios">💋 Líos / Besos</option><option value="fechas">⏳ Fechas límite</option></select></label>
            <div className="person-fields">
              <label>Persona A<input value={form.person_a} onChange={set('person_a')} placeholder="Nombre" /></label>
              <div aria-hidden="true"><Heart size={17} fill="currentColor" /></div>
              <label>Persona B<input value={form.person_b} onChange={set('person_b')} placeholder="Nombre" /></label>
            </div>
          </div>
        </>}
        {step === 3 && <>
          <div className="create-title"><h1>Últimos detalles</h1><p>Configura las opciones y la hora de cierre de la mesa.</p></div>
          <div className="create-form">
            {type !== 'binary' && <div className="dynamic-options"><label>Opciones</label>{options.map((option, i) => <div key={i}><input value={option} onChange={(e) => setOptions(options.map((o, j) => (j === i ? e.target.value : o)))} /><button type="button" aria-label={`Eliminar opción ${i + 1}`} onClick={() => setOptions(options.filter((_, j) => j !== i))}><Trash2 size={16} /></button></div>)}<button type="button" className="add-option" onClick={() => setOptions([...options, 'Nueva opción'])}><Plus size={16} /> Añadir opción</button></div>}
            <label>Fecha y hora de cierre<input type="datetime-local" value={form.closes_at} onChange={set('closes_at')} /></label>
          </div>
        </>}
        {error && <div className="form-error" role="alert">{error}</div>}
        <div className="create-actions">
          {step > 1 && <button type="button" className="back-button" onClick={() => setStep(step - 1)}>Atrás</button>}
          <button type="button" className="next-button" onClick={() => (step < 3 ? setStep(step + 1) : submit())}>{step < 3 ? 'Continuar' : 'Publicar mercado'}<ArrowRight size={18} /></button>
        </div>
      </div>
    </div>
  )
}
