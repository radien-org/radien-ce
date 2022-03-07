/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.webapp.i18n;

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NRESTServiceAccess;
import io.radien.exception.SystemException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I18NPropertyDataModel extends LazyDataModel<SystemI18NProperty> {

    private static final transient Logger log = LoggerFactory.getLogger(I18NPropertyDataModel.class);

    private static final long serialVersionUID = -8872194189478081408L;

    private List<? extends SystemI18NProperty> datasource;
    private final I18NRESTServiceAccess service;

    public I18NPropertyDataModel(I18NRESTServiceAccess service) {
        this.service = service;
        datasource = new ArrayList<>();
    }

    @Override
    public SystemI18NProperty getRowData(String rowKey) {
        for (SystemI18NProperty obj : datasource) {
            if (String.format("%s%s", obj.getKey(), obj.getApplication()).equals(rowKey)) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemI18NProperty obj) {
        return String.format("%s%s", obj.getKey(), obj.getApplication());
    }

    private Page<SystemI18NProperty> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        return service.getAll(null, (offset/pageSize) + 1, pageSize, null, true);
    }

    @Override
    public List<SystemI18NProperty> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = 0L;
        try {
            Page<SystemI18NProperty> pagedInformation = getData(offset,pageSize,sortBy,filterBy);

            datasource = pagedInformation.getResults();

            rowCount = pagedInformation.getTotalResults();
            setRowCount(Math.toIntExact(rowCount));
        } catch (SystemException e) {
            log.error(e.getMessage(),e);
        }


        return datasource != null ? new ArrayList<>(datasource) : new ArrayList<>();
    }
}
