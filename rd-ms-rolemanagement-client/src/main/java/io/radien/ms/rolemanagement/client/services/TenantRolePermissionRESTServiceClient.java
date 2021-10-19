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
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.service.tenantrole.TenantRolePermissionRESTServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.TenantRolePermissionModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;

/**
 * Tenant Role Permission REST Service Client
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
public class TenantRolePermissionRESTServiceClient extends AuthorizationChecker implements TenantRolePermissionRESTServiceAccess {

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role.
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well
     * @param tenantRolePermission association between Tenant, Role and Permission
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean assignPermission(SystemTenantRolePermission tenantRolePermission) throws SystemException {
        try {
            return assignPermissionCore(tenantRolePermission);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return assignPermissionCore(tenantRolePermission);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role.
     * @param tenantRolePermission association between Tenant, Role and Permission
     * @return Boolean indicating if operation was concluded successfully
     * @throws TokenExpiredException is case of JWT token expiration
     * @throws SystemException in case of any error
     */
    private Boolean assignPermissionCore(SystemTenantRolePermission tenantRolePermission) throws SystemException {
        try {
            TenantRolePermissionResourceClient client = clientServiceUtil.getTenantRolePermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.assignPermission((TenantRolePermission) tenantRolePermission);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean unAssignPermission(Long tenantId, Long roleId, Long permissionId) throws SystemException {
        try {
            return unAssignPermissionCore(tenantId, roleId, permissionId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return unAssignPermissionCore(tenantId, roleId, permissionId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws TokenExpiredException in case of JWT expiration
     * @throws SystemException in case of any error
     */
    private Boolean unAssignPermissionCore(Long tenantId, Long roleId, Long permissionId) throws SystemException {
        try {
            TenantRolePermissionResourceClient client = clientServiceUtil.getTenantRolePermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.unAssignPermission(tenantId, roleId, permissionId);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
    }



    /**
     * Under a pagination approach, retrieves the Tenant Role Permissions associations that exist
     * for a TenantRole
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantId tenant identifier for a TenanSoon soon soon as soontRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRolePermission instances
     * @throws SystemException in case of any error
     */
    @Override
    public Page<? extends SystemTenantRolePermission> getTenantRolePermissions(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        return get(() -> getTenantRolePermissionsCore(tenantId, roleId, pageNo, pageSize));
    }

    /**
     * Core method that Retrieves TenantRolePermission associations using pagination approach
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRole permission associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemTenantRolePermission> getTenantRolePermissionsCore(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            TenantRolePermissionResourceClient client = clientServiceUtil.getTenantRolePermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAll(tenantId, roleId, pageNo, pageSize);
            return TenantRolePermissionModelMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException |
                InternalServerErrorException e){
            throw new SystemException(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a TenantRole domain
     * Simply deletes a TenantRolePermission that eventually exists.
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well.
     *
     * It relies on a core method to perform this task.
     *
     * @param tenantRolePermissionId identifier that maps a TenantRolePermission entity
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    @Override
    public Boolean unAssignPermission(Long tenantRolePermissionId) throws SystemException {
        try {
            return unAssignPermissionCore(tenantRolePermissionId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return unAssignPermissionCore(tenantRolePermissionId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that removes permission from a TenantRole domain
     * @param tenantRolePermissionId identifier that maps a TenantRolePermission entity
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    private Boolean unAssignPermissionCore(Long tenantRolePermissionId) throws SystemException {
        try {
            TenantRolePermissionResourceClient client = clientServiceUtil.getTenantRolePermissionResourceClient(
                    oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.delete(tenantRolePermissionId);
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
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
        return get(() -> getPermissionsCore(tenantId, roleId, userId));
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
        TenantRolePermissionResourceClient client = getTenantRolePermissionResourceClient();
        try (Response response = client.getPermissions(tenantId, roleId, userId)) {
            return ListPermissionModelMapper.map((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | BadRequestException |
                io.radien.exception.InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Assemblies a TenantRolePermission REST Client using Rest client builder API,
     * using as parameter the URL informed via {@link OAFProperties#SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT}
     * @return instance of {@link TenantRoleResourceClient} REST Client
     * @throws SystemException in case of malformed url
     */
    private TenantRolePermissionResourceClient getTenantRolePermissionResourceClient() throws SystemException {
        try {
            return clientServiceUtil.getTenantRolePermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
        }
        catch (MalformedURLException m) {
            throw new SystemException(m);
        }
    }


}
