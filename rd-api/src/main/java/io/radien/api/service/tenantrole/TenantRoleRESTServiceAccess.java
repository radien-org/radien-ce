package io.radien.api.service.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Rest service client responsible to Deal with TenantRole endpoint
 * @author Newton Carvalho
 */
public interface TenantRoleRESTServiceAccess {

    /**
     * Obtains TenantRole by Id
     * @param id TenantRole identifier
     * @return Optional containing TenantRole
     * @throws SystemException in case of any error
     */
    Optional<SystemTenantRole> getTenantRoleById(Long id) throws SystemException;

    /**
     * Create a TenantRole association
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return Boolean indicating if the operation was concluded with success.
     * @throws SystemException in case of any error
     */
    Boolean save(SystemTenantRole tenantRole) throws SystemException;

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true (if association exists), false otherwise.
     * @throws SystemException in case of any other error.
     */
    Boolean exists(Long tenantId, Long roleId) throws SystemException;

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions.
     * @throws SystemException in case of any error
     */
    List<? extends SystemPermission> getPermissions(Long tenantId, Long roleId, Long userId) throws SystemException;

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenants
     * @throws SystemException in case of any error
     */
    List<? extends SystemTenant> getTenants(Long userId, Long roleId) throws SystemException;

    /**
     * Retrieves TenantRole associations that met the following parameter
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns a Collection containing TenantRole associations.
     * @throws SystemException in case of Any error
     */
    List<? extends SystemTenantRole> getTenantRoles(Long tenantId, Long roleId, boolean isLogicalConjunction)
            throws SystemException;

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean assignUser(Long tenantId, Long roleId, Long userId) throws SystemException;

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean unassignUser(Long tenantId, Long roleId, Long userId) throws SystemException;

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean assignPermission(Long tenantId, Long roleId, Long permissionId) throws SystemException;

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean unassignPermission(Long tenantId, Long roleId, Long permissionId) throws SystemException;
}
