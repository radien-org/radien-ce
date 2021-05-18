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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TokenExpiredException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.TenantRoleModelMapper;
import io.radien.ms.rolemanagement.client.util.TenantRoleUserModelMapper;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

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
@Stateless
@Default
public class TenantRoleRESTServiceClient extends AuthorizationChecker implements TenantRoleRESTServiceAccess {

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Retrieves a Tenant Role association using the id as search parameter.
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
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

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
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Create a TenantRole association
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
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private Boolean saveCore(SystemTenantRole tenantRole) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.save((TenantRole) tenantRole);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Boolean.TRUE;
            }
            log.info("Error trying to save TenantRole. Obtained status= {}", response.getStatusInfo().getStatusCode());
            return Boolean.FALSE;
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Check if a Tenant role association exists
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
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

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
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
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
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private List<? extends SystemPermission> getPermissionsCore(Long tenantId, Long roleId, Long userId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getPermissions(tenantId, roleId, userId);
            return ListPermissionModelMapper.map((InputStream) response.getEntity());
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
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
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private List<? extends SystemTenant> getTenantsCore(Long userId, Long roleId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getTenants(userId, roleId);
            return TenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean assignUser(Long tenantId, Long roleId, Long userId) throws SystemException {
        try {
            return assignUserCore(tenantId, roleId, userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return assignUserCore(tenantId, roleId, userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private Boolean assignUserCore(Long tenantId, Long roleId, Long userId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.assignUser(tenantId, roleId, userId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Boolean.TRUE;
            }
            log.info("Error assigning tenant {} role {} user {}. Obtained status= {}",
                    tenantId, roleId, userId, response.getStatusInfo().getStatusCode());
            return Boolean.FALSE;
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean unassignUser(Long tenantId, Long roleId, Long userId) throws SystemException{
        try {
            return unassignUserCore(tenantId, roleId, userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return unassignUserCore(tenantId, roleId, userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private Boolean unassignUserCore(Long tenantId, Long roleId, Long userId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.unassignUser(tenantId, roleId, userId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Boolean.TRUE;
            }
            log.info("Error (un)assigning tenant {} role {} user {}. Obtained status= {}",
                    tenantId, roleId, userId, response.getStatusInfo().getStatusCode());
            return Boolean.FALSE;
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean assignPermission(Long tenantId, Long roleId, Long permissionId) throws SystemException {
        try {
            return assignPermissionCore(tenantId, roleId, permissionId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return assignPermissionCore(tenantId, roleId, permissionId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private Boolean assignPermissionCore(Long tenantId, Long roleId, Long permissionId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.assignPermission(tenantId, roleId, permissionId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Boolean.TRUE;
            }
            log.info("Error assigning tenant {} role {} permission {}. Obtained status= {}",
                    tenantId, roleId, permissionId, response.getStatusInfo().getStatusCode());
            return Boolean.FALSE;
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean unassignPermission(Long tenantId, Long roleId, Long permissionId) throws SystemException {
        try {
            return unassignPermissionCore(tenantId, roleId, permissionId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return unassignPermissionCore(tenantId, roleId, permissionId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private Boolean unassignPermissionCore(Long tenantId, Long roleId, Long permissionId) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.unassignPermission(tenantId, roleId, permissionId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return Boolean.TRUE;
            }
            log.info("Error (un)assigning tenant {} role {} permission {}. Obtained status= {}",
                    tenantId, roleId, permissionId, response.getStatusInfo().getStatusCode());
            return Boolean.FALSE;
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves TenantRole associations that met the following parameter
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
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    private List<? extends SystemTenantRole> getTenantRolesCore(Long tenantId, Long roleId, boolean isLogicalConjunction) throws SystemException {
        try {
            TenantRoleResourceClient client = clientServiceUtil.getTenantResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getSpecific(tenantId, roleId, isLogicalConjunction);
            return TenantRoleModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (Exception e) {
            log.error("Error trying to create Tenant Role association", e);
            throw new SystemException(e);
        }
    }
}
