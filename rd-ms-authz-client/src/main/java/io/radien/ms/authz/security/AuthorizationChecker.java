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
package io.radien.ms.authz.security;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.client.exception.NotFoundException;
import io.radien.ms.authz.util.BiFunction;
import io.radien.ms.authz.util.Consumer;
import io.radien.ms.authz.util.FourthFunction;
import io.radien.ms.authz.util.Function;
import io.radien.ms.authz.util.FunctionReturn;
import io.radien.ms.authz.util.ThirdFunction;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * This abstract class maybe extended by any component that needs to
 * evaluate authorization (Role, permission, etc)
 *
 * @author Newton Carvalho
 */
public abstract class AuthorizationChecker implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationChecker.class);

    @Context
    private HttpServletRequest servletRequest;

    private OAFAccess oafAccess;

    private UserClient userClient;

    private TenantRoleClient tenantRoleClient;

    private TokensPlaceHolder tokensPlaceHolder;

    private RestClientBuilder restClientBuilder;

    private PermissionClient permissionClient;

    public AuthorizationChecker(){

    }

    /**
     * By the active user this method will update the refresh token, so that he can continue to use the application
     * @return true in case of success of updating the refresh token
     * @throws SystemException in case of any issue while getting the current user or getting the token information
     */
    public boolean refreshToken() throws SystemException {
        log.info("Trying to refresh token");
        try {
            getUserClient();
            getTokensPlaceHolder();

            Response response = userClient.refreshToken(tokensPlaceHolder.getRefreshToken());
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                tokensPlaceHolder.setAccessToken(response.readEntity(String.class));
                return true;
            }
            return false;

        } catch (IllegalStateException | ProcessingException | TokenExpiredException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Check if the current logged user has (grant to) some role (under a specific tenant - optionally)
     * @param tenantId Tenant identifier (Optional parameter)
     * @param roleName this parameter corresponds to the role name
     * @return true if user has the correct access
     * @throws SystemException in case of any issue while getting the current user or getting correct access
     * information
     */
    public boolean hasGrant(Long tenantId, String roleName) throws SystemException{
        try {
            this.preProcess();
            Response response;
            try {
                response = getTenantRoleClient().
                        isRoleExistentForUser(getCurrentUserId(), roleName, tenantId);
            } catch (TokenExpiredException tee) {
                refreshToken();
                response = getTenantRoleClient().
                        isRoleExistentForUser(getCurrentUserId(), roleName, tenantId);
            }
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
            return false;

        } catch (SystemException e) {
            throw new SystemException(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
        }
    }

    /**
     * Check if the current logged user has (permission to) some action on resource (under a specific tenant - optionally)
     * @param tenantId Tenant identifier (Optional parameter)
     * @param actionName this parameter corresponds to the role name
     * @param resourceName this parameter corresponds to the role name
     * @return true if user has the correct access
     * @throws SystemException in case of any issue while getting the current user or getting correct access
     * information
     */
    public boolean hasPermission(Long tenantId, String actionName,String resourceName) throws SystemException{
        try {
            this.preProcess();
            Optional<Long> permissionId = get(this::getPermissionIdCore,actionName,resourceName);
            if(!permissionId.isPresent()) {
                return false;
            }
            return hasGrant(permissionId.get(),tenantId);
        } catch (SystemException e) {
            throw new SystemException(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
        }
    }

    private Optional<Long> getPermissionIdCore( String actionName,String resourceName) throws SystemException{
        Response response = getPermissionClient().getIdByResourceAndAction(resourceName,actionName);
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            return Optional.ofNullable(response.readEntity(Long.class));
        } else {
            log.error("Permission Not Found resource:{} action:{}",resourceName,actionName);
            return Optional.empty();
        }
    }

    /**
     * Check if the current logged user has (grant to) some role
     * @param roleName role to be validated
     * @return true in case of user has access
     * @throws SystemException in case of any issue while getting the current user or getting correct access
     * information
     */
    public boolean hasGrant(String roleName) throws SystemException{
        return hasGrant(null, roleName);
    }

    /**
     * Check if the current logged user has (has grant to) some permission regarding (or not)
     * some tenant
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (not mandatory)
     * @return true in case user has access with correct role and permissions
     * @throws SystemException in case of any issue while getting the current user or getting correct access
     * information
     */
    public boolean hasGrant(Long permissionId, Long tenantId) throws SystemException {
        try {
            this.preProcess();
            Response response;
            try {
                response = getTenantRoleClient().isPermissionExistentForUser(getCurrentUserId(),
                        permissionId, tenantId);
            } catch (TokenExpiredException e) {
                try{
                    refreshToken();
                    response = getTenantRoleClient().isPermissionExistentForUser(getCurrentUserId(),
                            permissionId, tenantId);
                } catch (TokenExpiredException tokenExpiredException){
                    throw new SystemException(tokenExpiredException);
                }
            }
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
            return false;
        }
        catch (SystemException e) {
            throw new SystemException(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
        }
    }

    /**
     * Check if the current logged user has (grant to) one of the specific given roles in a list
     * (under a specific tenant - optionally)
     * @param tenantId Tenant identifier (Optional parameter)
     * @param roleNames this parameter corresponds to the role names inside a list
     * @return true in case the roles exist for the user
     * @throws SystemException in case of any issue while communicating with the client
     */
    public boolean hasGrantMultipleRoles(Long tenantId, List<String> roleNames) throws SystemException {
        try {
            this.preProcess();
            Response response = null;
            try {
                response = getTenantRoleClient().
                        checkPermissions(getCurrentUserId(), roleNames, tenantId);
            } catch (TokenExpiredException tee) {
                refreshToken();
                response = getTenantRoleClient().
                        checkPermissions(getCurrentUserId(), roleNames, tenantId);
            }
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
            return false;
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * Check if the current logged user has (grant to) one of the specific given roles in a given list
     * @param roleNames to be validated
     * @return true in case they exist
     * @throws SystemException in case of issue while communicating with the client
     */
    public boolean hasGrantMultipleRoles(List<String> roleNames) throws SystemException{
        return hasGrantMultipleRoles(null, roleNames);
    }

    /**
     * Retrieves the User Id using sub as parameter
     * @param sub sub from the current logged logged user
     * @return user id in case of user has been found
     * @throws SystemException in case of any error or issue while trying to obtain user information
     */
    protected Long getCurrentUserIdBySub(String sub) throws SystemException {
        try {
            Response response = null;
            try {
                response = getUserClient().getUserIdBySub(sub);
            } catch (TokenExpiredException tee) {
                refreshToken();
                response = getUserClient().getUserIdBySub(sub);
            }
            return response.readEntity(Long.class);
        }
        catch (NotFoundException e) {
            throw new NotFoundException(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString(), e);
        }
        catch(SystemException e) {
            log.error(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(),e);
            throw e;
        }
    }

    /**
     * Retrieves the ID that belongs to the current logged user
     * @return the current user id
     * @throws SystemException in case of current user is null
     */
    protected Long getCurrentUserId() throws SystemException {
        SystemUser user = getInvokerUser();
        if (user == null) {
            throw new SystemException(GenericErrorCodeMessage.
                    NO_CURRENT_USER_AVAILABLE.toString());
        }
        return getCurrentUserIdBySub(user.getSub());
    }

    protected boolean isLoggedIn(){
        return getInvokerUser()!=null;
    }
    /**
     * Retrieves the reference for current logged user
     * @return the reference for current logged user
     */
    protected SystemUser getInvokerUser() {
        HttpSession session = getServletRequest().getSession(false);
        if(session == null){
            return null;
        }
        return (SystemUser) session.getAttribute("USER");
    }

    /**
     * This method retrieves the tokens (access and refresh), and store them
     * to be transferred through GlobalHeaders
     */
    public void preProcess() {
        HttpSession httpSession = this.getServletRequest().getSession(false);
        if (this.getTokensPlaceHolder().getAccessToken() == null) {
            if (httpSession.getAttribute("accessToken") != null) {
                this.getTokensPlaceHolder().setAccessToken(httpSession.getAttribute("accessToken").toString());
            }
            else {
                // Lets obtain (at least) accessToken from Header
                String token = this.getServletRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (token != null && token.startsWith("Bearer ")) {
                    this.getTokensPlaceHolder().setAccessToken(token.substring(7));
                }
            }
        }
    }

    /**
     * Build method that produces an Rest client instance (i.e UserClient or LinkedAuthorizationClient).
     * Is being adopted (instead of direct injection) due some EJB container issues
     * encountered on Unit Tests
     * @return rest build client requested
     */
    protected <T> T buildClient(String url, Class<T> clazz) throws SystemException {
        try {
            return getRestClientBuilder().baseUrl(new URL(url)).build(clazz);
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * Gets user management client instance
     * @return user client for user management instance
     * @throws SystemException in case of any issue while retrieving the communication user client instance
     */
    public UserClient getUserClient() throws SystemException {
        if (userClient == null) {
            String url = getOafAccess().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT);
            log.info("UserManagement path:{}",url);
            userClient = buildClient(url,
                    UserClient.class);
        }
        return userClient;
    }

    /**
     * Gets tenant role management client instance
     * @return tenant role client for user management instance
     * @throws SystemException in case of any issue while retrieving the communication tenant
     * role client instance
     */
    public TenantRoleClient getTenantRoleClient() throws SystemException{
        if (tenantRoleClient == null) {
            String url = getOafAccess().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT);
            log.info("RoleManagement path:{}",url);
            tenantRoleClient = buildClient(url,
                    TenantRoleClient.class);
        }
        return tenantRoleClient;
    }

    /**
     * Gets permission management client instance
     * @return permission client for permission management instance
     * @throws SystemException in case of any issue while retrieving the communication tenant
     * role client instance
     */
    public PermissionClient getPermissionClient() throws SystemException{
        if (permissionClient == null) {
            String url= getOafAccess().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT);
            log.info("PermissionManagement path:{}",url);
            permissionClient = buildClient(url,
                    PermissionClient.class);
        }
        return permissionClient;
    }

    /**
     * Sets the tenant role client as the given one
     * @param tenantRoleClient given tenant role client instance to be set
     */
    public void setTenantRoleClient(TenantRoleClient tenantRoleClient) {
        this.tenantRoleClient = tenantRoleClient;
    }

    /**
     * Sets the user management client instance as the given one
     * @param userClient given user client instance to be set
     */
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    /**
     * Gets the active token place holder
     * @return the active token place holder
     */
    public TokensPlaceHolder getTokensPlaceHolder() {
        if (tokensPlaceHolder == null) {
            tokensPlaceHolder =  CDI.current().select(TokensPlaceHolder.class).get();
        }
        return tokensPlaceHolder;
    }

    /**
     * Gets the Rest Client builder object
     * @return the rest client builder
     */
    public RestClientBuilder getRestClientBuilder() {
        if (restClientBuilder == null) {
            restClientBuilder = RestClientBuilder.newBuilder();
        }
        return restClientBuilder;
    }

    /**
     * Gets the current servlet request
     * @return the current http servlet request
     */
    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    /**
     * Gets the current OAF access
     * @return the oaf object
     */
    public OAFAccess getOafAccess() {
        if(oafAccess == null) {
            oafAccess = CDI.current().select(OAFAccess.class).get();
        }
        return oafAccess;
    }

    public <T> T get(FunctionReturn<T> function) throws SystemException {
        try {
            return function.apply();
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return function.apply();
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }


    public <T,R> R get(Function<T,R> function, T input) throws SystemException {
        try {
            return function.apply(input);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return function.apply(input);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    public <T,U,R> R get(BiFunction<T,U,R> function, T input1,U input2) throws SystemException {
        try {
            return function.apply(input1,input2);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return function.apply(input1,input2);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    public <T,U,R,A> R get(ThirdFunction<T,U,R,A> function, T input1, U input2, A input3) throws SystemException {
        try {
            return function.apply(input1,input2, input3);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return function.apply(input1,input2, input3);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    public <T,U,R,A,B> R get(FourthFunction<T,U,R,A,B> function, T input1, U input2, A input3, B input4 ) throws SystemException {
        try {
            return function.apply(input1,input2, input3, input4);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                return function.apply(input1,input2, input3, input4);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    public <I> void add(Consumer<I> consumer, I i) throws SystemException {
        try {
            consumer.accept(i);
        } catch (TokenExpiredException tokenExpiredException) {
            refreshToken();
            try {
                consumer.accept(i);
            } catch (TokenExpiredException expiredException) {
                throw new SystemException( GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }
}

