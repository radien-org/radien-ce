package io.radien.ms.usermanagement.service;

import io.radien.api.model.user.SystemUser;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakFactory {
    public static UserRepresentation convertToUserRepresentation(SystemUser user){
        UserRepresentation result = new UserRepresentation();
        result.setUsername(user.getLogon());
        result.setEmail(user.getUserEmail());
        result.setId(user.getSub());
        result.setFirstName(user.getFirstname());
        result.setLastName(user.getLastname());
        result.setEnabled(user.isEnabled());
        result.setEmailVerified(true);
        return result;
    }
}
