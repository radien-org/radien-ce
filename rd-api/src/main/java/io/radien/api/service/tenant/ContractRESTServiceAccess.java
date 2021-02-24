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
package io.radien.api.service.tenant;

import io.radien.api.model.tenant.SystemContract;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Santana
 */
public interface ContractRESTServiceAccess {

    /**
     * Search for a contract with given name
     * @param name of the contract to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public List<? extends SystemContract> getContractByName(String name) throws Exception ;

    /**
     * Creates given contract
     * @param contract to be created
     * @return true in case of success
     */
    public boolean create(SystemContract contract) throws MalformedURLException;

    /**
     * Deletes given contract
     * @param contractId id of the contract to be deleted
     * @return true in case of success
     */
    public boolean delete(long contractId) throws MalformedURLException;

    /**
     * Updates given contract
     * @param contractId to be updated
     * @param contract information to update
     * @return true in case of success
     * @throws MalformedURLException
     */
    public boolean update(Long contractId, SystemContract contract) throws MalformedURLException;

    /**
     * Checks if contract is existent in the db
     * @param contractId to be found
     * @return true in case of success
     */
    public boolean isContractExistent(Long contractId) throws MalformedURLException, SystemException;
}
