/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.ms.usermanagement.service;

import io.radien.api.model.user.SystemUser;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UserChangeCredentialException;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keycloak request services and actions
 *
 * @author Nuno Santana
 */
@Stateless
public class KeycloakService {
    protected static final Logger log = LoggerFactory.getLogger(KeycloakService.class);

    @Inject
    private KeycloakClientFactory keycloakClientFactory;

    /**
     * Request to the keycloak client to create given user
     * @param user to be created
     * @return the newlly created user subject
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public String createUser(SystemUser user) throws RemoteResourceException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(user);
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        userRepresentation.setEmailVerified(false);
        String sub= client.createUser(userRepresentation);
        try {
            client.sendUpdatePasswordEmail(sub);
        } catch (RemoteResourceException e){
            deleteUser(sub);
            throw e;
        }

        return sub;
    }

    /**
     * Method to request keycloak to delete specific user
     * @param sub of the user to be deleted
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void deleteUser(String sub) throws RemoteResourceException {
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        client.deleteUser(sub);
    }

    /**
     * Method to request keycloak to update a specific user information
     * @param newUser information to be update
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void updateUser(SystemUser newUser) throws RemoteResourceException {
        UserRepresentation userRepresentation = KeycloakFactory.convertToUserRepresentation(newUser);
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        client.updateUser(newUser.getSub(), userRepresentation);
    }

    /**
     * Method to request the keycloak client to send new updated password to the specific user
     * @param user to be reset password and sent email
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void sendUpdatePasswordEmail(SystemUser user) throws RemoteResourceException{
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        client.sendUpdatePasswordEmail(user.getSub());
    }

    /**
     * Method to request the keycloak client to send new updated email to verify for the specific user
     * @param email user attribute
     * @param sub user sub keycloak id attribute
     * @param emailVerify boolean flag
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void updateEmailAndExecuteActionEmailVerify(String email, String sub, boolean emailVerify) throws RemoteResourceException{
        UserRepresentation userRepresentation = KeycloakFactory.convertUpdateEmailToUserRepresentation(email, emailVerify);
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        client.updateEmailAndExecuteActionEmailVerify(sub, userRepresentation);
        if(emailVerify) {
            client.sendUpdatedEmailToVerify(sub);
        }
    }

    /**
     * Method to request the keycloak client to refresh access token by a given refresh token (after validation)
     * @param refreshToken to be validated if can refresh access token
     * @return the new access token
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public String refreshToken(String refreshToken) throws RemoteResourceException {
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        return client.refreshToken(refreshToken);
    }

    /**
     * Method to request keycloak to the subjectId from the user with the specified email
     * @param email of the user to fetch the subjectId
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public Optional<String> getSubFromEmail(String email) throws RemoteResourceException {
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        return client.getSubFromEmail(email);
    }


    /**
     * Performs the logic regarding changing password process.
     * First, Validates the combination of username and password. In case of success, call api method to change
     * the user password.
     * @param username user logon
     * @param subject user identifier from the perspective of KeyCloak
     * @param password user password
     * @param newPassword new password value to be defined
     * @throws UserChangeCredentialException thrown in case of invalid credentials
     * @throws RemoteResourceException thrown in case of any issue regarding communication with KeyCloak service
     */
    public void validateChangeCredentials(String username, String subject, String password, String newPassword) throws UserChangeCredentialException, RemoteResourceException {
        KeycloakClient client = keycloakClientFactory.getKeycloakClient();
        if (!client.validateCredentials(username, password)) {
            throw new UserChangeCredentialException(GenericErrorCodeMessage.ERROR_INVALID_CREDENTIALS.toString());
        }
        client.changePassword(subject, newPassword);
    }
}
