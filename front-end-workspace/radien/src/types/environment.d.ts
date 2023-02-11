namespace NodeJS {
    interface ProcessEnv extends NodeJS.ProcessEnv {
        NEXTAUTH_URL: string;
        KEYCLOAK_ID: string;
        KEYCLOAK_SECRET: string;
        KEYCLOAK_ISSUER: string;
    }
}
