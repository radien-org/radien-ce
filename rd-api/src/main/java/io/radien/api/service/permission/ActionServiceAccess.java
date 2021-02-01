/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.radien.api.service.permission;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.model.permission.SystemActionType;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
public interface ActionServiceAccess extends ServiceAccess {

    public SystemAction get(Long actionId) throws ActionNotFoundException;

    public List<SystemAction> get(List<Long> actionId);

    public Page<SystemAction> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    public void save(SystemAction action) throws UniquenessConstraintException;

    public void delete(Long actionId);

    public void delete(Collection<Long> actionIds);

    public List<? extends SystemAction> getActions(SystemActionSearchFilter filter);

}
