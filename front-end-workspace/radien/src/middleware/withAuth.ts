import { MiddlewareFactory } from "@/middleware/types";
import { NextFetchEvent, NextMiddleware, NextRequest, NextResponse } from "next/server";
import { getToken } from "next-auth/jwt";

const protectedPaths = [
    "permissionManagement",
    "roleManagement",
    "tenantManagement",
    "userManagement",
    "createUser",
    "userProfile",
    "createPermission",
    "createRole",
];

export const withAuth: MiddlewareFactory = (next: NextMiddleware) => {
    return async (req: NextRequest, _next: NextFetchEvent) => {
        if (!protectedPaths.find((p) => req.nextUrl.pathname.includes(p))) {
            return NextResponse.next();
        }

        const session = await getToken({ req });
        if (!session) {
            const url = req.nextUrl.clone();
            url.pathname = "/";
            url.search = "";
            return NextResponse.redirect(req.nextUrl.origin, { status: 307 });
        }
    };
};
