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
package io.radien.ms.openid.security;

import io.radien.api.model.user.SystemUser;
import io.radien.exception.ProcessingException;
import io.radien.exception.SystemException;
import io.radien.ms.openid.client.LinkedAuthorizationClient;
import io.radien.ms.openid.client.UserClient;
import io.radien.ms.openid.client.exception.NotFoundException;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URL;

/**
 * This abstract class maybe extended by any component that needs to propagate
 * access token information, retrieve information regarding the current logged user and
 * assess authorization (Role, permission, etc)
 */
public abstract class AuthorizationChecker extends TokensPropagator {

    private String accessToken;
    private String refreshToken;

    @Context
    private HttpServletRequest servletRequest;

//    @Inject
//    @RestClient
    private UserClient userClient;

//    @Inject
//    @RestClient
    private LinkedAuthorizationClient linkedAuthorizationClient;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Check if the current logged user has (grant to) some role (under a specific tenant - optionally)
     * @param tenantId Tenant identifier (Optional parameter)
     * @param roleName this parameter corresponds to the role name
     * @return
     * @throws SystemException
     */
    public boolean hasGrant(Long tenantId, String roleName) throws SystemException{
        try {
            this.preProcess();
            Response response = getLinkedAuthorizationClient().isRoleExistentForUser(getCurrentUserId(), roleName, tenantId);
            return response.readEntity(Boolean.class);
        } catch (Exception e) {
            this.log.error("Error checking authorization", e);
            throw new SystemException(e);
        }
    }

    /**
     * Check if the current logged user has (grant to) some role
     * @param roleName
     * @return
     * @throws SystemException
     */
    public boolean hasGrant(String roleName) throws SystemException{
        return hasGrant(null, roleName);
    }

    /**
     * Check if the current logged user has (has grant to) some permission regarding (or not)
     * some tenant
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (not mandatory)
     * @return
     * @throws SystemException
     */
    public boolean hasGrant(Long permissionId, Long tenantId) throws SystemException {
        try {
            this.preProcess();
            Response response = getLinkedAuthorizationClient().existsSpecificAssociation(tenantId,
                    permissionId, null, getCurrentUserId(), true);
            return true;
        }
        catch (NotFoundException e) {
            // In case of 404 status we just want to return false
            return false;
        }
        catch (Exception e) {
            this.log.error("Error checking authorization", e);
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves the User Id using sub as parameter
     * @param sub sub from the current logged logged user
     * @return
     * @throws SystemException
     */
    protected Long getCurrentUserIdBySub(String sub) throws SystemException {
        try {
            Response response = getUserClient().getUserIdBySub(sub);
            return response.readEntity(Long.class);
        }
        catch (NotFoundException e) {
            throw new SystemException("Could Not find User id for sub=" + sub);
        }
        catch(Exception e) {
            log.error("Error trying to obtain user id", e);
            throw new SystemException("Error trying to obtain user id", e);
        }
    }

    /**
     * Retrieves the ID that belongs to the current logged user
     * @return
     * @throws SystemException
     */
    protected Long getCurrentUserId() throws SystemException {
        SystemUser user = getInvokerUser();
        if (user == null) {
            throw new SystemException("No current user available");
        }
        return getCurrentUserIdBySub(user.getSub());
    }

    /**
     * Build method that produces an UserClient instance.
     * Is being adopted (instead of direct injection) due some EJB container issues
     * encountered on Unit Tests
     * @return
     */
    protected UserClient buildUserClient() {
        try {
            String urlStr = ConfigProvider.getConfig().
                    getValue("system.ms.endpoint.usermanagement",
                            String.class);
            return RestClientBuilder.newBuilder()
                    .baseUrl(new URL(urlStr)).build(UserClient.class);
        }
        catch (Exception e) {
            log.error("Error build UserClient", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Build method that produces an LinkedAuthorizationClient instance.
     * Is being adopted (instead of direct injection) due some EJB container issues
     * encountered on Unit Tests
     * @return
     */
    protected LinkedAuthorizationClient buildLinkedClient() {
        try {
            String urlStr = ConfigProvider.getConfig().
                    getValue("system.ms.endpoint.rolemanagement",
                            String.class);
            return RestClientBuilder.newBuilder()
                    .baseUrl(new URL(urlStr)).build(LinkedAuthorizationClient.class);
        }
        catch (Exception e) {
            log.error("Error build LinkedAuthorizationClient", e);
            throw new IllegalStateException(e);
        }
    }

    public UserClient getUserClient() {
        if (userClient == null) {
            userClient = buildUserClient();
        }
        return userClient;
    }

    public LinkedAuthorizationClient getLinkedAuthorizationClient() {
        if (linkedAuthorizationClient == null) {
            linkedAuthorizationClient = buildLinkedClient();
        }
        return linkedAuthorizationClient;
    }

    public void setLinkedAuthorizationClient(LinkedAuthorizationClient linkedAuthorizationClient) {
        this.linkedAuthorizationClient = linkedAuthorizationClient;
    }

    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
