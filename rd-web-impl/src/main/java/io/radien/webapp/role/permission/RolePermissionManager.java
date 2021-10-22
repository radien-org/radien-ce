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
package io.radien.webapp.role.permission;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.tenantrole.TenantRolePermissionRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.services.TenantRoleFactory;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.util.TenantRoleUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import org.primefaces.event.ToggleEvent;
/**
 * JSF Manager bean that constructs an association of assignable/unassigned
 * Permission(s) for the Role and the active User Tenant
 *
 * @author Rajesh Gavvala
 */
@Model
@SessionScoped
public class RolePermissionManager extends AbstractManager implements Serializable {
    private static final long serialVersionUID = -2843128450554819276L;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Inject
    private TenantRolePermissionRESTServiceAccess tenantRolePermissionRESTServiceAccess;

    @Inject
    private TenantRoleUtil tenantRoleUtil;

    private SystemRole systemRole;
    private SystemActiveTenant systemActiveTenant;

    private List<Long> systemPermissionsIdsList;
    private Map<Long, Boolean> isPermissionsAssigned = new HashMap<>();

    private Set<Long> assignableRolePermissions = new HashSet<>();
    private Set<Long> unassignedRolePermissions = new HashSet<>();

    private boolean isPermissionAssigned;
    private boolean isPermissionUnassigned;

