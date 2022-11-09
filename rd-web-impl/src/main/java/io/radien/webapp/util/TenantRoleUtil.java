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

import io.radien.api.SystemVariables;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.api.model.tenantrole.exception.TenantRoleIllegalArgumentException;
import io.radien.webapp.AbstractManager;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

/**
 * JSF Bean that must encapsulate common operations regarding
 * TenantRole domain (what includes TenantRole, TenantRolePermission,
 * TenantRoleUser and so on) allowing reducing code duplication
 */
@Model
@RequestScoped
public class TenantRoleUtil extends AbstractManager {

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    /**
     * Given a tenant and a role, retrieves the id that exists for that relationship
     * @param tenantId tenant identifier (mandatory)
     * @param roleId role identifier (mandatory)
     * @return Id (key) that must exist for a Tenant Role association
     * @throws SystemException in case of any error regarding communication with TenantRole endpoint
     * @throws NotFoundException if association between tenant and role does not exist
     * @throws TenantRoleIllegalArgumentException in case of not informed mandatory params
     */
    public Long getTenantRoleId(Long tenantId, Long roleId) throws SystemException, TenantRoleIllegalArgumentException {
        if (tenantId == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }
        if (roleId == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_ID.getLabel()));
        }
        return tenantRoleRESTServiceAccess.getIdByTenantRole(tenantId, roleId).
                orElseThrow(() -> new NotFoundException(GenericErrorCodeMessage.TENANT_ROLE_ASSOCIATION_TENANT_ROLE.
                        toString(String.valueOf(tenantId), String.valueOf(roleId))));
    }


}
