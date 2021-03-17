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
package io.radien.api.service.role;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import java.util.List;

/**
 * @author Bruno Gama
 */
public interface RoleServiceAccess extends ServiceAccess {

    public SystemRole get(Long roleId) throws RoleNotFoundException;

    /**
     * Gets all the role into a pagination mode.
     * @param pageNo of the requested information. Where the user is.
     * @param pageSize total number of pages returned in the request.
     * @return a page of system roles.
     */
    public Page<SystemRole> getAll(int pageNo, int pageSize);

    public List<? extends SystemRole> getSpecificRoles(SystemRoleSearchFilter filter);

    public void save(SystemRole role) throws RoleNotFoundException, UniquenessConstraintException;

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
