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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.BadRequestException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.RoleModelMapper;
import io.radien.ms.rolemanagement.client.util.TenantRoleUserModelMapper;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(TenantRoleUserRESTServiceClient.class);

    /**
     * Under a pagination approach, retrieves the Tenant Role Users associations that currently exist
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantRoleId tenant role identifier(Acting as filter)
     * @param userId user identifier (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    @Override
    public Page<? extends SystemTenantRoleUser> getAll(Long tenantRoleId, Long userId,
                                                       int pageNo, int pageSize,
                                                       List<String> sortBy, boolean isAscending) throws SystemException {
        return get(() -> getAllCore(tenantRoleId, userId, pageNo, pageSize, sortBy, isAscending));
    }

    /**
     * Core method that Retrieves TenantRoleUser associations using pagination approach
     * @param tenantRoleId tenant role identifier(Acting as filter)
     * @param userId user identifier (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return Page containing TenantRole user associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemTenantRoleUser> getAllCore(Long tenantRoleId, Long userId,
                                                              int pageNo, int pageSize,
                                                              List<String> sortBy, boolean isAscending) throws SystemException {
        TenantRoleUserResourceClient client = getClient();
        try (Response response = client.getAll(tenantRoleId, userId, pageNo, pageSize, sortBy, isAscending)){
            return TenantRoleUserModelMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException e){
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
        return get(() -> getUsersIdsCore(tenantId, roleId, pageNo, pageSize));
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
        TenantRoleUserResourceClient client = getClient();
        try (Response response = client.getAllUserIds(tenantId, roleId, pageNo, pageSize)) {
            return getPageIds((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException e){
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
        return get(this::assignUserCore, tenantRoleUser);
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
        return get(()-> unAssignUserCore(tenantId, roleIds, userId));
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
        return get(this::deleteCore, tenantRoleUserId);
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


    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * For this, it Invokes the core method counterpart and handles TokenExpiration error
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenants
     * @throws SystemException in case of any error
     */
    @Override
    public List<? extends SystemTenant> getTenants(Long userId, Long roleId) throws SystemException {
        return get(this::getTenantsCore, userId, roleId);
    }

    /**
     * Core method that retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenants
     * @throws TokenExpiredException if JWT token expires
     * @throws SystemException in case of any error
     */
    private List<? extends SystemTenant> getTenantsCore(Long userId, Long roleId) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getTenants(userId, roleId);
            return TenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ParseException | ExtensionException | ProcessingException | MalformedURLException |
                io.radien.exception.InternalServerErrorException | BadRequestException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Updates a TenantRoleUser previously crated (When a user was assigned into a TenantRole)
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well.
     * @param tenantRoleUser association between Tenant, Role and User
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    public Boolean update(SystemTenantRoleUser tenantRoleUser) throws SystemException {
        return this.get(this::updateCore, tenantRoleUser);
    }

    /**
     * Core method that updates the TenantRoleUser
     * @param tenantRoleUser association between Tenant, Role and User
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    private Boolean updateCore(SystemTenantRoleUser tenantRoleUser) throws SystemException{
        TenantRoleUserResourceClient client = getClient();
        try (Response response = client.update(tenantRoleUser.getId(), (TenantRoleUser)tenantRoleUser)) {
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ProcessingException | ExtensionException | BadRequestException |
                NotFoundException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves TenantRoleUser associations that met the following parameter
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantRoleId TenantRole identifier
     * @param userId User identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRoleUser associations.
     * @throws SystemException in case of Any error
     */
    @Override
    public List<? extends SystemTenantRoleUser> getTenantRoleUsers(Long tenantRoleId, Long userId,
                                                                   boolean isLogicalConjunction) throws SystemException {
        return get(() -> getTenantRoleUsersCore(tenantRoleId, userId, isLogicalConjunction));
    }

    /**
     * Retrieves TenantRoleUser associations that met the following parameter
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantRoleId Tenant identifier
     * @param userId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRoleUser associations.
     * @throws SystemException in case of Any error
     */
    private List<? extends SystemTenantRoleUser> getTenantRoleUsersCore(Long tenantRoleId, Long userId,
                                                                        boolean isLogicalConjunction) throws SystemException {
        TenantRoleUserResourceClient client = getClient();
        try (Response response = client.getSpecific(tenantRoleId, userId, isLogicalConjunction)) {
            return TenantRoleUserModelMapper.mapList((InputStream) response.getEntity());
        }
        catch(ProcessingException | ExtensionException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Retrieve a Tenant Role User using the id as search parameter.
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param id Tenant Role User id association to guide the search process
     * @return Optional containing Tenant Role User found.
     * @throws SystemException in case of any error
     */
    @Override
    public Optional<SystemTenantRoleUser> getTenantRoleUserById(Long id) throws SystemException{
        return get(this::getTenantRoleUserByIdCore, id);
    }

    /**
     * Core method that retrieves a Tenant Role User using the id as search parameter.
     * @param id Tenant Role User id to guide the search process
     * @return Optional containing Tenant Role User found.
     * @throws TokenExpiredException in case of JWT token expiration
     * @throws SystemException in case of any error
     */
    private Optional<SystemTenantRoleUser> getTenantRoleUserByIdCore(Long id) throws SystemException {
        TenantRoleUserResourceClient client = getClient();
        try (Response response = client.getById(id)){
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Optional.of(TenantRoleUserModelMapper.map((InputStream) response.getEntity()));
            }
            log.info("Error retrieving TenantRoleUser for id {}. Obtained status= {}",
                    id, response.getStatusInfo().getStatusCode());
        }
        catch (NotFoundException e) {
            if (log.isDebugEnabled()) {
                log.info("TenantRoleUser not found for {}",id);
            }
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
        return Optional.empty();
    }


    /**
     * Retrieves the existent Roles for a User of specific associated Tenant
     * For this, it Invokes the core method counterpart and handles TokenExpiration error
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing Roles
     * @throws SystemException in case of any error
     */
    @Override
    public List<? extends SystemRole> getRolesForUserTenant(Long userId, Long tenantId) throws SystemException {
        return get(this::getRolesForUserTenantCore, userId, tenantId);
    }

    /**
     * Core method that retrieves the existent Roles for a User of specific associated Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing Roles
     * @throws TokenExpiredException if JWT token expires
     * @throws SystemException in case of any error
     */
    private List<? extends SystemRole> getRolesForUserTenantCore(Long userId, Long tenantId) throws SystemException {
        TenantRoleUserResourceClient client = getClient();
        try (Response response = client.getRolesForUserTenant(userId, tenantId)) {
            return RoleModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ProcessingException | ParseException | BadRequestException |
                io.radien.exception.InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Assemblies a {@link TenantRoleUserResourceClient} instance using RestClientBuilder Microprofile API
     * @return instance of {@link TenantRoleResourceClient}
     * @throws SystemException in case of Malformed URL
     */
    private TenantRoleUserResourceClient getClient() throws SystemException {
        try {
            return clientServiceUtil.getTenantRoleUserResourceClient(oaf.getProperty(
                    OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
    }
}
