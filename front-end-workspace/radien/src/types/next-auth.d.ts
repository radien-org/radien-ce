import { Session } from "next-auth";
import {ActiveTenant, User} from "radien";

declare module "next-auth" {
    interface Session {
        accessToken?: string,
        radienUser: User,
    }
}

declare module "next-auth/jwt" {
    interface JWT {
        accessToken?: string
    }
}