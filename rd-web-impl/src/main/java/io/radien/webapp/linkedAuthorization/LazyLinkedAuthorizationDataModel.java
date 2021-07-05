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
package io.radien.webapp.linkedAuthorization;

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class LazyLinkedAuthorizationDataModel extends LazyDataModel<SystemLinkedAuthorization> {

    protected static final transient Logger log = LoggerFactory.getLogger(LazyLinkedAuthorizationDataModel.class);
    private static final long serialVersionUID = 5374304621678901012L;
    private LinkedAuthorizationRESTServiceAccess service;

    private List<? extends SystemLinkedAuthorization> datasource;

    public LazyLinkedAuthorizationDataModel(LinkedAuthorizationRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemLinkedAuthorization getRowData(String rowKey) {
        for (SystemLinkedAuthorization linkedAuthorization : datasource) {
            if (linkedAuthorization.getId() == Integer.parseInt(rowKey)) {
                return linkedAuthorization;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemLinkedAuthorization linkedAuthorization) {
        return String.valueOf(linkedAuthorization.getId());
    }

    @Override
    public List<SystemLinkedAuthorization> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            datasource = service.getAll((offset/pageSize)+1, pageSize);

            rowCount = service.getTotalRecordsCount();
        } catch (SystemException e) {
            log.error(e.getMessage(),e);
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }
}
