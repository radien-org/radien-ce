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
package io.radien.webapp.action;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.exception.SystemException;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class LazyActionsDataModel extends LazyDataModel<SystemAction> {

    private static final long serialVersionUID = -2212982407608665459L;
    private ActionRESTServiceAccess service;

    private List<? extends SystemAction> datasource;

    public LazyActionsDataModel(ActionRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemAction getRowData(String rowKey) {
        for (SystemAction action : datasource) {
            if (action.getId() == Integer.parseInt(rowKey)) {
                return action;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemAction action) {
        return String.valueOf(action.getId());
    }

    @Override
    public List<SystemAction> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemAction> pagedInformation = service.getAll(null, (offset/pageSize) + 1, pageSize, null, true);

            datasource = pagedInformation.getResults();

            rowCount = (long)pagedInformation.getTotalResults();
        } catch (SystemException e) {
            e.printStackTrace();
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }
}

