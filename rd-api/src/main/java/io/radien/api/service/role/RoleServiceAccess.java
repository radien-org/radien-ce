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
package io.radien.api.service.role;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import java.util.List;

/**
 * Role Service Access interface with all the possible requests for the roles
 *
 * @author Bruno Gama
 */
public interface RoleServiceAccess extends ServiceAccess {

    /**
     * Gets a specific role by searching for its id
     * @param roleId to be found
     * @return the requested system role
     * @throws RoleNotFoundException in case the requested id does not exist
     */
    public SystemRole get(Long roleId) throws RoleNotFoundException;

    /**
     * Gets all the role into a pagination mode.
     * @param search name description for some role
     * @param pageNo of the requested information. Where the role is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system roles.
     */
    public Page<SystemRole> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * By a given filter object will try to find all the system roles that match that requested filter
     * @param filter with fields to be found
     * @return a list of all the system oles that match
     */
    public List<? extends SystemRole> getSpecificRoles(SystemRoleSearchFilter filter);

    /**
     * Saves or stores the requested given role into the db
     * @param role to be save/stored
     * @throws RoleNotFoundException in case of update and the requested role does not exist
     * @throws UniquenessConstraintException in case of saving and the record already exists or has duplicated
     * information
     */
    public void save(SystemRole role) throws RoleNotFoundException, UniquenessConstraintException;

    /**
     * Deletes from the db the requested role
     * @param roleId to be deleted
     * @throws RoleNotFoundException in case the role could not be found
     */
    public void delete(Long roleId) throws RoleNotFoundException;

    /**
     * Validates if a certain specified role is existent
     * @param roleId to be found
     * @param name to be searched
     * @return true if it exists.
     */
    public boolean checkIfRolesExist(Long roleId, String name);

    /**
     * Count the number of all the roles existent in the DB.
     * @return the count of roles
     */
    public long getTotalRecordsCount();
}
