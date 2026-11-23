import { Route, Routes } from 'react-router-dom'
import Header from './components/Header'
import { DesktopNav, BottomNav } from './components/SideNav'
import BetSlip from './components/BetSlip'
import Feed from './pages/Feed'
import MarketDetail from './pages/MarketDetail'
import Auth from './pages/Auth'
import CreateMarket from './pages/CreateMarket'
import Portfolio from './pages/Portfolio'
import Leaderboard from './pages/Leaderboard'

export default function App() {
  return (
    <div className="app-shell">
      <Header />
      <DesktopNav />
      <main className="main-content" id="main-content">
        <Routes>
          <Route path="/" element={<Feed />} />
          <Route path="/auth" element={<Auth />} />
          <Route path="/market/:id" element={<MarketDetail />} />
          <Route path="/create" element={<CreateMarket />} />
          <Route path="/portfolio" element={<Portfolio />} />
          <Route path="/leaderboard" element={<Leaderboard />} />
        </Routes>
      </main>
      <BottomNav />
      <BetSlip />
    </div>
  )
}
