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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Bruno Gama
 */
@RequestScoped
public class LinkedAuthorizationBusinessService implements Serializable {

    private static final long serialVersionUID = 9136267725285788804L;

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationBusinessService.class);

    @Inject
    private LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Inject
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Inject
    private RoleServiceAccess roleServiceAccess;

    /**
     * Searches the Linked Authorization association based on the id
     * @param associationId to be searched
     * @return the System Linked Authorization object searched
     * @throws LinkedAuthorizationNotFoundException if object does not exist
     */
    public SystemLinkedAuthorization getAssociationById(Long associationId) throws LinkedAuthorizationNotFoundException {
        return linkedAuthorizationServiceAccess.getAssociationById(associationId);
    }

    /**
     * Gets all the linked authorization objects
     * @param pageNo where the user will begin
     * @param pageSize number of max pages
     * @return a paged of System Linked Authorization
     */
    public Page<SystemLinkedAuthorization> getAll(int pageNo, int pageSize) {
        return linkedAuthorizationServiceAccess.getAll(pageNo, pageSize);
    }

    /**
     * Searches for specific Linked authorizations
     * @param filter to be searched
     * @return a list of System Linked Authorization
     */
    public List<? extends SystemLinkedAuthorization> getSpecificAssociation(SystemLinkedAuthorizationSearchFilter filter) {
        return linkedAuthorizationServiceAccess.getSpecificAssociation(filter);
    }

    /**
     * Saves a specific association
     * @param association information to be saved
     * @throws LinkedAuthorizationNotFoundException if object does not exist
     * @throws UniquenessConstraintException if there are duplicated fields
     */
    public void save(SystemLinkedAuthorization association) throws Exception {
        if(checkIfFieldsAreValid(association)) {
            linkedAuthorizationServiceAccess.save(association);
        }
    }

    /**
     * Validates if inserted values are correct to make the association. Validates if roles, tenants and permissions exist.
     * @param association to be saved
     * @return true if everything is ok to be saved
     */
    public boolean checkIfFieldsAreValid(SystemLinkedAuthorization association) throws MalformedURLException, SystemException {
        boolean isTenantExistent = tenantRESTServiceAccess.isTenantExistent(association.getTenantId());
        boolean isPermissionExistent = permissionRESTServiceAccess.isPermissionExistent(association.getPermissionId());
        boolean isRoleExistent = checkIfRoleExists(association.getRoleId());

        if(!isTenantExistent ||
                !isPermissionExistent ||
                !isRoleExistent) {
            return false;
        }
        return true;
    }

    /**
     * Validates if specific role exists
     * @param roleId to be searched
     * @return true if it found the role
     */
    public boolean checkIfRoleExists(Long roleId) {
        if(roleServiceAccess.checkIfRolesExist(roleId)) {
            return true;
        }
        return false;
    }

    /**
     * Deletes specified linked authorization
     * @param associationId to be deleted
     * @throws LinkedAuthorizationNotFoundException if object does not exist
     */
    public void deleteAssociation(Long associationId) throws LinkedAuthorizationNotFoundException {
        linkedAuthorizationServiceAccess.deleteAssociation(associationId);
    }

}
