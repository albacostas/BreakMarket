import { useState } from 'react'
import { Spade, ArrowRight } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Auth() {
  const [tab, setTab] = useState('login')
  const [form, setForm] = useState({ username: '', email: '', password: '', avatar_url: '' })
  const [error, setError] = useState('')
  const { login, register, loading } = useAuth()
  const navigate = useNavigate()

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    try {
      if (tab === 'login') await login({ email: form.email, password: form.password })
      else await register(form)
      navigate('/')
    } catch { setError('No pudimos iniciar sesión. Revisa tus datos.') }
  }

  const set = (key) => (event) => setForm({ ...form, [key]: event.target.value })
  return (
    <div className="auth-page">
      <form className="auth-card casino-panel" onSubmit={submit}>
        <div className="auth-logo"><Spade size={28} fill="currentColor" /></div>
        <h1 className="gold-text">Acceso VIP</h1>
        <p>Entra a la sala, apuesta fichas y sigue el salseo en directo.</p>
        <div className="auth-tabs" role="tablist" aria-label="Modo de acceso">
          <button type="button" role="tab" aria-selected={tab === 'login'} className={tab === 'login' ? 'active' : ''} onClick={() => setTab('login')}>Entrar</button>
          <button type="button" role="tab" aria-selected={tab === 'register'} className={tab === 'register' ? 'active' : ''} onClick={() => setTab('register')}>Unirse al club</button>
        </div>
        {tab === 'register' && <label>Nombre de usuario<input value={form.username} onChange={set('username')} required placeholder="Tu nombre público" autoComplete="username" /></label>}
        <label>Email<input type="email" value={form.email} onChange={set('email')} required placeholder="tu@email.com" autoComplete="email" /></label>
        <label>Contraseña<input type="password" value={form.password} onChange={set('password')} required minLength={4} placeholder="••••••••" autoComplete={tab === 'login' ? 'current-password' : 'new-password'} /></label>
        {tab === 'register' && <label>Avatar <span>(opcional)</span><input value={form.avatar_url} onChange={set('avatar_url')} placeholder="https://..." autoComplete="url" /></label>}
        {error && <div className="form-error" role="alert">{error}</div>}
        <button className="auth-submit" disabled={loading}>{loading ? 'Entrando...' : tab === 'login' ? 'Entrar al club' : 'Recibir 1.000 💔'}<ArrowRight size={18} /></button>
        <small>Al continuar aceptas las reglas del cotilleo.</small>
      </form>
    </div>
  )
}
