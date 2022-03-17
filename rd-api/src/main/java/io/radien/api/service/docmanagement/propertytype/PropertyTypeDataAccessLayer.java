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
package io.radien.api.service.docmanagement.propertytype;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.service.ServiceAccess;

import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import java.util.List;

public interface PropertyTypeDataAccessLayer extends ServiceAccess {

    Page<SystemJCRPropertyType> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    SystemJCRPropertyType get(Long id) throws NotFoundException;

    void save(SystemJCRPropertyType propertyType) throws UniquenessConstraintException;

    void delete(Long id);

    long getTotalRecordsCount();
}
