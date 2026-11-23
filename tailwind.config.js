/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        velvet: '#1A050A',
        velvetDark: '#0F0205',
        felt: '#0B281E',
        feltLight: '#123D2E',
        gold: '#D4AF37',
        goldLight: '#F6E27F',
        goldDark: '#8C6D1F',
        ivory: '#FAF6EE',
        crimson: '#FF2A55',
        ink: '#1A050A',
        surface: '#22070F',
        line: '#5A4112',
        pink: '#FF2A55',
        purple: '#D4AF37',
        red: '#FF4D4D',
        muted: '#C7B284',
      },
      fontFamily: {
        casino: ['"Abril Fatface"', 'serif'],
        editorial: ['"Playfair Display"', 'serif'],
        sans: ['"Outfit"', 'sans-serif'],
        led: ['"Share Tech Mono"', 'monospace'],
        mono: ['"Share Tech Mono"', 'monospace'],
      },
      boxShadow: {
        casino: '0 12px 35px rgba(0,0,0,0.85)',
        gold: '0 0 18px rgba(212,175,55,0.35)',
      },
    },
  },
  plugins: [],
}
