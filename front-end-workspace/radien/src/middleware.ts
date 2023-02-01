import { withAuth } from "next-auth/middleware"

export default withAuth({pages: {signIn: "/"}})

export const config = { matcher: ["/system/:path*", "/api/:path*", "/user/:path*"] }