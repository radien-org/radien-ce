/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class LazyContractDataModel extends LazyDataModel<SystemContract> {

    private static final long serialVersionUID = -7412588857597749996L;
    private ContractRESTServiceAccess service;

    private List<? extends SystemContract> datasource;

    public LazyContractDataModel(ContractRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemContract getRowData(String rowKey) {
        for (SystemContract contract : datasource) {
            if (contract.getId() == Integer.parseInt(rowKey)) {
                return contract;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemContract contract) {
        return String.valueOf(contract.getId());
    }

    @Override
    public List<SystemContract> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemContract> pagedInformation = service.getAll((offset/pageSize)+1, pageSize);

            datasource = pagedInformation.getResults();

            rowCount = service.getTotalRecordsCount();
        } catch (SystemException e) {
            e.printStackTrace();
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }
}
