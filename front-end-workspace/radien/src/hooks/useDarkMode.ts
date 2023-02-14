import { useEffect, useState } from "react";

export default function useDarkMode() {
    const [theme, setTheme] = useState(typeof window !== "undefined" && localStorage.theme ? localStorage.theme : "light");
    const colorTheme = theme == undefined ? "light" : theme === "light" ? "awsui-dark-mode" : "light";

    useEffect(() => {
        const root = window.document.documentElement;

        root.classList.remove(colorTheme);
        root.classList.add(theme);

        console.log(theme, colorTheme);

        if (typeof window !== "undefined") {
            localStorage.setItem("theme", theme);
        }
    }, [theme, colorTheme]);

    return { theme, setTheme };
}
