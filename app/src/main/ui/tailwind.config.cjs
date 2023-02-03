/** @type {import('tailwindcss').Config} */
module.exports = {
  presets: [ require('@cloudogu/ces-theme-tailwind/tailwind.presets.cjs') ],
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
    './node_modules/@cloudogu/ces-theme-tailwind/build/**/*.{js,ts,jsx,tsx,mjs}'
  ],
};
