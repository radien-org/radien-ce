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
package io.radien.api.service.permission;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link SystemActionsEnum}
 */
public class SystemActionsEnumTest {

    /**
     * Test for getter {@link SystemActionsEnum#getActionName()}
     */
    @Test
    public void testGetActionName() {
        assertEquals("Create", SystemActionsEnum.ACTION_CREATE.getActionName());
        assertEquals("Read", SystemActionsEnum.ACTION_READ.getActionName());
        assertEquals("Update", SystemActionsEnum.ACTION_UPDATE.getActionName());
        assertEquals("Delete", SystemActionsEnum.ACTION_DELETE.getActionName());
        assertEquals("All", SystemActionsEnum.ACTION_ALL.getActionName());
    }

}
