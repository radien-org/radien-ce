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
import io.radien.api.service.ServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * @author Santana
 * @author Bruno Gama
 */
public interface ContractServiceAccess extends ServiceAccess {

    /**
     * Gets the System Contract searching by the PK (id).
     *
     * @param contractId to be searched.
     * @return the system contract requested to be found.
     */
    public SystemContract get(Long contractId) throws UserNotFoundException;

    /**
     * Gets all the contracts.
     * Can be filtered by name
     *
     * @param name specific logon or user email
     * @return a List of system contracts.
     */
    public List<? extends SystemContract> get(String name);

    /**
     * Creates the requested and given Contract information into the DB.
     *
     * @param contract to be added
     * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
     */
    public void create(SystemContract contract) throws UniquenessConstraintException;

    /**
     * Updates the requested and given Contract information into the DB.
     *
     * @param contract to be updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    public void update(SystemContract contract) throws UniquenessConstraintException;

    /**
     * Deletes a unique contract selected by his id.
     *
     * @param contractId to be deleted.
     */
    public boolean delete(Long contractId);

    /**
     * Deletes a list of contracts selected by his id.
     *
     * @param contractIds to be deleted.
     */
    public void delete(Collection<Long> contractIds);

    /**
     * Validates if specific requested Contract exists
     * @param contractId to be searched
     * @return response true if it exists
     */
    public boolean exists(Long contractId) throws NotFoundException;
}
