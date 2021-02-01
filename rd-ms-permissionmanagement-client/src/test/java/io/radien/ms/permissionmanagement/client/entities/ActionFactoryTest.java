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

import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.junit.Assert.assertEquals;

public class ActionFactoryTest {

    Action action;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the permission for
     * testing purposes.
     */
    public ActionFactoryTest() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "action-bbb");
        builder.add("createUser", 2L);
        builder.addNull("lastUpdateUser");
        builder.add("type", String.valueOf(ActionType.WRITE));

        json = builder.build();
        action = ActionFactory.create("action-bbb",
                ActionType.WRITE, 2L);
    }

    /**
     * Test method to validate the creation of a Permission using a Json
     */
    @Test
    public void create() {
        Action newtAction = ActionFactory.create("permission-bbb",
                        ActionType.WRITE, 2L);

        assertEquals(action.getId(), newtAction.getId());
        assertEquals(action.getCreateUser(), newtAction.getCreateUser());
        assertEquals(action.getLastUpdateUser(), newtAction.getLastUpdateUser());
        assertEquals(action.getActionType(), newtAction.getActionType());
    }

    /**
     * Test method to validate the conversion of a Permission using a Json
     */
    @Test
    public void convert() {
        Action newAct = ActionFactory.convert(json);

        assertEquals(action.getId(), newAct.getId());
        assertEquals(action.getCreateUser(), newAct.getCreateUser());
        assertEquals(action.getLastUpdateUser(), newAct.getLastUpdateUser());
        assertEquals(action.getName(), newAct.getName());
        assertEquals(action.getActionType(), newAct.getActionType());
    }

    @Test
    public void testConvertToJsonObject() {
        JsonObject constructedNewJson = ActionFactory.convertToJsonObject(action);
        assertEquals(json.toString(), constructedNewJson.toString());
    }
}