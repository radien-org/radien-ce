import NextAuth, {AuthOptions, Awaitable, Session} from "next-auth"
import KeycloakProvider from "next-auth/providers/keycloak"
import axios, {AxiosError, AxiosResponse} from "axios";
export const authOptions: AuthOptions = {
    providers: [
        KeycloakProvider({
            clientId: process.env.KEYCLOAK_ID,
            clientSecret: process.env.KEYCLOAK_SECRET,
            issuer: process.env.KEYCLOAK_ISSUER,
        }),
    ],
    callbacks: {
        async session({ session, token, user }) {
            session.accessToken = token.accessToken;
            const pathUser = `${process.env.RADIEN_USER_URL}/user/session`;
            const resultUser: AxiosResponse = await axios
                .get(pathUser, {
                    headers: {
                        "Authorization": `Bearer ${session.accessToken}`
                    }
                });
            session.radienUser = resultUser.data;
            return session;
        },
        async jwt({ token, user, account, profile, isNewUser }) {
            if (user) {
                token.id = user.id;
            }
            if (account) {
                token.accessToken = account.access_token;
            }
            return token;
        }
    }
}

export default NextAuth(authOptions)