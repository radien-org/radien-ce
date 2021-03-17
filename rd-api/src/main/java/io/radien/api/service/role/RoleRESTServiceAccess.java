/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Bruno Gama
 */
public interface RoleRESTServiceAccess extends Appframeable{

    /**
     * Requests all existent roles
     * @param pageNo to be show the information
     * @param pageSize number of max pages of information
     * @return list of system roles
     * @throws SystemException in case of any error
     */
    public Page<? extends SystemRole> getAll(int pageNo, int pageSize) throws SystemException;

    /**
     * Search for a role with given id
     * @param id of the role to be retrieved
     * @return Optional containing (or not) one role
     * @throws Exception in case of any trouble during the retrieving process
     */
    public Optional<SystemRole> getRoleById(Long id) throws Exception ;

    /**
     * Requests find of role by role description
     * @param description to be found
     * @return list of system roles
     * @throws SystemException in case of any error
     */
    public List<? extends SystemRole> getRolesByDescription(String description) throws SystemException ;

    /**
     * Creates given role
     * @param role to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemRole role) throws SystemException;

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent roles.
     */
    public Long getTotalRecordsCount() throws SystemException;
}
