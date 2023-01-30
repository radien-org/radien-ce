import { Session } from "next-auth";

declare module "next-auth" {
    interface Session {
        accessToken?: string
    }

    interface Profile {
        given_name: string,
        family_name: string,
        preferred_username: string,
    }
}

declare module "next-auth/jwt" {
    interface JWT {
        accessToken?: string,
        firstName: string,
        lastName: string,
        userName: string

    }
}