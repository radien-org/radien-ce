import { NextFetchEvent, NextMiddleware, NextRequest, NextResponse } from "next/server";
import { MiddlewareFactory } from "./types";
export const withLocale: MiddlewareFactory = (next: NextMiddleware) => {
    return async (req: NextRequest, _next: NextFetchEvent) => {
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
    };
};
