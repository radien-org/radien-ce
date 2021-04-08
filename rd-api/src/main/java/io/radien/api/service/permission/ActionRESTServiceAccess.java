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
package io.radien.api.service.permission;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * @author Newton Carvalho
 * Contract for Rest Service Client regarding Action domain object
 */
public interface ActionRESTServiceAccess extends Appframeable {

    /**
     * Fetches all Actions
     * @param search field value to be searched or looked up
     * @param pageNo initial page number
     * @param pageSize max page size
     * @param sortBy sort by filter fields
     * @param isAscending ascending result list or descending
     * @return List of existent Actions
     * @throws MalformedURLException in case of URL specification
     * @throws ParseException in case of any issue when parsing the response
     * @throws SystemException in any other kind of system issue
     */
    public Page<? extends SystemAction> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Retrieves a Action by Id
     * @param id
     * @return
     * @throws SystemException
     */
    public Optional<SystemAction> getActionById(Long id) throws SystemException;

    /**
     * Retrieves a Action by Name
     * @param name
     * @return
     * @throws SystemException
     */
    public Optional<SystemAction> getActionByName(String name) throws SystemException;

    /**
     * Creates given action
     * @param action to be created
     * @return true if action has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemAction action) throws SystemException;

}
