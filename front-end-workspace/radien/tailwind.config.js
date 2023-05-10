/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{js,ts,jsx,tsx}"],
    theme: {
        extend: {
            colors: {
                container: {
                    light: "#faf7f7",
                    dark: "var(--color-grey-800-8gx340)",
                },
                text: {
                    light: "#00142b",
                    dark: "var(--color-grey-300-gjxrd6)",
                },
            },
        },
    },
    plugins: [],
};
