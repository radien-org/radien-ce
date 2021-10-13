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
package io.radien.api.service.${entityResourceName.toLowerCase()};

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};

import io.radien.exception.SystemException;

import java.net.MalformedURLException;

import java.util.List;
import java.util.Optional;
/**
 * ${entityResourceName} REST Service Access interface for future requests
 *
 * @author Marco Weiland
 */
public interface ${entityResourceName}RESTServiceAccess extends Appframeable{

    /**
     * Returns all the existent System Users into a pagination format
     * @param search in case there should only be returned a specific type of users
     * @param pageNo where the user currently is
     * @param pageSize number of records to be show by page
     * @param sortBy any specific column
     * @param isAscending true in case records should be filter in ascending order
     * @return a page of all the requested system users
     * @throws MalformedURLException in case of any issue while attempting communication with the client side
     */
    public Page<? extends System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException, SystemException;

    /**
     * Gets the requested user searching for his id
     * @param id to be searched
     * @return a optional of system user if found
     * @throws SystemException in case of token expiration or any issue on the application
     */
	public Optional<System${entityResourceName}> get${entityResourceName}ById(Long id) throws SystemException;


    /**
     * Creates given user
     * @param ${entityResourceName.toLowerCase()} to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean save(System${entityResourceName} testDemo) throws SystemException;

    /**
     * Deletes the requested user from the db
     * @param id of the user to be deleted
     * @return true in case of deletion
     */
    public boolean delete${entityResourceName}(long id) throws SystemException;

    /**
     * Updates the given user information, will validate by the given user id since that one cannot change
     * @param ${entityResourceName.toLowerCase()} information to be updated
     * @return true in case of success
     */
    public boolean update${entityResourceName}(System${entityResourceName} ${entityResourceName.toLowerCase()});


    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent ${entityResourceName.toLowerCase()}s.
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public Long getTotalRecordsCount() throws SystemException;
}
