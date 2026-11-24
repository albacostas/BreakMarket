import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { Auth0Provider } from '@auth0/auth0-react'
import App from './App'
import { AuthProvider } from './context/AuthContext'
import { BetSlipProvider } from './context/BetSlipContext'
import './styles.css'

const auth0Domain = import.meta.env.VITE_AUTH0_DOMAIN
const auth0ClientId = import.meta.env.VITE_AUTH0_CLIENT_ID
const auth0Audience = import.meta.env.VITE_AUTH0_API_AUDIENCE

function Root() {
  if (!auth0Domain || !auth0ClientId || !auth0Audience) {
    return <p role="alert">Configure VITE_AUTH0_DOMAIN, VITE_AUTH0_CLIENT_ID and VITE_AUTH0_API_AUDIENCE to enable login.</p>
  }

  return (
    <Auth0Provider
      domain={auth0Domain}
      clientId={auth0ClientId}
      cacheLocation="memory"
      authorizationParams={{
        redirect_uri: window.location.origin,
        audience: auth0Audience,
        scope: 'openid profile email',
      }}
    >
      <BrowserRouter>
        <AuthProvider>
          <BetSlipProvider>
            <App />
          </BetSlipProvider>
        </AuthProvider>
      </BrowserRouter>
    </Auth0Provider>
  )
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Root />
  </React.StrictMode>,
)
