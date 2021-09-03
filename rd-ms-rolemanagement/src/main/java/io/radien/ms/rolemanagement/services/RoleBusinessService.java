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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.Role;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Role Business Service will request the service to validate the actions on the db
 *
 * @author Bruno Gama
 */
@RequestScoped
public class RoleBusinessService implements Serializable {

    private static final long serialVersionUID = -8256825318630144628L;

    @Inject
    private RoleServiceAccess roleServiceAccess;

    /**
     * Gets all the role information into a paginated mode and return those information to the user.
     * In case of omitted (empty) search parameter retrieves ALL roles
     * @param search search parameter for matching roles (optional).
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     */
    public Page<? extends SystemRole> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        return roleServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending);
    }

    /**
     * Retrieve all the information which has a specific name or description.
     * @return a paginated response with the requested information. 200 code message if success, 500 code message
     * if there is any error.
     */
    public List<? extends SystemRole> getSpecificRoles(SystemRoleSearchFilter filter) {
        return roleServiceAccess.getSpecificRoles(filter);
    }

    /**
     *  Gets the information of a role which will be found using the id.
     *
     * @param id to be searched for
     * @return a paginated response with the requested information. 200 code message if success,
     * 404 if role is not found, 500 code message if there is any error.
     */
    public SystemRole getById(Long id) throws RoleNotFoundException {
        return roleServiceAccess.get(id);
    }

    /**
     * Delete request which will delete the given id role information
     *
     * @param id record to be deleted
     */
    public void delete(long id) throws RoleNotFoundException {
        roleServiceAccess.delete(id);
    }

    /**
     * Inserts the given role information, wither creates a new record or updated one already existent one, depending
     * if the given role has an id or not.
     *
     * @param role information to be update or created.
     */
    public void save(Role role) throws RoleNotFoundException, UniquenessConstraintException {
        roleServiceAccess.save(role);
    }

    /**
     * Validates if specific requested role exists by id or name
     * @param id to be searched
     * @param name to be searched
     * @return true in case role does exist, false if not
     */
    public boolean exists(Long id, String name) {
        return roleServiceAccess.checkIfRolesExist(id, name);
    }

    /**
     * Will count how many roles are existent in the db
     * @return a count of all the roles
     */
    public long getTotalRecordsCount() {
        return roleServiceAccess.getTotalRecordsCount();
    }
}
