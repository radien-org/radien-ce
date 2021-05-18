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
package io.radien.webapp.tenant;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class LazyTenantDataModel extends LazyDataModel<SystemTenant> {

    private static final long serialVersionUID = -2487575100973554400L;
    private TenantRESTServiceAccess service;

    private List<? extends SystemTenant> datasource;

    public LazyTenantDataModel(TenantRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemTenant getRowData(String rowKey) {
        for (SystemTenant tenant : datasource) {
            if (tenant.getId() == Integer.parseInt(rowKey)) {
                return tenant;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemTenant tenant) {
        return String.valueOf(tenant.getId());
    }

    @Override
    public List<SystemTenant> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemTenant> pagedInformation = service.getAll(null,(offset/pageSize) + 1, pageSize, null, false);

            datasource = pagedInformation.getResults();

            rowCount = (long)pagedInformation.getTotalResults();
        } catch (SystemException e) {
            e.printStackTrace();
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }
}
