/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
      colors: {
        primary: {
          DEFAULT: '#FD5C20',
          50: '#FFF1EA',
          100: '#FFE1D0',
          500: '#FD5C20',
          600: '#E34E15',
          700: '#C43F0F',
        },
        navy: {
          DEFAULT: '#14213D',
          900: '#0E1730',
        },
        peach: {
          DEFAULT: '#F3D5C4',
          light: '#FBEAE0',
        },
        accent: {
          purple: '#B064F7',
          green: '#2FB573',
          blue: '#2F8FF5',
        },
        surface: '#F3F3F3',
      },
      boxShadow: {
        card: '0 2px 10px 0 rgb(20 33 61 / 0.06)',
      },
      borderRadius: {
        xl2: '1.25rem',
      },
    },
  },
  plugins: [],
}
