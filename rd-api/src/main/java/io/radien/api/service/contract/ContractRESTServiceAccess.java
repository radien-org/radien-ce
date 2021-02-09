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
package io.radien.api.service.contract;

import io.radien.api.Appframeable;
import io.radien.api.model.contract.SystemContract;
import io.radien.api.model.user.SystemUser;

import javax.ws.rs.ProcessingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * @author Santana
 *
 */
public interface ContractRESTServiceAccess {

    /**
     * Search for a contract with given name
     * @param name of the contract to be retrieved
     * @return true if contract has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public Optional<SystemContract> getContractByName(String name) throws MalformedURLException, ParseException, Exception ;

    /**
     * Fetches all Contracts
     * @return List of Contracts
     */
    public List<? extends SystemContract> getAll() throws MalformedURLException, ParseException;

    /**
     * Creates given contract
     * @param contract to be created
     * @return true if contract has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemContract contract) throws MalformedURLException;

    /**
     * deletes given contract
     * @param contractId id of the contract to be deleted
     * @return true if contract has been deleted with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean delete(long contractId) throws MalformedURLException;

    /**
     * updates given contract
     * @param contract to be updated
     * @return true if contract has been updated with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
     public boolean update(SystemContract contract) throws MalformedURLException;
}