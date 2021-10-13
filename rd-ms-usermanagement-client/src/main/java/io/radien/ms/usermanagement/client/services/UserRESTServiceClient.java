/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.usermanagement.client.services;

import io.radien.exception.GenericErrorCodeMessage;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.radien.api.entity.Page;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.batch.BatchSummary;
import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
import io.radien.ms.usermanagement.client.util.UserModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.ListUserModelMapper;

import static java.util.stream.Collectors.toList;

/**
 * User management service client for Rest Service Client regarding user domain object
 *
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
 */
//@Stateless
//@Default
@RequestScoped
@RegisterProvider(UserResponseExceptionMapper.class)
public class UserRESTServiceClient extends AuthorizationChecker implements UserRESTServiceAccess {
    private static final long serialVersionUID = -8231324104338674760L;

    private static final Logger log = LoggerFactory.getLogger(UserRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private TokensPlaceHolder tokensPlaceHolder;

    /**
     * Gets all the users in the DB searching for the field Subject
     *
     * @param sub to be looked after
     * @return Optional List of Users
     * @throws SystemException in case it founds multiple users or if URL is malformed
     */
    public Optional<SystemUser> getUserBySub(String sub) throws SystemException {
        try {
            return getSystemUser(sub);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return getSystemUser(sub);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Retrieves an user by its respective Id
     * @param id user identifier
     * @return Optional containing user
     * @throws SystemException in case it founds multiple users or if URL is malformed
     */
    public Optional<SystemUser> getUserById(Long id) throws SystemException {
        try {
            return getSystemUser(id);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return getSystemUser(id);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets the requested users searching for a list of ids
     * @param ids to be searched
     * @return a list containing system users
     * @throws SystemException in case of token expiration or any issue on the application
     */
    @Override
    public List<? extends SystemUser> getUsersByIds(Collection<Long> ids) throws SystemException {
        try {
            return getSystemUsers(ids);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return getSystemUsers(ids);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException(expiredException);
            }
        }
    }

    /**
     * Retrieves an user by its respective logon
     * @param logon user logon
     * @return Optional containing user
     * @throws SystemException in case it founds multiple users or if URL is malformed
     */
    public Optional<SystemUser> getUserByLogon(String logon) throws SystemException {
        try {
            return getSystemUserByLogon(logon);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return getSystemUserByLogon(logon);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Method to retrieve a specific system user by a given user id
     * @param id to be fetched and found
     * @return a optional list and if filled with the required system user
     * @throws SystemException in case it founds multiple users or if URL is malformed
     * @throws TokenExpiredException in case of JWT token expiration
     */
    private Optional<SystemUser> getSystemUser(Long id) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            return Optional.of(UserModelMapper.map((InputStream) client.getById(id).getEntity()));
        }
        catch (NotFoundException n) {
            return Optional.empty();
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Method to retrieve system users by its ids
     * @param ids to be fetched and found
     * @return a list containing the system users found by the informed ids
     * @throws SystemException in case of any communication issue
     * @throws TokenExpiredException in case of JWT token expiration
     */
    private List<? extends SystemUser> getSystemUsers(Collection<Long> ids) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.getUsers(null, null, null, ids, true, true);
            return ListUserModelMapper.map((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Method to retrieve a specific system user by a given user subject
     * @param sub to be fetched and found
     * @return a optional list and if filled with the required system user
     * @throws SystemException in case it founds multiple users or if URL is malformed
     * @throws TokenExpiredException in case of JWT token expiration
     */
    private Optional<SystemUser> getSystemUser(String sub) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.getUsers(sub, null, null, null,true, true);
            List<? extends SystemUser> list = ListUserModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException | WebApplicationException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Method to retrieve a specific system user by a given user logon
     * @param logon to be fetched and found
     * @return a optional list and if filled with the required system user
     * @throws SystemException in case it founds multiple users or if URL is malformed
     * @throws TokenExpiredException in case of JWT token expiration
     */
    private Optional<SystemUser> getSystemUserByLogon(String logon) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.getUsers(null, null, logon, null,true, true);
            List<? extends SystemUser> list = ListUserModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Creates given user
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple users or if URL is malformed
     */
    public boolean create(SystemUser user, boolean skipKeycloak) throws SystemException {
        try {
            return createUser(user, skipKeycloak);
        } catch (TokenExpiredException e) {
            refreshToken();
            try {
                return createUser(user, skipKeycloak);
            } catch (TokenExpiredException tokenExpiredException) {
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Method to create a given user in the db
     * @param user information/object to be created and added into the db
     * @param skipKeycloak boolean to skip or not the user creation in the keycloak
     * @return returns true if user has been created with success
     * @throws SystemException in case it founds multiple users or if URL is malformed
     * @throws TokenExpiredException in case of JWT token expiration
     */
    private boolean createUser(SystemUser user, boolean skipKeycloak) throws TokenExpiredException, SystemException {
        UserResourceClient client;
        try {
            client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
        if (skipKeycloak) {
            user.setDelegatedCreation(true);
        }
        try (Response response = client.save((User) user)) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                logErrorEnabledResponse(response);
                return false;
            }
        } catch (ProcessingException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Returns all the existent System Users into a pagination format
     * @param search in case there should only be returned a specific type of users
     * @param pageNo where the user currently is
     * @param pageSize number of records to be show by page
     * @param sortBy any specific column
     * @param isAscending true in case records should be filter in ascending order
     * @return a page of all the requested system users
     */
    @Override
    public Page<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        Page<User> pageUsers = null;
        try {
            pageUsers = getPageUsers(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException e) {

            try {
                refreshToken();
                pageUsers = getPageUsers(search, pageNo, pageSize, sortBy, isAscending);
            } catch (SystemException | TokenExpiredException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage(), tokenExpiredException);
            }
        }
        return pageUsers;
    }

    /**
     * Returns all the existent System Users into a pagination format
     * @param search in case there should only be returned a specific type of users
     * @param pageNo where the user currently is
     * @param pageSize number of records to be show by page
     * @param sortBy any specific column
     * @param isAscending true in case records should be filter in ascending order
     * @return a page of all the requested system users
     * @throws TokenExpiredException in case of any issue while attempting communication with the client side
     */
    private Page<User> getPageUsers(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws TokenExpiredException {
        Page<User> page = new Page<>();
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            //WEB APPLICATION EXCEPTION jax rs
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);

            page = UserModelMapper.mapToPage((InputStream) response.getEntity());

        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    /**
     * Send the update password email to the active/requested user
     * @param id user id to be validated his information and the email sent
     * @return true in case of success
     */
    @Override
    public boolean sendUpdatePasswordEmail(long id) {
        boolean setAdminResetPassword = false;
        try {
            setAdminResetPassword = updatePasswordEmail(id);
        } catch (TokenExpiredException e) {
            try {
                refreshToken();
                setAdminResetPassword = updatePasswordEmail(id);
            } catch (SystemException | TokenExpiredException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage(), tokenExpiredException);
            }
        }
        return setAdminResetPassword;
    }

    /**
     * Method for the request to update the user password and send it via email to him
     * @param id of the user to update the password
     * @return true in case of success
     * @throws TokenExpiredException in case of any issue while attempting communication with the client side
     */
    private boolean updatePasswordEmail(long id) throws TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.sendUpdatePasswordEmail(id);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                logErrorEnabledResponse(response);
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Send the update user email to the active/requested user
     * @param userId of the user to update and execute action email verify
     * @param user having an update an email attribute
     * @return true in case of success
     * @throws SystemException in case of token expiration or any issue on the application
     */
    @Override
    public boolean updateEmailAndExecuteActionEmailVerify(long userId, SystemUser user) throws SystemException {
        return get(this::updateUserEmailAndVerify, userId, user);
    }

    /**
     * Method for the request to update the user email and sends a verification email
     * @param userId userId of the user to update and execute action email verify
     * @param user having an update an email attribute
     * @return true in case of success
     */
    private boolean updateUserEmailAndVerify(long userId, SystemUser user) {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.updateEmailAndExecuteActionEmailVerify(userId, (User) user, true);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                logErrorEnabledResponse(response);
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Deletes the requested user from the db
     * @param id of the user to be deleted
     * @return true in case of deletion
     */
    @Override
    public boolean deleteUser(long id) {
        try {
            return delUser(id);
        } catch (TokenExpiredException e) {
            try {
                refreshToken();
                return delUser(id);
            } catch (TokenExpiredException | SystemException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage());
                return false;
            }
        }
    }

    /**
     * Deletes the requested user from the db
     * @param id of the user to be deleted
     * @return true in case of deletion
     * @throws TokenExpiredException in case of any issue while attempting communication with the client side
     */
    private boolean delUser(long id) throws TokenExpiredException {
        boolean deleteUser = false;
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.delete(id);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                deleteUser = true;
            } else {
                logErrorEnabledResponse(response);
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return deleteUser;
    }

    /**
     * Updates the given user information, will validate by the given user id since that one cannot change
     * @param user information to be updated
     * @return true in case of success
     */
    @Override
    public boolean updateUser(SystemUser user) {
        try {
            return updateUser((User) user);
        } catch (TokenExpiredException e) {
            try {
                refreshToken();
                return updateUser((User) user);
            } catch (SystemException | TokenExpiredException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage(), tokenExpiredException);
            }
        }
        return false;
    }

    /**
     * Updates the given user information, will validate by the given user id since that one cannot change
     * @param user information to be updated
     * @return true in case of success
     * @throws TokenExpiredException in case of any issue while attempting communication with the client side
     */
    private boolean updateUser(User user) throws TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.save(user);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                logErrorEnabledResponse(response);
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Adds multiple users into the DB.
     *
     * @param systemUsers of users to be added
     * @return returns Optional containing the process summary.<br>
     * There will be a summary when:<br>
     * If ALL users were added without any issue (Http Status = 200)<br>
     * If issues regarding some users were found (Http status = 202)<br>
     * If none users were added, what means that all informed users contained issues (Http status = 400) <br>
     * In case of 500 error the returned optional will be empty
     * and the found issues as well.
     */
    public Optional<BatchSummary> create(List<SystemUser> systemUsers) throws MalformedURLException {
        try {
            return createBatch(systemUsers);
        } catch (TokenExpiredException e) {
            try {
                refreshToken();
                return createBatch(systemUsers);
            } catch (TokenExpiredException | SystemException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage(), tokenExpiredException);
                return Optional.empty();
            }

        }
    }

    /**
     * Adds multiple users into the DB.
     *
     * @param systemUsers of users to be added
     * @return returns Optional containing the process summary.<br>
     * There will be a summary when:<br>
     * If ALL users were added without any issue (Http Status = 200)<br>
     * If issues regarding some users were found (Http status = 202)<br>
     * If none users were added, what means that all informed users contained issues (Http status = 400) <br>
     * In case of 500 error the returned optional will be empty
     * and the found issues as well.
     * @throws TokenExpiredException in case of JWT token expiration
     * @throws MalformedURLException in case of URL specification
     */
    private Optional<BatchSummary> createBatch(List<SystemUser> systemUsers) throws MalformedURLException, TokenExpiredException {
        UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        List<User> users = systemUsers.stream().map(User.class::cast).collect(toList());
        try (Response response = client.create(users)) {
            if (response.getStatus() == Response.Status.OK.getStatusCode() ||
                    response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                return Optional.of(response.readEntity(BatchSummary.class));
            } else {
                logErrorEnabledResponse(response);
                return Optional.empty();
            }
        } catch (ProcessingException pe) {
            throw new ProcessingException(pe);
        }
    }

    /**
     * Invoke when Response error is to be logged
     * @param response object info
     */
    private void logErrorEnabledResponse(Response response) {
        if(log.isErrorEnabled()){
            log.error(response.readEntity(String.class));
        }
    }

    /**
     * Method to return the current oaf session
     * @return the current oaf access
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
