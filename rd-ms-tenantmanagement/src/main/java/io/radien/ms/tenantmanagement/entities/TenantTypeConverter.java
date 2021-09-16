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
package io.radien.ms.tenantmanagement.entities;

import io.radien.ms.tenantmanagement.client.entities.TenantType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * A class that implements this interface can be used to convert entity attribute state into database
 * column representation and back again.
 * @author Newton Carvalho
 */
@Converter
public class TenantTypeConverter implements AttributeConverter<String, Long> {

    /**
     * Converts the value stored in the entity attribute into the data representation to be stored in the database.
     * @param attribute the entity attribute value to be converted
     * @return the converted data to be stored in the database column
     */
    public Long convertToDatabaseColumn(String attribute) {
        TenantType tenantType = TenantType.getByDescription(attribute);
        return attribute != null ? tenantType.getId() : null;
    }

    /**
     * Converts the data stored in the database column into the value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to specify the correct dbData type for the
     * corresponding column for use by the JDBC driver: i.e., persistence providers are not expected to do
     * such type conversion.
     * @param idFromDb the data from the database column to be converted
     * @return the converted value to be stored in the entity attribute
     */
    @Override
    public String convertToEntityAttribute(Long idFromDb) {
        TenantType tenantType = TenantType.getById(idFromDb);
        return tenantType.getDescription();
    }
}
