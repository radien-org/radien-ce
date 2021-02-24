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
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Bruno Gama
 */
@RequestScoped
public class RoleBusinessService implements Serializable {

    private static final long serialVersionUID = -8256825318630144628L;

    private static final Logger log = LoggerFactory.getLogger(RoleBusinessService.class);

    @Inject
    private RoleServiceAccess roleServiceAccess;

    /**
     * Gets all the role information into a paginated mode and return those information to the user.
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
     * error.
     */
    public Page<? extends SystemRole> getAll(int pageNo, int pageSize) {
        return roleServiceAccess.getAll(pageNo, pageSize);
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
     * @return 200 code message if success, 404 if role is not found, 500 code message if there is any error.
     */
    public void delete(long id) throws RoleNotFoundException {
        roleServiceAccess.delete(id);
    }

    /**
     * Inserts the given role information, wither creates a new record or updated one already existent one, depending
     * if the given role has an id or not.
     *
     * @param role information to be update or created.
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 404 if role is not found, 500 code message if there is any error.
     */
    public void save(Role role) throws RoleNotFoundException, UniquenessConstraintException {
        roleServiceAccess.save(role);
    }
}
