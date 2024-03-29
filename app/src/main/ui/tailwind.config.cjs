/** @type {import('tailwindcss').Config} */
module.exports = {
    presets: [require('@cloudogu/deprecated/ces-theme-tailwind/tailwind.presets.cjs'), require('@cloudogu/ces-theme-tailwind/tailwind.presets.cjs')],
    content: [
        './index.html',
        './src/**/*.{js,ts,jsx,tsx}',
        './node_modules/@cloudogu/deprecated/ces-theme-tailwind/build/**/*.{js,ts,jsx,tsx,mjs}',
        './node_modules/@cloudogu/ces-theme-tailwind/target/**/*.{js,ts,jsx,tsx,mjs}',
    ],
};
