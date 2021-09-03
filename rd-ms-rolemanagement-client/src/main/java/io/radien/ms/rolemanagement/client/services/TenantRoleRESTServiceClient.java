/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.RoleModelMapper;
import io.radien.ms.rolemanagement.client.util.TenantRoleModelMapper;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
public class TenantRoleRESTServiceClient extends AuthorizationChecker implements TenantRoleRESTServiceAccess {

    private static final Logger log = LoggerFactory.getLogger(TenantRoleRESTServiceClient.class);
    private static final long serialVersionUID = -3294029074149507760L;

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Retrieves TenantRole associations using pagination approach
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRole user associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    @Override
    public Page<? extends SystemTenantRole> getAll(int pageNo, int pageSize) throws SystemException {
        try {
            return getAllCore(pageNo, pageSize);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getAllCore(pageNo, pageSize);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that Retrieves TenantRole associations using pagination approach
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRole user associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemTenantRole> getAllCore(int pageNo, int pageSize) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAll(pageNo, pageSize);
            return TenantRoleModelMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            throw new SystemException("Error trying to retrieve Tenant Role User associations", e);
        }
    }

    /**
     * Given a Tenant and a role identifier obtains the TenantRole Id
     * (To do that it invokes the core method counterpart and handles TokenExpiration error)
     * @param tenant Tenant identifier (mandatory)
     * @param role Role identifier (mandatory)
     * @return Optional containing TenantRole Identifier (Empty if no id could be found)
     * @throws SystemException in case of any error
     */
    public Optional<Long> getIdByTenantRole(Long tenant, Long role) throws SystemException {
        try {
            return getIdByTenantRoleCore(tenant, role);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getIdByTenantRoleCore(tenant, role);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that obtains the TenantRole Id (for given Tenant and role identifiers)
     * @param tenant Tenant identifier (mandatory)
     * @param role Role identifier (mandatory)
     * @return Optional containing TenantRole Identifier (Empty if no id could be found)
     * @throws SystemException in case of any error
     */
    protected Optional<Long> getIdByTenantRoleCore(Long tenant, Long role) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getIdByTenantRole(tenant, role);
            return Optional.of(response.readEntity(Long.class));
        }
        catch (NotFoundException nf) {
            return Optional.empty();
        }
        catch (TokenExpiredException tke) {
            throw  tke;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Retrieve a Tenant Role association using the id as search parameter.
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param id Tenant Role id association to guide the search process
     * @return Optional containing Tenant Role association found.
     * @throws SystemException in case of any error
     */
    @Override
    public Optional<SystemTenantRole> getTenantRoleById(Long id) throws SystemException{
        try {
            return getTenantRoleByIdCore(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTenantRoleByIdCore(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that retrieves a Tenant Role association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return Optional containing Tenant Role association found.
     * @throws TokenExpiredException in case of JWT token expiration
     * @throws SystemException in case of any error
     */
    private Optional<SystemTenantRole> getTenantRoleByIdCore(Long id) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getById(id);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Optional.of(TenantRoleModelMapper.map((InputStream) response.getEntity()));
            }
            log.info("Error retrieving TenantRole for id {}. Obtained status= {}",
                    id, response.getStatusInfo().getStatusCode());
            return Optional.empty();
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Create a TenantRole association (Invokes the core method counterpart and
     * handles TokenExpiration error)
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return Boolean indicating if the operation was concluded with success.
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean save(SystemTenantRole tenantRole) throws SystemException {
        try {
            return saveCore(tenantRole);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return saveCore(tenantRole);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Main method invoked to Create a TenantRole association
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return Boolean indicating if the operation was concluded with success.
     * @throws TokenExpiredException if the JWT token expires
     * @throws SystemException in case of any error
     */
    private Boolean saveCore(SystemTenantRole tenantRole) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.save((TenantRole) tenantRole);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Check if a Tenant role association exists (Invokes the core method counterpart and
     *  handles TokenExpiration error)
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true (if association exists), false otherwise.
     * @throws SystemException in case of any other error.
     */
    @Override
    public Boolean exists(Long tenantId, Long roleId) throws SystemException {
        try {
            return existsCore(tenantId, roleId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return existsCore(tenantId, roleId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that will check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true (if association exists), false otherwise.
     * @throws TokenExpiredException if JWT token expires
     * @throws SystemException in case of any other error.
     */
    private Boolean existsCore(Long tenantId, Long roleId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.exists(tenantId, roleId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
            return false;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * It Invokes the core method counterpart and handles TokenExpiration error
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions.
     * @throws SystemException in case of any error
     */
    @Override
    public List<? extends SystemPermission> getPermissions(Long tenantId, Long roleId, Long userId) throws SystemException {
        try {
            return getPermissionsCore(tenantId, roleId, userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getPermissionsCore(tenantId, roleId, userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions.
     * @throws TokenExpiredException if JWT token expires
     * @throws SystemException in case of any error
     */
    private List<? extends SystemPermission> getPermissionsCore(Long tenantId, Long roleId, Long userId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getPermissions(tenantId, roleId, userId);
            return ListPermissionModelMapper.map((InputStream) response.getEntity());
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
        try {
            return getTenantsCore(userId, roleId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTenantsCore(userId, roleId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
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
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getTenants(userId, roleId);
            return TenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ParseException | ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
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
        try {
            return getRolesForUserTenantCore(userId, tenantId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getRolesForUserTenantCore(userId, tenantId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
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
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getRolesForUserTenant(userId, tenantId);
            return RoleModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (MalformedURLException | ProcessingException | ParseException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Summary: Retrieve TenantRole associations by tenant identifier, role identifier and a
     * junction function
     * To do this action will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRole associations.
     * @throws SystemException in case of Any error
     */
    @Override
    public List<? extends SystemTenantRole> getTenantRoles(Long tenantId, Long roleId, boolean isLogicalConjunction) throws SystemException {
        try {
            return getTenantRolesCore(tenantId, roleId, isLogicalConjunction);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTenantRolesCore(tenantId, roleId, isLogicalConjunction);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that retrieves TenantRole associations that met the following parameter.
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRole associations.
     * @throws TokenExpiredException in case of JWT expiration
     * @throws SystemException in case of Any error
     */
    private List<? extends SystemTenantRole> getTenantRolesCore(Long tenantId, Long roleId, boolean isLogicalConjunction) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getSpecific(tenantId, roleId, isLogicalConjunction);
            return TenantRoleModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }
}
