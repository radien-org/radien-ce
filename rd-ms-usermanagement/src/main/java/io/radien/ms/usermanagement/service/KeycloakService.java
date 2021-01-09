package io.radien.ms.usermanagement.service;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.config.KeycloakConfigs;
import io.radien.ms.usermanagement.config.KeycloakEmailActions;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;


@Stateless
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);
    @Inject
    private ConfigService configService;

    private Keycloak getKeycloak(){
        return KeycloakBuilder.builder() //
                .serverUrl(configService.getProperty(KeycloakConfigs.ADMIN_URL)) //
                .realm(configService.getProperty(KeycloakConfigs.ADMIN_REALM)) //
                .grantType(OAuth2Constants.PASSWORD) //
                .clientId(configService.getProperty(KeycloakConfigs.ADMIN_CLIENT_ID)) //
                //.clientSecret(clientSecret) //
                .username(configService.getProperty(KeycloakConfigs.ADMIN_USER)) //
                .password(configService.getProperty(KeycloakConfigs.ADMIN_PASSWORD)) //
                .build();
    }

    private UsersResource getUsersResource(){
        Keycloak keycloak = getKeycloak();
        // Get realm
        RealmResource realmResource = keycloak.realm(configService.getProperty(KeycloakConfigs.APP_REALM));
        return realmResource.users();
    }

    public Optional<String> createUser(SystemUser user){
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(user);

        UsersResource userResource = getUsersResource();
        String userId;
        // Create user (requires manage-users role)
        try(Response response = userResource.create(userRepresentation)){
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                userResource.get(userId).executeActionsEmail(Collections.singletonList(KeycloakEmailActions.UPDATE_PASSWORD.propKey()));
            } else {
                //TODO: ERROR HANDLING
                String msg = response.readEntity(String.class);
                log.error(msg);
                return Optional.empty();
            }
        }

        return Optional.of(userId);
    }

    public boolean sendTOTPEmail(SystemUser user){
        UsersResource userResource = getUsersResource();
        userResource.get(user.getSub()).executeActionsEmail(Collections.singletonList(KeycloakEmailActions.TOTP.propKey()));
        //TODO: ERROR HANDLING
        return true;
    }
}
