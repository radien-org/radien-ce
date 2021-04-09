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
package io.radien.webapp.role;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.exception.SystemException;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class LazyRoleDataModel extends LazyDataModel<SystemRole> {

    private static final long serialVersionUID = 7416623442338554256L;
    private RoleRESTServiceAccess service;

    private List<? extends SystemRole> datasource;

    public LazyRoleDataModel(RoleRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemRole getRowData(String rowKey) {
        for (SystemRole role : datasource) {
            if (role.getId() == Integer.parseInt(rowKey)) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemRole role) {
        return String.valueOf(role.getId());
    }

    @Override
    public List<SystemRole> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemRole> pagedInformation = service.getAll((offset/pageSize)+1, pageSize);

            datasource = pagedInformation.getResults();

            rowCount = service.getTotalRecordsCount();
        } catch (SystemException e) {
            e.printStackTrace();
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }
}
