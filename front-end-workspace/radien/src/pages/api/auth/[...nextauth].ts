import NextAuth, { AuthOptions } from "next-auth";
import KeycloakProvider from "next-auth/providers/keycloak";
import axios from "axios";
import { User } from "radien";
export const authOptions: AuthOptions = {
    providers: [
        KeycloakProvider({
            clientId: process.env.KEYCLOAK_ID,
            clientSecret: process.env.KEYCLOAK_SECRET,
            issuer: process.env.KEYCLOAK_ISSUER,
            checks: "state",
            authorization: { params: { scope: "openid profile" } },
        }),
    ],
    callbacks: {
        async session({ session, token, user }) {
            session.accessToken = token.accessToken;
            const userBySub = await axios
                .get(`${process.env.RADIEN_USER_URL}/user/sub/${token.sub}`, {
                    headers: {
                        Authorization: `Bearer ${session.accessToken}`,
                    },
                })
                .catch((e) => undefined);
            if (!userBySub) {
                const newUser: User = {
                    sub: token.sub!,
                    firstname: token.firstName,
                    lastname: token.lastName,
                    userEmail: token.email!,
                    logon: token.userName,
                    mobileNumber: "",
                    enabled: true,
                    delegatedCreation: true,
                    processingLocked: false,
                    terminationDate: new Date(),
                    createDate: new Date(),
                    lastUpdate: new Date(),
                };

                await axios.post(`${process.env.RADIEN_USER_URL}/user`, newUser, {
                    headers: {
                        Authorization: `Bearer ${session.accessToken}`,
                    },
                });
            }
            return session;
        },
        async jwt({ token, user, account, profile, isNewUser }) {
            if (user) {
                token.id = user.id;
            }
            if (account) {
                token.accessToken = account.access_token;
            }
            if (profile) {
                token.firstName = profile.given_name;
                token.lastName = profile.family_name;
                token.userName = profile.preferred_username;
            }

            return token;
        },
    },
};

export default NextAuth(authOptions);
