import keycloakConfig from "./keycloak.config";

keycloakConfig.url = 'https://idp-test.protec.help/auth'

export const environment = {
  production: false,
  keycloak: keycloakConfig
};