import { KeycloakConfig } from "keycloak-js";

const keycloakConfig: KeycloakConfig = {
  url: 'https://idp-test.protec.help/auth',
  realm: 'protec',
  clientId: 'protec-fe'
}; 

export default keycloakConfig;
