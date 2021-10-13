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
package io.radien.webapp.contract;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.webapp.LazyAbstractDataModel;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Bruno Gama
 */
public class LazyContractDataModel extends LazyAbstractDataModel<SystemContract> {

    private static final long serialVersionUID = -7412588857597749996L;
    private ContractRESTServiceAccess service;

    public LazyContractDataModel(ContractRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public Page<? extends SystemContract> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        return service.getAll((offset/pageSize)+1, pageSize);
    }
}
