/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.api.service.tenant;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemContract;
import io.radien.exception.SystemException;

import java.util.List;

/**
 * Contract REST service access interface and requested actions
 *
 * @author Santana
 */
public interface ContractRESTServiceAccess {

    /**
     * Search for a contract with given name
     * @param name of the contract to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public List<? extends SystemContract> getContractByName(String name) throws SystemException ;

    /**
     * Fetches all Contracts
     * @param pageNo of the data to be visualized
     * @param pageSize is the max size of pages regarding the existent data to be checked
     * @return List of System Contracts
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Page<? extends SystemContract> getAll(int pageNo, int pageSize) throws SystemException;

    /**
     * Creates given contract
     * @param contract to be created
     * @return true in case of success
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean create(SystemContract contract) throws SystemException;

    /**
     * Deletes given contract
     * @param contractId id of the contract to be deleted
     * @return true in case of success
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean delete(Long contractId) throws SystemException;

    /**
     * Updates given contract
     * @param contractId to be updated
     * @param contract information to update
     * @return true in case of success
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean update(Long contractId, SystemContract contract) throws SystemException;

    /**
     * Checks if contract is existent in the db
     * @param contractId to be found
     * @return true in case of success
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean isContractExistent(Long contractId) throws SystemException;

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent contracts.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Long getTotalRecordsCount() throws SystemException;
}
