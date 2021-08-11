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
package io.radien.webapp.user.tenant;

import io.radien.exception.SystemException;

import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;

import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.user.UserDataModel;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;

import javax.faces.application.FacesMessage;

import javax.inject.Inject;
/**
 * JSF manager bean that handles the unAssign operation
 * For a selected User of a corresponding Tenant
 *
 * @author Rajesh Gavvala
 */
@Model
@RequestScoped
public class UnAssignTenantUser extends AbstractManager {
    private static final long serialVersionUID = -4945758156817953873L;

    @Inject
    private UserDataModel userDataModel;

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    private boolean userSelectedAndHasUserAdminRoleAccess = false;

    /**
     * Performs unAssign operation for selected User of a Tenant
     * @return Home page if unAssignment operation is done on ActiveTenantUser or
     * Users page if unAssignment operation is done on Tenant User
     * @throws SystemException if any system error occurs
     */
    public String unAssignSelectedTenantUser() throws SystemException {
        try{
            if(userDataModel.getSelectedUser() != null) {
                tenantRoleRESTServiceAccess.unassignUser(activeTenantDataModelManager.getActiveTenant().getId(), null,
                        userDataModel.getSelectedUser().getId());

                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE_UNASSIGNED_SUCCESS.getValue()));
            }
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE.getValue()));
        }
        return setTenantUserFieldsAsNull();
    }

    /**
     * Sets mandatory validation boolean field(s) as false
     * Redirects to home page, if unAssigned User belongs to the ActiveTenant
     * @return Home page if unAssignment operation is done on ActiveTenantUser or
     * Users page if unAssignment operation is done on Tenant User
     */
    public String setTenantUserFieldsAsNull() {
        userSelectedAndHasUserAdminRoleAccess = false;
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Validates User selection and admin role access privileges of a User Tenant
     * @return boolean value true if a User selected to unAssign and
     * also the ActiveTenant User has admin Role access privileges
     */
    public boolean isUserSelectedAndHasUserAdminRoleAccess() {
        if(userDataModel.getSelectedUser() != null && userDataModel.getHasUserAdministratorRoleAccess()){
            userSelectedAndHasUserAdminRoleAccess = true;
        }
        return userSelectedAndHasUserAdminRoleAccess;
    }

    /**
     * Sets the boolean flag for a Tenant User selection and validates User
     * Admin Role access
     * @param userSelectedAndHasUserAdminRoleAccess boolean value to be set
     */
    public void setUserSelectedAndHasUserAdminRoleAccess(boolean userSelectedAndHasUserAdminRoleAccess) {
        this.userSelectedAndHasUserAdminRoleAccess = userSelectedAndHasUserAdminRoleAccess;
    }

}
