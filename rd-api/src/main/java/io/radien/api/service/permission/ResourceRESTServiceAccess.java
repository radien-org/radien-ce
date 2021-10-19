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
package io.radien.api.service.permission;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemResource;
import io.radien.exception.SystemException;

import java.util.List;
import java.util.Optional;

/**
 * Contract for Rest Service Client regarding Resource domain object
 *
 * @author Newton Carvalho
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
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Page<? extends SystemResource> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Retrieves a Resource by Id
     * @param id to be search for
     * @return the requested resource
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Optional<SystemResource> getResourceById(Long id) throws SystemException;

    /**
     * Retrieves a Resource by Name
     * @param name to be found
     * @return the requested resource
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Optional<SystemResource> getResourceByName(String name) throws SystemException;

    /**
     * Creates given resource
     * @param resource to be created
     * @return true if resource has been created with success or false if not
     * @throws SystemException in case of any communication/processing issue
     * regarding Resource Rest API
     */
    public boolean create(SystemResource resource) throws SystemException;

    /**
     * Updates a given resource
     * @param resource to be updated
     * @return true if resource has been updated with success or false if not
     * @throws SystemException in case of any communication/processing issue
     * regarding Resource Rest API
     */
    public boolean update(SystemResource resource) throws SystemException;

    /**
     * Deletes given resource
     * @param resourceId to be deleted
     * @return true if resource has been deleted with success or false if not
     * @throws SystemException in case of URL specification
     */
    public boolean delete(long resourceId) throws SystemException;

    /**
     * Retrieves from DB a collection containing resources. The retrieval process will be
     * based on a list containing identifiers
     * @param ids list containing resource identifiers
     * @return a list of resources found using the informed identifiers
     * @throws SystemException in case of any found error
     */
    public List<? extends SystemResource> getResourcesByIds(List<Long> ids) throws SystemException;

}
