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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.TenantRoleUserModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;

import static io.radien.exception.GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN;

/**
 * Tenant Role REST Service Client
 *
 * It means that the server will have a RESTful web service which would provide the required
 * functionality to the client. The client send's a request to the web service on the server.
 * The server would either reject the request or comply and provide an adequate response to the
 * client.
 *
 * This Rest service client will be responsible to Deal with TenantRole endpoint
 *
 * @author Newton Carvalho
 */
@RequestScoped
public class TenantRoleUserRESTServiceClient extends AuthorizationChecker implements TenantRoleUserRESTServiceAccess {

    private static final long serialVersionUID = -3294029074149507760L;

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Under a pagination approach, retrieves the Users associations that exist
     * for a TenantRole
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    @Override
    public Page<? extends SystemTenantRoleUser> getUsers(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            return getUsersCore(tenantId, roleId, pageNo, pageSize);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getUsersCore(tenantId, roleId, pageNo, pageSize);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that Retrieves TenantRoleUser associations using pagination approach
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRole user associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemTenantRoleUser> getUsersCore(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAll(tenantId, roleId, pageNo, pageSize);
            return TenantRoleUserModelMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException |
                InternalServerErrorException e){
            throw new SystemException(e);
        }
    }

    /**
     * Under a pagination approach, retrieves the Ids for Users associations that exist
     * for a TenantRole
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    @Override
    public Page<Long> getUsersIds(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            return getUsersIdsCore(tenantId, roleId, pageNo, pageSize);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getUsersIdsCore(tenantId, roleId, pageNo, pageSize);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that Retrieves TenantRoleUser associations Ids using pagination approach
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRole user associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<Long> getUsersIdsCore(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAllUserIds(tenantId, roleId, pageNo, pageSize);
            return getPageIds((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException |
                InternalServerErrorException e){
            throw new SystemException(e);
        }
    }

    /**
     * Converts a Json Object into a Page object containing ids
     * @param i input stream from where the json object will be read
     * @return page containing user ids
     */
    protected Page<Long> getPageIds(InputStream i) {
        Page<Long> idsPage = new Page<>();
        try(JsonReader jsonReader = Json.createReader(i)) {
            JsonObject page = jsonReader.readObject();
            int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
            int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
            int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);
            List<Long> ids = FactoryUtilService.getArrayFromJson("results", page).
                    getValuesAs(JsonNumber.class).stream().
                    map(JsonNumber::longValue).collect(Collectors.toList());
            idsPage.setTotalPages(totalPages);
            idsPage.setTotalResults(totalResults);
            idsPage.setCurrentPage(currentPage);
            idsPage.setResults(ids);
        }
        return idsPage;
    }


    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role.
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well
     * @param tenantRoleUser association between Tenant, Role and User
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean assignUser(SystemTenantRoleUser tenantRoleUser) throws SystemException {
        try {
            return assignUserCore(tenantRoleUser);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return assignUserCore(tenantRoleUser);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role.
     * @param tenantRoleUser association between Tenant, Role and User
     * @return Boolean indicating if operation was concluded successfully
     * @throws TokenExpiredException is case of JWT token expiration
     * @throws SystemException in case of any error
     */
    private Boolean assignUserCore(SystemTenantRoleUser tenantRoleUser) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.assignUser((TenantRoleUser) tenantRoleUser);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleIds Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean unAssignUser(Long tenantId, Collection<Long> roleIds, Long userId) throws SystemException {
        try {
            return unAssignUserCore(tenantId, roleIds, userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return unAssignUserCore(tenantId, roleIds, userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleIds Roles identifiers
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws TokenExpiredException in case of JWT expiration
     * @throws SystemException in case of any error
     */
    private Boolean unAssignUserCore(Long tenantId, Collection<Long> roleIds, Long userId) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.unAssignUser(tenantId, roleIds, userId);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }



    /**
     * (Un)Assign/Dissociate/remove user from a TenantRole domain
     * Simply deletes a TenantRoleUser that eventually exists.
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well.
     *
     * It relies on a core method to perform this task.
     *
     * @param tenantRoleUserId identifier that maps a TenantRoleUser entity
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean delete(Long tenantRoleUserId) throws SystemException {
        try {
            return deleteCore(tenantRoleUserId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteCore(tenantRoleUserId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that removes user from a TenantRole domain
     * @param tenantRoleUserId identifier that maps a TenantRoleUser entity
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    private Boolean deleteCore(Long tenantRoleUserId) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(
                    oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.delete(tenantRoleUserId);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

}
