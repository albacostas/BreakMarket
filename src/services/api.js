import axios from 'axios'

let accessTokenProvider

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 2500,
})

export const setAccessTokenProvider = (provider) => {
  accessTokenProvider = provider
}

api.interceptors.request.use(async (config) => {
  if (accessTokenProvider) {
    const token = await accessTokenProvider()
    if (token) config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const avatars = {
  lucia: 'https://i.pravatar.cc/160?img=47',
  marcos: 'https://i.pravatar.cc/160?img=11',
  alex: 'https://i.pravatar.cc/160?img=68',
  sara: 'https://i.pravatar.cc/160?img=32',
  hugo: 'https://i.pravatar.cc/160?img=33',
  valeria: 'https://i.pravatar.cc/160?img=45',
}

export const mockMarkets = [
  {
    id: 'lucia-marcos', type: 'YES_NO', typeLabel: 'Sí / No', status: 'OPEN',
    title: '¿Romperán Lucía y Marcos antes del 31/12?',
    personA: { name: 'Lucía', avatar: avatars.lucia }, personB: { name: 'Marcos', avatar: avatars.marcos },
    probability: 68, yesOdds: 1.45, noOdds: 2.80, volume: 18420, closing: '31 Dic, 23:59',
    market_type: 'binary', trending: true,
    options: [
      { id: 'yes', label: 'Sí, romperán', probability: 68, odds: 1.45, color: '#FF2A6D' },
      { id: 'no', label: 'No romperán', probability: 32, odds: 2.80, color: '#9D4EDD' },
    ],
    history: [52,54,55,57,56,60,58,63,65,64,67,68].map((probability, i) => ({ time: `${i + 8}:00`, probability })),
  },
  {
    id: 'sara-hugo', type: 'PERSON', typeLabel: 'Por persona', status: 'OPEN',
    title: '¿Quién es el tercero de Sara y Hugo?',
    personA: { name: 'Sara', avatar: avatars.sara }, personB: { name: 'Hugo', avatar: avatars.hugo },
    probability: 54, yesOdds: 1.85, noOdds: 2.10, volume: 9260, closing: '24 Dic, 20:00',
    market_type: 'multiple', trending: true,
    options: [
      { id: 'ex', label: 'Su ex', probability: 54, odds: 2.10, avatar: avatars.alex },
      { id: 'classmate', label: 'Compañero de clase', probability: 29, odds: 3.40, avatar: avatars.valeria },
      { id: 'gym', label: 'Del gimnasio', probability: 17, odds: 5.25, avatar: avatars.marcos },
    ],
    history: [38,41,39,44,47,43,49,51,50,52,55,54].map((probability, i) => ({ time: `${i + 8}:00`, probability })),
  },
  {
    id: 'alex-valeria', type: 'YES_NO', typeLabel: 'Sí / No', status: 'OPEN',
    title: '¿Alex y Valeria se liarán esta noche?',
    personA: { name: 'Alex', avatar: avatars.alex }, personB: { name: 'Valeria', avatar: avatars.valeria },
    probability: 72, yesOdds: 1.38, noOdds: 3.10, volume: 6730, closing: 'Hoy, 02:00',
    market_type: 'binary', trending: true,
    options: [
      { id: 'yes', label: 'Sí se lían', probability: 72, odds: 1.38, color: '#FF2A6D' },
      { id: 'no', label: 'No se lían', probability: 28, odds: 3.10, color: '#9D4EDD' },
    ],
    history: [62,65,64,66,68,67,69,71,70,72,74,72].map((probability, i) => ({ time: `${i + 8}:00`, probability })),
  },
  {
    id: 'paula-dani', type: 'DATE', typeLabel: 'Por fecha', status: 'OPEN',
    title: '¿Cuándo anunciará su ruptura Paula?',
    personA: { name: 'Paula', avatar: avatars.valeria }, personB: { name: 'Dani', avatar: avatars.hugo },
    probability: 46, yesOdds: 2.20, noOdds: 1.90, volume: 4150, closing: '30 Ene, 00:00',
    market_type: 'multiple', trending: false,
    options: [
      { id: 'week', label: 'Esta semana', probability: 46, odds: 2.20 },
      { id: 'month', label: 'En 2–4 semanas', probability: 34, odds: 2.95 },
      { id: 'later', label: 'Más tarde', probability: 20, odds: 4.80 },
    ],
    history: [35,36,38,40,39,42,44,43,45,46,44,46].map((probability, i) => ({ time: `${i + 8}:00`, probability })),
  },
]

export const mockComments = [
  { id: 1, author: 'mariax', avatar: 'https://i.pravatar.cc/80?img=25', text: 'Los vi cenar separados ayer. Alguien tiene fotos?', upvotes: 42, time: 'hace 8 min' },
  { id: 2, author: 'ojosdetiktok', avatar: 'https://i.pravatar.cc/80?img=5', text: 'Dejó de seguir a Marcos a las 00:12. Esto pinta mal.', upvotes: 28, time: 'hace 21 min' },
  { id: 3, author: 'patataVIP', avatar: 'https://i.pravatar.cc/80?img=14', text: 'Yo digo que siguen juntos por pura inercia.', upvotes: 16, time: 'hace 1 h' },
]

export const mockActivity = [
  { id: 1, user: 'RosaOscura', amount: 100, option: 'Sí', odds: 1.45, time: 'hace 1 min' },
  { id: 2, user: 'exo4ever', amount: 25, option: 'No', odds: 2.80, time: 'hace 4 min' },
  { id: 3, user: 'cristalero', amount: 75, option: 'Sí', odds: 1.48, time: 'hace 7 min' },
]

const fallback = (data) => Promise.resolve({ data })

const useMocks = import.meta.env.VITE_USE_MOCKS === 'true'

const avatarFor = (name = '') => `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=2E213B&color=F8F9FA&bold=true`

const formatClosingDate = (value) => {
  if (!value) return 'Sin fecha de cierre'
  return new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

const betTypeLabels = {
  YES_NO: 'Sí / No',
  PERSON: 'Por persona',
  DATE: 'Por fecha',
}

const normalizeMarket = (bet, couple, options = []) => {
  const members = couple?.members || []
  const personA = members[0] || { fullName: 'Persona A' }
  const personB = members[1] || { fullName: 'Persona B' }
  const pools = options.map(option => Number(option.totalPool || 0))
  const totalPool = pools.reduce((sum, pool) => sum + pool, 0)
  const probabilities = options.map((option, index) => {
    if (totalPool > 0) return Math.round((pools[index] / totalPool) * 100)
    const implied = 1 / Number(option.currentRate || 1)
    const impliedTotal = options.reduce((sum, item) => sum + (1 / Number(item.currentRate || 1)), 0)
    return Math.round((implied / impliedTotal) * 100)
  })
  const normalizedOptions = options.map((option, index) => ({
    id: option.uuid,
    label: option.description,
    probability: probabilities[index] || 0,
    odds: Number(option.currentRate),
    totalPool: option.totalPool,
    avatar: avatarFor(option.description),
  }))
  const firstOption = normalizedOptions[0]
  const secondOption = normalizedOptions[1]
  return {
    id: bet.uuid,
    type: bet.type,
    typeLabel: betTypeLabels[bet.type] || bet.type,
    status: bet.state,
    title: bet.description,
    personA: { name: personA.fullName, avatar: avatarFor(personA.fullName) },
    personB: { name: personB.fullName, avatar: avatarFor(personB.fullName) },
    probability: probabilities[0] || 0,
    yesOdds: firstOption?.odds || 0,
    noOdds: secondOption?.odds || 0,
    volume: totalPool,
    closing: formatClosingDate(bet.endDate),
    market_type: bet.type === 'YES_NO' ? 'binary' : 'multiple',
    trending: false,
    options: normalizedOptions,
    history: [],
    backend: { bet, couple, options },
  }
}

const getBackendMarkets = async (params = {}) => {
  const { data: bets } = await api.get('/bets', {
    params: {
      state: 'ACTIVE',
      description: params.search || undefined,
      type: params.type && params.type !== 'ALL' ? params.type : undefined,
    },
  })
  return Promise.all(bets.map(async (bet) => {
    const [{ data: couple }, { data: options }] = await Promise.all([
      api.get(`/couples/${bet.coupleUuid}`),
      api.get(`/bets/${bet.uuid}/options`),
    ])
    return normalizeMarket(bet, couple, options)
  }))
}

export const marketService = {
  list: async (params = {}) => {
    try { return await getBackendMarkets(params) }
    catch (error) {
      if (!useMocks) throw error
      let data = mockMarkets
      if (params.type && params.type !== 'ALL') data = data.filter(m => m.type === params.type)
      if (params.search) data = data.filter(m => m.title.toLowerCase().includes(params.search.toLowerCase()))
      return data
    }
  },
  get: async (id) => {
    try {
      const { data: bet } = await api.get(`/bets/${id}`)
      const [{ data: couple }, { data: options }] = await Promise.all([
        api.get(`/couples/${bet.coupleUuid}`),
        api.get(`/bets/${id}/options`),
      ])
      return normalizeMarket(bet, couple, options)
    } catch (error) {
      if (!useMocks) throw error
      return mockMarkets.find(m => m.id === id) || mockMarkets[0]
    }
  },
  create: async (payload) => {
    const { data } = await api.post('/bets', {
      creatorUuid: payload.creatorUuid,
      coupleUuid: payload.coupleUuid,
      type: payload.type,
      description: payload.title,
      endDate: payload.closes_at ? new Date(payload.closes_at).toISOString() : undefined,
      options: payload.options,
    })
    return data
  },
}

export const evidenceService = {
  listForBet: async (id) => {
    try {
      const { data } = await api.get(`/bets/${id}/evidence`)
      return data.map(item => ({
        id: item.uuid,
        author: item.authorUuid,
        avatar: avatarFor(item.authorUuid),
        text: item.comment,
        state: item.state,
        time: formatClosingDate(item.submittedDate),
      }))
    } catch (error) {
      if (useMocks) return mockComments.map(item => ({ ...item, state: 'PENDING' }))
      throw error
    }
  },
  submit: async (id, text, authorUUID) => {
    const { data } = await api.post(`/bets/${id}/evidence`, { authorUUID, comment: text })
    return {
      id: data.uuid,
      author: data.authorUuid,
      avatar: avatarFor(data.authorUuid),
      text: data.comment,
      state: data.state,
      time: formatClosingDate(data.submittedDate),
    }
  },
  pending: async () => (await api.get('/evidence', { params: { state: 'PENDING' } })).data,
  review: async (uuid, reviewerUuid, state) => (
    await api.patch(`/evidence/${uuid}/review`, null, { params: { reviewerUuid, state } })
  ).data,
}

export const ticketService = {
  listForBet: async (id) => {
    const { data } = await api.get(`/bets/${id}/tickets`)
    return data.map(item => ({
      id: item.uuid,
      user: item.userUuid,
      amount: item.quantity,
      option: item.optionUuid,
      rate: Number(item.rate),
      createdDate: item.createdDate,
      time: formatClosingDate(item.createdDate),
    }))
  },
}

export const betService = {
  place: async (payload) => {
    const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i
    if (!uuidPattern.test(payload.market_id || '') || !uuidPattern.test(payload.option_id || '') || !uuidPattern.test(payload.user_id || '')) {
      throw new Error('Tu sesión contiene un usuario antiguo. Cierra sesión y entra de nuevo con admin@breakmarket.com.')
    }
    if (!Number.isInteger(payload.amount) || payload.amount < 1) {
      throw new Error('La cantidad debe ser un número entero mayor que cero.')
    }
    const { data } = await api.post(`/bets/${payload.market_id}/tickets`, {
      userUUID: payload.user_id,
      optionUUID: payload.option_id,
      quantity: payload.amount,
    })
    return data
  },
  portfolio: async (status, userId) => {
    if (!userId) return []
    const { data: bets } = await api.get('/bets')
    const tickets = (await Promise.all(bets.map(async (bet) => {
      const [{ data }, { data: options }] = await Promise.all([
        api.get(`/bets/${bet.uuid}/tickets`, { params: { authorID: userId } }),
        api.get(`/bets/${bet.uuid}/options`),
      ])
      return data.map(ticket => {
        const option = options.find(item => item.uuid === ticket.optionUuid)
        return {
          id: ticket.uuid,
          market: bet.description,
          option: option?.description || ticket.optionUuid,
          amount: ticket.quantity,
          odds: ticket.rate,
          potential: Number((ticket.quantity * ticket.rate).toFixed(2)),
          status: ticket.state === 'PENDING' ? 'ACTIVE' : 'RESOLVED',
        }
      })
    }))).flat()
    return tickets.filter((ticket) => status === 'resolved'
      ? ticket.status === 'RESOLVED'
      : ticket.status === 'ACTIVE')
  },
  resolveTicket: async (ticketUuid, state) => {
    const { data } = await api.patch(`/bets/tickets/${ticketUuid}/resolve`, null, { params: { state } })
    return data
  },
}

export const authService = {
  me: async () => normalizeUser((await api.get('/auth/me')).data),
}

const normalizeUser = (user) => ({
  ...user,
  id: user.uuid,
  balance: Number(user.coins || 0),
  avatar_url: avatarFor(user.username || user.fullName),
})

export const userService = {
  list: async () => {
    const { data } = await api.get('/users')
    return data.map(normalizeUser)
  },
  me: async () => {
    return authService.me()
  },
  leaderboard: async () => {
    const users = await userService.list()
    return users.sort((a, b) => b.balance - a.balance).map((user, index) => ({
      rank: index + 1,
      username: user.username,
      avatar: user.avatar_url,
      balance: user.balance,
      roi: '—',
      accuracy: '—',
    }))
  },
}

export const personService = {
  create: async (fullName) => (await api.post('/people', { fullName })).data,
  findOrCreate: async (fullName) => {
    const { data: matches } = await api.get('/people', { params: { fullName } })
    const existing = matches.find((person) => person.fullName.trim().toLocaleLowerCase() === fullName.trim().toLocaleLowerCase())
    return existing || personService.create(fullName)
  },
}

export const coupleService = {
  list: async () => (await api.get('/couples')).data,
  create: async (members) => (await api.post('/couples', { status: 'RELATIONSHIP', startDate: new Date().toISOString(), members })).data,
}
