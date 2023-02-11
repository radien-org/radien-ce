import { NextRequest, NextResponse } from "next/server";

export async function middleware(req: NextRequest) {
    if (req.nextUrl.pathname.startsWith("/_next") || req.nextUrl.pathname.includes("/api/")) {
        return;
    }

    if (req.nextUrl.locale === "default") {
        const locale = req.cookies.get("NEXT_LOCALE")?.value || "en";

        const response = NextResponse.redirect(new URL(`/${locale}${req.nextUrl.pathname}${req.nextUrl.search}`, req.url));
        response.cookies.set("NEXT_LOCALE", locale);
        return response;
    } else {
        const locale = req.nextUrl.locale;
        const localeCookie = req.cookies.get("NEXT_LOCALE")?.value || "en";

        const response = NextResponse.redirect(new URL(`/${locale}${req.nextUrl.pathname}${req.nextUrl.search}`, req.url));
        if (locale !== localeCookie) {
            response.cookies.set("NEXT_LOCALE", locale);
            return response;
        }
    }
}
