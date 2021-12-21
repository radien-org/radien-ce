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
package io.radien.ms.legal.entities;

import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test for class {@link LegalDocumentTypeEntity}
 */
public class LegalDocumentTypeEntityTest {

    /**
     * Test for constructor {@link LegalDocumentTypeEntity#LegalDocumentTypeEntity(LegalDocumentType)}
     */
    @Test
    public void testConstructor() {
        LegalDocumentType ldt = new LegalDocumentType();
        ldt.setId(1L);
        ldt.setName("test");
        ldt.setTenantId(2L);
        ldt.setToBeShown(true);
        ldt.setToBeAccepted(true);

        LegalDocumentTypeEntity ldt2 = new LegalDocumentTypeEntity(ldt);

        assertEquals(ldt.getId(), ldt2.getId());
        assertEquals(ldt.getName(), ldt2.getName());
        assertEquals(ldt.getTenantId(), ldt2.getTenantId());
        assertEquals(ldt.isToBeShown(), ldt2.isToBeShown());
        assertEquals(ldt.isToBeAccepted(), ldt2.isToBeAccepted());

        LegalDocumentTypeEntity ldt3 = new LegalDocumentTypeEntity();
        assertFalse(ldt3.isToBeAccepted());
        assertFalse(ldt3.isToBeShown());
    }
}
