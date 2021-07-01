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
package io.radien.webapp;

import io.radien.api.Model;
import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.kernel.OAF;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class LazyAbstractDataModel<T extends Model> extends LazyDataModel<T> {

    private static final long serialVersionUID = 797281468925687077L;

    protected List<? extends T> datasource;

    private transient static final Logger log = LoggerFactory.getLogger(LazyAbstractDataModel.class);

    @Override
    public T getRowData(String rowKey) {
        for (T obj : datasource) {
            if (obj.getId() == Integer.parseInt(rowKey)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(T obj) {
        return String.valueOf(obj.getId());
    }

    public abstract Page<? extends T> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException;

    @Override
    public List<T> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = 0L;
        try {
            Page<? extends T> pagedInformation = getData(offset,pageSize,sortBy,filterBy);

            datasource = pagedInformation.getResults();

            rowCount = (long) pagedInformation.getTotalResults();
        } catch (SystemException e) {
            log.error(e.getMessage(),e);
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource != null ? new ArrayList<>(datasource) : null;
    }
}
