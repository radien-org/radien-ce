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
 *
 */
package io.radien.ms.ecm.client.entities.legal;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test class for {@link LegalDocumentType}
 */
public class LegalDocumentTypeTest {

    /**
     * Test for constructor {@link LegalDocumentType#LegalDocumentType(LegalDocumentType)}
     */
    @Test
    public void testConstructor() {
        LegalDocumentType ldt = new LegalDocumentType();
        ldt.setId(1L);
        ldt.setName("test");
        ldt.setTenantId(2L);
        ldt.setToBeShown(true);
        ldt.setToBeAccepted(true);

        LegalDocumentType ldt2 = new LegalDocumentType(ldt);

        assertEquals(ldt.getId(), ldt2.getId());
        assertEquals(ldt.getName(), ldt2.getName());
        assertEquals(ldt.getTenantId(), ldt2.getTenantId());
        assertEquals(ldt.isToBeShown(), ldt2.isToBeShown());
        assertEquals(ldt.isToBeAccepted(), ldt2.isToBeAccepted());
    }
}
