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
import io.radien.exception.UserNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Gama
 */
public interface RoleServiceAccess extends ServiceAccess {

    // TODO: Bruno Gama - Handle exceptions
    // TODO: getAll

    public SystemRole get(Long roleId) throws RoleNotFoundException;

    public Page<SystemRole> getAll(int pageNo, int pageSize);

    public List<? extends SystemRole> getSpecificRoles(SystemRoleSearchFilter filter);

    public void save(SystemRole role) throws RoleNotFoundException, UniquenessConstraintException ;

    public void delete(Long roleId) throws RoleNotFoundException;
}