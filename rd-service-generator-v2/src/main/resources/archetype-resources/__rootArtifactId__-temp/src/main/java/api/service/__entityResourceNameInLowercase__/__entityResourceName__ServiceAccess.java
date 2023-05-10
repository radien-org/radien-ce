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

import io.radien.api.entity.Page;
import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.api.service.batch.BatchSummary;

import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.exception.${entityResourceName.toLowerCase()}.${entityResourceName}NotFoundException;
import java.util.List;
/**
 * ${entityResourceName} Service access interface class and all the possible requests
 *
 * @author Marco Weiland
 */
public interface ${entityResourceName}ServiceAccess extends ServiceAccess {

    /**
     * Returns all the requested and existent users in the db into a pagination mode
     * @param search field to be looked up for
     * @param pageNo where te user is
     * @param pageSize number of records per page
     * @param sortBy any type of column or field
     * @param isAscending if in case of true the records will come in ascending sorted
     * @return a page of requested users
     */
    public Page<System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * Requests the user information based on the user id
     * @param ${entityResourceName.toLowerCase()}Id to be found
     * @return the ${entityResourceName.toLowerCase()} object
     * @throws ${entityResourceName}NotFoundException in case no user is found with the given id
     */
    public System${entityResourceName} get(Long ${entityResourceName.toLowerCase()}Id) throws ${entityResourceName}NotFoundException;

    /**
     * Saves/Updates the requested information into the db
     * @param ${entityResourceName.toLowerCase()} information to be stored
     * @throws ${entityResourceName}NotFoundException in case of update and the user is not found
     * @throws UniquenessConstraintException in case of save and the record already exists or has duplicated fields
     */
    public void save(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws ${entityResourceName}NotFoundException, UniquenessConstraintException;

    /**
     * Deletes a requested user based on the received id
     * @param ${entityResourceName.toLowerCase()} to be deleted
     */
    public void delete(Long ${entityResourceName.toLowerCase()});

    /**
     * Count the number of all the ${entityResourceName.toLowerCase()}s existent in the DB.
     * @return the count of ${entityResourceName.toLowerCase()}s
     */
    public long getTotalRecordsCount();
}
