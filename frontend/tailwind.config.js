/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}', './public/index.html'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Nanum Barun Gothic', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI'],
      },
    },
  },
  plugins: [],
};