    /**
     * Persists change of SystemRole object from the client and constricts
     * permissions for the corresponding Role
     * @param rowToggleSystemRole SystemRole object
     * @throws SystemException if any error
     */
    public void onRowExpand(ToggleEvent rowToggleSystemRole) throws SystemException {
        try{
            systemRole = (SystemRole) rowToggleSystemRole.getData();
            if(systemRole != null){
                isPermissionsAssigned.clear();
                loadRolePermissions(systemRole);
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.ON_ROW_ROLE_EXPAND_ERROR.getValue()));
        }
    }

    /**
     * This method retrieves the default assigned Permission(s) for the Role
     * that corresponds to the active user tenant
     * @throws SystemException is thrown when error occurs
     */
    public void loadRolePermissions(SystemRole systemRole) throws SystemException {
        if (getSystemActiveTenant() == null) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.TRP_NO_ACTIVE_TENANT.getValue()));
            return;
        }
        try{
            systemPermissionsIdsList = tenantRoleRESTServiceAccess.getPermissions(
                    getSystemActiveTenant().getTenantId(), systemRole.getId(), null).stream().
                    map(SystemPermission::getId).collect(Collectors.toList());

            if (!systemPermissionsIdsList.isEmpty()) {
                for (Long permissionId : systemPermissionsIdsList) {
                    isPermissionsAssigned.put(permissionId, true);
                }
            }

        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()));
        }
    }

    /**
     * Persists the change of role permission from the client that is to be assignable or unassigned
     * @param isAssignableOrUnassignedPermission state of boolean value for the assignable(newValue) or
     *                                           unassigned(oldValue)
     */
    public void isAssignableOrUnassignedPermission(ValueChangeEvent isAssignableOrUnassignedPermission) {
        if(isAssignableOrUnassignedPermission.getOldValue() != null ){
           isPermissionUnassigned = (boolean) isAssignableOrUnassignedPermission.getOldValue();
        }
        if(isAssignableOrUnassignedPermission.getNewValue() != null){
            isPermissionAssigned = (boolean) isAssignableOrUnassignedPermission.getNewValue();
        }
    }

    /**
     * Prepares set of ids for assignable or unassigned permission(s) for the
     * active user tenant
     * @param systemPermission persists from the client
     */
    public void isAssignableOrUnassignedRolePermission(SystemPermission systemPermission){
        if(isPermissionUnassigned){
            unassignedRolePermissions.add(systemPermission.getId());
            if(!assignableRolePermissions.isEmpty()){
                assignableRolePermissions.remove(systemPermission.getId());
            }
        }

        if(isPermissionAssigned){
            assignableRolePermissions.add(systemPermission.getId());
            if(!unassignedRolePermissions.isEmpty()){
                unassignedRolePermissions.remove(systemPermission.getId());
            }
        }
    }

    /**
     * This Method gathers unique set of assignable/unassailable permissions
     * for an active user tenant. Validates & creates tenant role permissions
     * @return working HTML page of roles
     */
    public String assignOrUnassignedPermissionsToActiveUserTenant() {
        if (getSystemActiveTenant() == null) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.TRP_NO_ACTIVE_TENANT.getValue()));
            return DataModelEnum.ROLE_MAIN_PAGE.getValue();
        }
        if(assignableRolePermissions != null && !assignableRolePermissions.isEmpty()) {
            doAssignedPermissionsForRole();
        }
        if(unassignedRolePermissions != null && !unassignedRolePermissions.isEmpty()) {
            doUnassignedPermissionsForRole();
        }
        return DataModelEnum.ROLE_MAIN_PAGE.getValue();
    }

    /**
     * This method validates & creates association of
     * User tenant role permission(s)
     */
    protected void doAssignedPermissionsForRole() {
        try {
            for(Long systemPermissionId : assignableRolePermissions) {
                if (!systemPermissionsIdsList.contains(systemPermissionId)) {
                    if (!tenantRoleRESTServiceAccess.exists(getSystemActiveTenant().getTenantId(), systemRole.getId())) {
                        SystemTenantRole str = TenantRoleFactory.create(getSystemActiveTenant().getTenantId(),
                                systemRole.getId(), getSystemActiveTenant().getUserId());
                        tenantRoleRESTServiceAccess.save(str);
                    }
                    TenantRolePermission tenantRolePermission = new TenantRolePermission();
                    tenantRolePermission.setTenantRoleId(tenantRoleUtil.getTenantRoleId(getSystemActiveTenant().getTenantId(),
                            systemRole.getId()));
                    tenantRolePermission.setPermissionId(systemPermissionId);
                    tenantRolePermission.setCreateDate(new Date());
                    tenantRolePermission.setCreateUser(getSystemActiveTenant().getUserId());
                    tenantRolePermissionRESTServiceAccess.assignPermission(tenantRolePermission);
                }
            }
            assignableRolePermissions.clear();
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.USER_ACTIVE_TENANT_ROLE_PERMISSION_ASSIGNED_SUCCESS.getValue()));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(JSFUtil.getMessage(DataModelEnum.USER_ACTIVE_TENANT_ROLE_PERMISSION_ASSIGNED_ERROR.getValue())));
        }
    }

    /**
     * This method  disassociation of
     * User tenant role permission(s)
     */
    protected void doUnassignedPermissionsForRole() {
        try {
            List<Long> permissionsToUnSign = systemPermissionsIdsList.stream().
                    filter(p -> unassignedRolePermissions.contains(p)).collect(Collectors.toList());
            for(Long permissionId:permissionsToUnSign) {
                tenantRolePermissionRESTServiceAccess.unAssignPermission(getSystemActiveTenant().getTenantId(),
                        getSystemRole().getId(), permissionId);
            }
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.USER_ACTIVE_TENANT_ROLE_PERMISSION_UNASSIGNED_SUCCESS.getValue()));
            unassignedRolePermissions.clear();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage(JSFUtil.getMessage(DataModelEnum.USER_ACTIVE_TENANT_ROLE_PERMISSION_UNASSIGNED_ERROR.getValue())));
        }
    }

    /**
     * Gets default assigned mapped permissions
     * @return isPermissionsAssigned object with mapped
     * key, values of id and boolean value
     */
    public Map<Long, Boolean> getIsPermissionsAssigned() {
        return isPermissionsAssigned;
    }

    /**
     * Setter for the default assigned permission(s) object
     * @param isPermissionsAssigned permission(s) mapped object
     */
    public void setIsPermissionsAssigned(Map<Long, Boolean> isPermissionsAssigned) {
        this.isPermissionsAssigned = isPermissionsAssigned;
    }

    /**
     * Gets isPermissionAssigned boolean flag
     * @return true if permission assigned otherwise false
     */
    public boolean isPermissionAssigned() {
        return isPermissionAssigned;
    }

    /**
     * Sets if permissionAssigned for the SystemRole
     * @param permissionAssigned boolean flag
     */
    public void setPermissionAssigned(boolean permissionAssigned) {
        isPermissionAssigned = permissionAssigned;
    }

    /**
     * Gets isPermissionUnassigned boolean flag
     * @return true if permission unassigned otherwise false
     */
    public boolean isPermissionUnassigned() {
        return isPermissionUnassigned;
    }

    /**
     * Sets if permissionUnassigned for the SystemRole
     * @param permissionUnassigned boolean flag
     */
    public void setPermissionUnassigned(boolean permissionUnassigned) {
        isPermissionUnassigned = permissionUnassigned;
    }

    /**
     * Gets set of assignable permission id(s) for the
     * selected role
     * @return assignableRolePermissions
     */
    public Set<Long> getAssignableRolePermissions() {
        return assignableRolePermissions;
    }

    /**
     * Sets unique list of  assignable permission id(s) if exists for the Role
     * @param assignableRolePermissions set of id(s)
     */
    public void setAssignableRolePermissions(Set<Long> assignableRolePermissions) {
        this.assignableRolePermissions = assignableRolePermissions;
    }

    /**
     * Gets set of Un assignable permission id(s) for the
     * selected role
     * @return unassignedRolePermissions
     */
    public Set<Long> getUnassignedRolePermissions() {
        return unassignedRolePermissions;
    }

    /**
     * Sets unique list of un assignable permission id(s) if exists for the Role
     * @param unassignedRolePermissions set of id(s)
     */
    public void setUnassignedRolePermissions(Set<Long> unassignedRolePermissions) {
        this.unassignedRolePermissions = unassignedRolePermissions;
    }

    /**
     * Get SystemRole object
     * @return systemRole
     */
    public SystemRole getSystemRole() {
        return systemRole;
    }

    /**
     * Setter for the systemRole
     * @param systemRole object
     */
    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    /**
     * Gets list of permissions ids for the selected SystemRole
     * @return systemPermissionsIdsList
     */
    public List<Long> getSystemPermissionsIdsList() {
        return systemPermissionsIdsList;
    }

    /**
     * Setter for the list of permissions ids
     * @param systemPermissionsIdsList object
     */
    public void setSystemPermissionsIdsList(List<Long> systemPermissionsIdsList) {
        this.systemPermissionsIdsList = systemPermissionsIdsList;
    }

    /**
     * Gets SystemActiveTenant object information
     * @return systemActiveTenant
     */
    public SystemActiveTenant getSystemActiveTenant() {
        if(activeTenantDataModelManager.getActiveTenant() != null){
            systemActiveTenant = activeTenantDataModelManager.getActiveTenant();
        }
        return systemActiveTenant;
    }

    /**
     * Setter for the SystemActiveTenant
     * @param systemActiveTenant info
     */
    public void setSystemActiveTenant(SystemActiveTenant systemActiveTenant) {
        this.systemActiveTenant = systemActiveTenant;
    }

    /**
     * This method refresh/clears
     * assignable/unassigned role Permissions
     */
    public void refresh(){
        if(!assignableRolePermissions.isEmpty()){
            assignableRolePermissions.clear();
        }
        if(!unassignedRolePermissions.isEmpty()){
            unassignedRolePermissions.clear();
        }
    }
}
