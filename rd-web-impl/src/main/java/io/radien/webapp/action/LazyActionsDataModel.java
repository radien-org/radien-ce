/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
import io.radien.webapp.LazyAbstractDataModel;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Bruno Gama
 */
public class LazyActionsDataModel extends LazyAbstractDataModel<SystemAction> {

    private static final long serialVersionUID = -2212982407608665459L;
    private ActionRESTServiceAccess service;

    public LazyActionsDataModel(ActionRESTServiceAccess service) {
        this.service = service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public Page<? extends SystemAction> getData(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) throws SystemException {
        return service.getAll(null, (offset/pageSize) + 1, pageSize, null, true);
    }
}

