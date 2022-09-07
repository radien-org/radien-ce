import keycloakConfig from "./keycloak.config";

keycloakConfig.url = 'https://idp-www.protec.help/auth';

export const environment = {
  production: true,
  keycloak: keycloakConfig
};
