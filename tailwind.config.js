/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#0F0A14',
        surface: '#1C1424',
        line: '#2E213B',
        pink: '#FF2A6D',
        purple: '#9D4EDD',
        red: '#FF4D4D',
        gold: '#FFD166',
        muted: '#A098AB',
      },
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
    },
  },
  plugins: [],
}
