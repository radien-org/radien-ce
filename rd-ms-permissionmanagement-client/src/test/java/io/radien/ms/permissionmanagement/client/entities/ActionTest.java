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
package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ActionTest {

    private Action action = new Action();
    private Date terminationDate = new Date();

    public ActionTest() {
        action.setId(2L);
        action.setName("action-test");
    }

    @Test
    public void getId() {
        assertSame(2L, action.getId());
    }

    @Test
    public void setId() {
        action.setId(3L);
        assertSame(3L, action.getId());
    }

    @Test
    public void getName() {
        assertEquals("action-test", action.getName());
    }

    @Test
    public void setName() {
        action.setName("action-test2");
        assertEquals("action-test2", action.getName());
    }


    @Test
    public void testConstructor() {
        Action original = new Action();
        original.setId(11L);
        original.setName("original");

        Action newAction = new Action(original);
        assertNotNull(newAction.getId());
        assertNotNull(newAction.getName());

        assertEquals(newAction.getId(), original.getId());
        assertEquals(newAction.getName(), original.getName());
    }
}
