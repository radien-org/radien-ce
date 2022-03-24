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
package io.radien.api.service.docmanagement.propertydefinition;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;

import io.radien.exception.SystemException;

import java.util.List;
import java.util.Optional;

public interface PropertyDefinitionRESTServiceAccess extends Appframeable{

    Page<? extends SystemPropertyDefinition> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending)
            throws SystemException;

	Optional<SystemPropertyDefinition> getPropertyDefinitionById(Long id) throws SystemException;

    boolean save(SystemPropertyDefinition propertyType) throws SystemException;

    boolean deletePropertyDefinition(long id) throws SystemException;

    Long getTotalRecordsCount() throws SystemException;
}
