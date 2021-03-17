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
import io.radien.api.model.permission.SystemResource;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * @author Newton Carvalho
 * Contract for Rest Service Client regarding Resource domain object
 */
public interface ResourceRESTServiceAccess extends Appframeable {

    /**
     * Fetches all resources
     * @param search value to be filtered
     * @param pageNo of the information to be checked
     * @param pageSize max page numbers for the necessary requested data
     * @param sortBy list of values to sort request
     * @param isAscending in case of true data will come ascending mode if false descending
     * @return list of resources
     * @throws MalformedURLException in case of URL exception construction
     * @throws ParseException in case of any issue parsing the response information
     */
    public Page<? extends SystemResource> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Retrieves a Resource by Id
     * @param id
     * @return
     * @throws SystemException
     */
    public Optional<SystemResource> getResourceById(Long id) throws SystemException;

    /**
     * Retrieves a Resource by Name
     * @param name
     * @return
     * @throws SystemException
     */
    public Optional<SystemResource> getResourceByName(String name) throws SystemException;

    /**
     * Creates given resource
     * @param resource to be created
     * @return true if resource has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemResource resource) throws SystemException;

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent resources.
     */
    public Long getTotalRecordsCount() throws SystemException;
}
