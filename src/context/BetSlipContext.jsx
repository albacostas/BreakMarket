import { createContext, useContext, useState } from 'react'

const BetSlipContext = createContext(null)

export function BetSlipProvider({ children }) {
  const [selection, setSelection] = useState(null)
  const [amount, setAmount] = useState(50)

  const openSlip = (market, option) => {
    setSelection({ market, option })
    setAmount(50)
  }

  const closeSlip = () => setSelection(null)

  return (
    <BetSlipContext.Provider value={{ selection, amount, setAmount, openSlip, closeSlip }}>
      {children}
    </BetSlipContext.Provider>
  )
}

export const useBetSlip = () => useContext(BetSlipContext)
