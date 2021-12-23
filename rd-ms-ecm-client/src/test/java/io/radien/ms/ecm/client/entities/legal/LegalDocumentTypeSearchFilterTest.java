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

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class {@link LegalDocumentTypeSearchFilter}
 */
public class LegalDocumentTypeSearchFilterTest {

    /**
     * Test for constructor regarding {@link LegalDocumentTypeSearchFilter}
     */
    @Test
    public void testConstructor() {
        LegalDocumentTypeSearchFilter filter = new LegalDocumentTypeSearchFilter();
        assertNull(filter.getIds());
        assertNull(filter.getName());
        assertNull(filter.isToBeShown());
        assertNull(filter.isToBeAccepted());
        assertFalse(filter.isExact());
        assertFalse(filter.isLogicConjunction());

        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        String name = "ToU";
        Long tenantId = 1L;

        filter = new LegalDocumentTypeSearchFilter(name, tenantId, true,
                    true, ids, true, true);

        assertEquals(name, filter.getName());
        assertEquals(tenantId, filter.getTenantId());
        assertEquals(ids, filter.getIds());
        assertTrue(filter.isToBeShown());
        assertTrue(filter.isToBeAccepted());
        assertTrue(filter.isExact());
        assertTrue(filter.isLogicConjunction());
    }

}
