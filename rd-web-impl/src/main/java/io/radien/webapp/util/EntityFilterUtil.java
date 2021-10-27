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
package io.radien.webapp.util;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.appendIfMissingIgnoreCase;

/**
 * JSF Bean that must encapsulate autocomplete filtering methods
 * Basically its an utility managed bean class that can be reused
 * whenever is necessary to handle autocomplete events
 */
@Model
@RequestScoped
public class EntityFilterUtil extends AbstractManager {

    @Inject
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Inject
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete component
     * @param name tenant name
     * @return list containing tenants
     */
    public List<? extends SystemTenant> filterTenantsByName(String name) {
        try {
            return this.tenantRESTServiceAccess.getAll(appendIfMissingIgnoreCase(name, "%").trim(),
                    1, 100, null, false).getResults();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.RETRIEVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANTS.getValue()));
            return new ArrayList<>();
        }
    }

    /**
     * Retrieve roles doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete component
     * @param name role name
     * @return list containing roles
     */
    public List<? extends SystemRole> filterRolesByName(String name) {
        try {
            return roleRESTServiceAccess.getAll(appendIfMissingIgnoreCase(name, "%").trim(),
                    1, 10, null, false).getResults();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.RETRIEVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ROLES_MESSAGE.getValue()));
            return new ArrayList<>();
        }
    }

    /**
     * Retrieve roles doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete component
     * @param name permission name
     * @return list containing permissions
     */
    public List<? extends SystemPermission> filterPermissionsByName(String name) {
        try {
            return this.permissionRESTServiceAccess.getAll(appendIfMissingIgnoreCase(name, "%").trim(),
                    1, 10, null, false).getResults();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.RETRIEVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.PERMISSIONS_MESSAGE.getValue()));
            return new ArrayList<>();
        }
    }
}
