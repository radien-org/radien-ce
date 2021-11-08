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
package io.radien.ms.tenantmanagement.entities;

import io.radien.ms.tenantmanagement.client.entities.TenantType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 */
public class TenantTypeConverterTest {

    @Test
    public void convertToDatabaseColumn() {
        TenantTypeConverter converter = new TenantTypeConverter();
        Long type = converter.convertToDatabaseColumn(TenantType.ROOT);

        assertEquals((Long) 1L, type);
    }

    @Test
    public void convertToEntityAttribute() {
        TenantTypeConverter converter = new TenantTypeConverter();
        TenantType type = converter.convertToEntityAttribute(2L);

        assertEquals(TenantType.CLIENT, type);
    }

    @Test
    public void testToString() {
        TenantTypeConverter converter = new TenantTypeConverter();
        TenantType type = converter.convertToEntityAttribute(2L);

        assertNotNull(type);
        assertEquals(TenantType.CLIENT, type);
    }
}