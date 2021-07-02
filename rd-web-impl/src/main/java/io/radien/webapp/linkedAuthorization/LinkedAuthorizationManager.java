package io.radien.webapp.linkedAuthorization;

import io.radien.api.SystemVariables;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author Newton Carvalho
 */
@Model
@RequestScoped
public class LinkedAuthorizationManager extends AbstractManager {

    @Inject
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Inject
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Inject
    private UserRESTServiceAccess userRESTServiceAccess;

    @Inject
    private LinkedAuthorizationRESTServiceAccess linkedAuthorizationRESTServiceAccess;

    protected SystemLinkedAuthorization linkedAuthorization = new LinkedAuthorization();

    protected SystemPermission selectedPermission = new Permission();
    protected SystemRole selectedRole = new Role();
    protected SystemTenant selectedTenant = new Tenant();
    protected SystemUser selectedUser = new User();

    public String save(SystemLinkedAuthorization l) {
        try {
            this.selectedUser = this.userRESTServiceAccess.getUserByLogon(
                    this.selectedUser.getLogon()).orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                    "rd_user_not_found"), selectedUser.getLogon())));

            l.setTenantId(this.selectedTenant.getId());
            l.setRoleId(this.selectedRole.getId());
            l.setPermissionId(this.selectedPermission.getId());
            l.setUserId(this.selectedUser.getId());

            this.linkedAuthorizationRESTServiceAccess.create(l);

            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_linkedauthorization"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_linkedauthorization"));
        }
        return "linkedauthorization";
    }

    public String edit(SystemLinkedAuthorization l) {
        this.setLinkedAuthorization(l);
        try {
            this.selectedPermission = this.permissionRESTServiceAccess.getPermissionById(l.getPermissionId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_permission_not_found"), l.getPermissionId())));

            this.selectedRole = this.roleRESTServiceAccess.getRoleById(l.getRoleId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                    "rd_role_not_found"), l.getRoleId())));

            this.selectedTenant = this.tenantRESTServiceAccess.getTenantById(l.getTenantId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_tenant_not_found"), l.getTenantId())));

            this.selectedUser = this.userRESTServiceAccess.getUserById(l.getRoleId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_user_not_found"), l.getUserId())));
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_linkedauthorization"));
        }
        return "linkedauthorization";
    }

    protected String prepareFilterParam(String filter) {
        if (StringUtils.isEmpty(filter)) {
            return null;
        }
        if (!filter.endsWith("%")) {
            filter += "%";
        }
        return filter;
    }

    public List<? extends SystemTenant> filterTenantsByName(String name) throws SystemException{
        return this.tenantRESTServiceAccess.getAll(prepareFilterParam(name),
                1, 10, null, false).getResults();
    }

    public List<? extends SystemPermission> filterPermissionsByName(String name) throws SystemException{
        return this.permissionRESTServiceAccess.getAll(prepareFilterParam(name),
                1, 10, null, false).getResults();
    }

    public List<? extends SystemRole> filterRolesByName(String name) throws SystemException{
        return this.roleRESTServiceAccess.getAll(prepareFilterParam(name),
                1, 10, null, false).getResults();
    }

    public SystemLinkedAuthorization getLinkedAuthorization() {
        return linkedAuthorization;
    }

    public void setLinkedAuthorization(SystemLinkedAuthorization linkedAuthorization) {
        this.linkedAuthorization = linkedAuthorization;
    }

    public SystemPermission getSelectedPermission() {
        return selectedPermission;
    }

    public void setSelectedPermission(SystemPermission selectedPermission) {
        this.selectedPermission = selectedPermission;
    }

    public SystemRole getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(SystemRole selectedRole) {
        this.selectedRole = selectedRole;
    }

    public SystemTenant getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(SystemTenant selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getLogon(Long userId) {
        try {
            return this.userRESTServiceAccess.getUserById(userId).orElseThrow(() -> new SystemException(
                    MessageFormat.format(JSFUtil.getMessage("rd_user_not_found"), userId))).
                    getLogon();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_user"));
            return null;
        }
    }

    public String getTenantName(Long tenantId) {
        try {
            return this.tenantRESTServiceAccess.getTenantById(tenantId).orElseThrow(() -> new SystemException(
                    MessageFormat.format(JSFUtil.getMessage("rd_tenant_not_found"), tenantId))).
                    getName();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_tenant"));
            return null;
        }
    }

    public String getRoleName(Long roleId) {
        try {
            return this.roleRESTServiceAccess.getRoleById(roleId).orElseThrow(() -> new SystemException(
                    MessageFormat.format(JSFUtil.getMessage("rd_role_not_found"), roleId))).
                    getName();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_role"));
            return null;
        }
    }

    public String getPermissionName(Long permissionId) {
        try {
            return this.permissionRESTServiceAccess.getPermissionById(permissionId).orElseThrow(() -> new SystemException(
                    MessageFormat.format(JSFUtil.getMessage("rd_permission_not_found"), permissionId))).
                    getName();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_permission"));
            return null;
        }
    }
}
