/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        tcm: {
          primary: '#047857',   // 竹青
          dark: '#064e3b',
          light: '#ecfdf5',
          amber: '#d97706',    // 橘络橙
          dan: '#e11d48',      // 丹砂赤
          bg: '#fafaf9',       // 宣纸暖白
        }
      }
    },
  },
  plugins: [],
}
