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
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.service.tenantrole.TenantRolePermissionRESTServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
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
        return get(this::assignPermissionCore, tenantRolePermission);
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
        TenantRolePermissionResourceClient client = getClient();
        try (Response response = client.assignPermission((TenantRolePermission) tenantRolePermission)){
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException | BadRequestException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Updates a TenantRolePermission previously crated (When a permission was assigned into a TenantRole)
     * To perform the action above, It will invoke the equivalent core method counterpart, and
     * will handle Token Expiration error as well.
     * @param tenantRolePermission association between Tenant, Role and Permission
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    public Boolean update(SystemTenantRolePermission tenantRolePermission) throws SystemException {
        return this.get(this::updateCore, tenantRolePermission);
    }

    /**
     * Core method that updates the TenantRolePermission
     * @param tenantRolePermission association between Tenant, Role and Permission
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    private Boolean updateCore(SystemTenantRolePermission tenantRolePermission) throws SystemException{
        TenantRolePermissionResourceClient client = getClient();
        try (Response response = client.update(tenantRolePermission.getId(), (TenantRolePermission)tenantRolePermission)) {
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ProcessingException | ExtensionException | BadRequestException |
                NotFoundException | InternalServerErrorException e) {
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
        return get(()-> unAssignPermissionCore(tenantId, roleId, permissionId));
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
        TenantRolePermissionResourceClient client = getClient();
        try (Response response = client.unAssignPermission(tenantId, roleId, permissionId)){
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException | BadRequestException e) {
            throw new SystemException(e);
        }
    }



    /**
     * Under a pagination approach, retrieves the Tenant Role Permissions associations that currently exist
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantRoleId tenant role identifier(Acting as filter)
     * @param permissionId permission identifier (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return Page containing TenantRolePermission instances
     * @throws SystemException in case of any error
     */
    @Override
    public Page<? extends SystemTenantRolePermission> getAll(Long tenantRoleId, Long permissionId,
                                                             int pageNo, int pageSize,
                                                             List<String> sortBy, boolean isAscending) throws SystemException {
        return get(() -> getAllCore(tenantRoleId, permissionId, pageNo, pageSize, sortBy, isAscending));
    }

    /**
     * Core method that Retrieves TenantRolePermission associations using pagination approach
     * @param tenantRoleId tenant role identifier(Acting as filter)
     * @param permissionId permission identifier (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return Page containing TenantRole permission associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemTenantRolePermission> getAllCore(Long tenantRoleId, Long permissionId,
                                                                    int pageNo, int pageSize,
                                                                    List<String> sortBy, boolean isAscending) throws SystemException {
        TenantRolePermissionResourceClient client = getClient();
        try (Response response = client.getAll(tenantRoleId, permissionId, pageNo, pageSize, sortBy, isAscending)){
            return TenantRolePermissionModelMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException e){
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves TenantRolePermission associations that met the following parameter
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantRoleId TenantRole identifier
     * @param permissionId Permission identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRolePermission associations.
     * @throws SystemException in case of Any error
     */
    @Override
    public List<? extends SystemTenantRolePermission> getTenantRolePermissions(Long tenantRoleId, Long permissionId,
                                                                               boolean isLogicalConjunction) throws SystemException {
        return get(() -> getTenantRolePermissionsCore(tenantRoleId, permissionId, isLogicalConjunction));
    }

    /**
     * Retrieves TenantRolePermission associations that met the following parameter
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantRoleId Tenant identifier
     * @param permissionId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRolePermission associations.
     * @throws SystemException in case of Any error
     */
    private List<? extends SystemTenantRolePermission> getTenantRolePermissionsCore(Long tenantRoleId, Long permissionId,
                                                                                    boolean isLogicalConjunction) throws SystemException {
        TenantRolePermissionResourceClient client = getClient();
        try (Response response = client.getSpecific(tenantRoleId, permissionId, isLogicalConjunction)) {
            return TenantRolePermissionModelMapper.mapList((InputStream) response.getEntity());
        }
        catch(ProcessingException | ExtensionException | InternalServerErrorException e) {
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
        return get(this::unAssignPermissionCore, tenantRolePermissionId);
    }

    /**
     * Core method that removes permission from a TenantRole domain
     * @param tenantRolePermissionId identifier that maps a TenantRolePermission entity
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    private Boolean unAssignPermissionCore(Long tenantRolePermissionId) throws SystemException {
        TenantRolePermissionResourceClient client = getClient();
        try (Response response = client.delete(tenantRolePermissionId)) {
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | BadRequestException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Assemblies a {@link TenantRoleResourceClient} instance using RestClientBuilder Microprofile API
     * @return instance of {@link TenantRoleResourceClient}
     * @throws SystemException in case of Malformed URL
     */
    private TenantRolePermissionResourceClient getClient() throws SystemException {
        try {
            return clientServiceUtil.getTenantRolePermissionResourceClient(oaf.getProperty(
                    OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
    }
}
