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
package io.radien.ms.tenantmanagement.entities;

import io.radien.ms.tenantmanagement.client.entities.TenantType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Newton Carvalho
 */
@Converter
public class TenantTypeConverter implements AttributeConverter<TenantType, Long> {

    @Override
    public Long convertToDatabaseColumn(TenantType attribute) {
        return attribute != null ? attribute.getId() : null;
    }

    @Override
    public TenantType convertToEntityAttribute(Long idFromDb) {
        return TenantType.getById(idFromDb);
    }
}
