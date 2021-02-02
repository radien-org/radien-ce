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

import io.radien.api.model.contract.SystemContract;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * @author Santana
 */
public interface ContractServiceAccess extends ServiceAccess {

    public SystemContract get(Long contractId) throws UserNotFoundException;

    public List<? extends SystemContract> get(String name);

    public void create(SystemContract contract) throws UniquenessConstraintException;

    public void update(SystemContract contract) throws UniquenessConstraintException;

    public boolean delete(Long contractId);

    public void delete(Collection<Long> contractIds);

}
