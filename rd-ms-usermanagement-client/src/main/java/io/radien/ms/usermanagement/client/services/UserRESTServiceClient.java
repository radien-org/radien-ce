/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.entity.Page;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.batch.BatchSummary;
import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;
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
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
 */
@Stateless
@Default
@RegisterProvider(UserResponseExceptionMapper.class)
public class UserRESTServiceClient implements UserRESTServiceAccess {
    private static final long serialVersionUID = 1L;

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
                log.error(expiredException.getMessage(), expiredException);
                throw new SystemException(expiredException);
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
                log.error(expiredException.getMessage(), expiredException);
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
                log.error(expiredException.getMessage(), expiredException);
                throw new SystemException(expiredException);
            }
        }
    }

    private Optional<SystemUser> getSystemUser(Long id) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            return Optional.of(UserModelMapper.map((InputStream) client.getById(id).getEntity()));
        }
        catch (NotFoundException n) {
            return Optional.empty();
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    private Optional<SystemUser> getSystemUser(String sub) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.getUsers(sub, null, null, true, true);
            List<? extends SystemUser> list = ListUserModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }

    private Optional<SystemUser> getSystemUserByLogon(String logon) throws SystemException, TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.getUsers(null, null, logon, true, true);
            List<? extends SystemUser> list = ListUserModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(e);
        }
    }


    /**
     * Creates given user
     *
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemUser user, boolean skipKeycloak) throws SystemException {
        UserResourceClient client;
        try {
            return createUser(user, skipKeycloak);
        } catch (TokenExpiredException e) {
            refreshToken();
            try {
                return createUser(user, skipKeycloak);
            } catch (TokenExpiredException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage(), tokenExpiredException);
                throw new SystemException(tokenExpiredException);
            }
        }
    }

    private boolean createUser(SystemUser user, boolean skipKeycloak) throws TokenExpiredException, SystemException {
        UserResourceClient client;
        try {
            client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new SystemException(e);
        }
        if (skipKeycloak) {
            user.setDelegatedCreation(true);
        }
        try (Response response = client.save((User) user)) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException e) {
            throw new SystemException(e);
        }
    }

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

    private Page<User> getPageUsers(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws TokenExpiredException {
        Page<User> page = new Page<>();
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            //WEB APPLICATION EXCEPTION jax rs
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);

            //JsonArray jsonArray = (JsonArray) Json.createReader(new StringReader(response.readEntity(String.class))).readObject().get("results");
            //listUsers = UserFactory.convert(jsonArray);
           page = UserModelMapper.mapToPage((InputStream) response.getEntity());

        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

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

    private boolean updatePasswordEmail(long id) throws TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.sendUpdatePasswordEmail(id);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

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

    private boolean delUser(long id) throws TokenExpiredException {
        boolean deleteUser = false;
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.delete(id);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                deleteUser = true;
            } else {
                log.error(response.readEntity(String.class));
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return deleteUser;
    }

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

    private boolean updateUser(User user) throws TokenExpiredException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
            Response response = client.save(user);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
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


    private Optional<BatchSummary> createBatch(List<SystemUser> systemUsers) throws MalformedURLException, TokenExpiredException {
        UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        List<User> users = systemUsers.stream().map(User.class::cast).collect(toList());
        try (Response response = client.create(users)) {
            if (response.getStatus() == Response.Status.OK.getStatusCode() ||
                    response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                return Optional.of(response.readEntity(BatchSummary.class));
            } else {
                log.error(response.readEntity(String.class));
                return Optional.empty();
            }
        } catch (ProcessingException pe) {
            log.error(pe.getMessage(), pe);
            throw pe;
        }
    }

    public boolean refreshToken() throws SystemException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.refreshToken(tokensPlaceHolder.getRefreshToken());
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                tokensPlaceHolder.setAccessToken(response.readEntity(String.class));
                return true;
            }
            return false;

        } catch (ExtensionException | ProcessingException | MalformedURLException | TokenExpiredException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
