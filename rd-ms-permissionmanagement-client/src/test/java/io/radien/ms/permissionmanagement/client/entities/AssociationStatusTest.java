/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Association status testing
 * {@link io.radien.ms.permissionmanagement.client.entities.AssociationStatus}
 */
public class AssociationStatusTest {

    private static final String emptyString = "";
    private static final String failString = "fail";

    /**
     * Test the association status constructor
     */
    @Test
    public void testConstructor() {
        AssociationStatus associationStatus = new AssociationStatus();
        assertTrue(associationStatus.isOK());
        assertNotNull(associationStatus.getMessage());
        assertEquals(emptyString, associationStatus.getMessage());

        associationStatus = new AssociationStatus(false, failString);
        assertFalse(associationStatus.isOK());
        assertNotNull(associationStatus.getMessage());
        assertEquals(failString, associationStatus.getMessage());

        associationStatus = new AssociationStatus(false, null);
        assertFalse(associationStatus.isOK());
        assertNull(associationStatus.getMessage());
    }
}
