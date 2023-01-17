/** @type {import('tailwindcss').Config} */
module.exports = {
  presets: [ require('@cloudogu/ces-theme-tailwind/tailwind.presets.cjs') ],
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
    './node_modules/@cloudogu/ces-theme-react/src/**/*.{js,ts,jsx,tsx}'
  ],
};
