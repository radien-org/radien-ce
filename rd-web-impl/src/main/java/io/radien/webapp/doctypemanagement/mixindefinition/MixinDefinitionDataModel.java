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

package io.radien.webapp.doctypemanagement.mixindefinition;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionRESTServiceAccess;
import io.radien.exception.SystemException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MixinDefinitionDataModel extends LazyDataModel<SystemMixinDefinition<Long>> {
    private static final Logger log = LoggerFactory.getLogger( MixinDefinitionDataModel.class);

    private List<? extends SystemMixinDefinition<Long>> datasource;
    private final MixinDefinitionRESTServiceAccess mixinDefinitionService;

    public MixinDefinitionDataModel(MixinDefinitionRESTServiceAccess mixinDefinitionService) {
        this.mixinDefinitionService = mixinDefinitionService;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemMixinDefinition<Long> getRowData(String rowKey) {
        for (SystemMixinDefinition<Long> propertyDefinition : datasource) {
            if (propertyDefinition.getId() == Long.parseLong(rowKey)) {
                return propertyDefinition;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemMixinDefinition<Long> propertyDefinition) {
        return String.valueOf(propertyDefinition.getId());
    }


    @Override
    public List<SystemMixinDefinition<Long>> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        long rowCount = 0L;
        try {
            Page<? extends SystemMixinDefinition<Long>> page = mixinDefinitionService.getAll(null,(offset/pageSize)+1,pageSize,null,true);
            datasource = page.getResults();

            rowCount = page.getTotalResults();
        } catch (SystemException e) {
            log.error(e.getMessage(),e);
        }

        setRowCount(Math.toIntExact(rowCount));

        return new ArrayList<>(datasource);
    }

}
