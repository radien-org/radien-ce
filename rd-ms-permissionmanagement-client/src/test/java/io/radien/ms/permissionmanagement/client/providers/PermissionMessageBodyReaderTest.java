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
package io.radien.ms.permissionmanagement.client.providers;

import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class PermissionMessageBodyReaderTest {

    @InjectMocks
    PermissionMessageBodyReader reader;

    @Test
    public void testIsReadable() {
        Assert.assertFalse(reader.isReadable(null, null, null, null));
        Assert.assertTrue(reader.isReadable(Permission.class, null, null, null));
    }

    @Test
    public void testRead() throws IOException {
        String permissionName = "permission-radien-a";
        Long id = 111L;
        Long createUser = 222L;
        Long updateUser = 333L;
        Action action = null;
        Permission p = PermissionFactory.create(permissionName, null,
                null, createUser);
        p.setLastUpdateUser(updateUser);
        p.setId(id);
        String json = getJsonString(p);

        InputStream in = new ByteArrayInputStream(json.getBytes());
        Permission p2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(p.getId(), p2.getId());
        Assert.assertEquals(p.getCreateUser(), p2.getCreateUser());
        Assert.assertEquals(p.getLastUpdateUser(), p2.getLastUpdateUser());
        Assert.assertEquals(p.getName(), p2.getName());
        Assert.assertEquals(p.getActionId(), p2.getActionId());

        // Setting others fields with null (id, action, createUser, lastUpdateUser,..., etc)
        p = PermissionFactory.create(permissionName, null, null, null);

        json = getJsonString(p);
        in = new ByteArrayInputStream(json.getBytes());
        p2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(p.getId(), p2.getId());
        Assert.assertEquals(p.getCreateUser(), p2.getCreateUser());
        Assert.assertEquals(p.getLastUpdateUser(), p2.getLastUpdateUser());
        Assert.assertEquals(p.getName(), p2.getName());
        Assert.assertEquals(p.getActionId(), p2.getActionId());

        p.setLastUpdateUser(111111L);
        action = ActionFactory.create("Update-Radien-User", 28L);
        action.setId(11L);
        p.setActionId(action.getId());

        json = getJsonOmittingNullFields(p);
        in = new ByteArrayInputStream(json.getBytes());
        p2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(p.getId(), p2.getId());
        Assert.assertEquals(p.getCreateUser(), p2.getCreateUser());
        Assert.assertEquals(p.getLastUpdateUser(), p2.getLastUpdateUser());
        Assert.assertEquals(p.getName(), p2.getName());
        Assert.assertEquals(p.getActionId(), p2.getActionId());
    }

    private java.lang.String getJsonString(Permission p) {
        StringBuffer params = new StringBuffer();

        params.append("\"id\":").append(p.getId()).append(",");
        params.append("\"createUser\":").append(p.getCreateUser()).append(",");
        params.append("\"lastUpdateUser\":").append(p.getLastUpdateUser()).append(",");
        params.append("\"actionId\":").append(p.getActionId()).append(",");
        params.append("\"name\":\"").append(p.getName()).append("\"");

        StringBuffer bf = new StringBuffer();
        bf.append("{").append(params).append("}");

        return bf.toString();
    }

    public String getJsonOmittingNullFields(Permission p) {
        StringBuffer params = new StringBuffer();

        if (p.getId() != null) {
            params.append("\"id\":").append(p.getId());
        }

        if (p.getCreateUser() != null) {
            if (params.length() > 0)
                params.append(",");
            params.append("\"createUser\":").append(p.getCreateUser());
        }

        if (p.getLastUpdateUser() != null) {
            if (params.length() > 0)
                params.append(",");
            params.append("\"lastUpdateUser\":").append(p.getLastUpdateUser());
        }

        if (p.getName() != null) {
            if (params.length() > 0)
                params.append(",");
            params.append("\"name\":\"").append(p.getName()).append("\"");
        }

        if (p.getActionId() != null) {
            params.append(",");
            params.append("\"actionId\":").append(p.getActionId());
        }

        StringBuffer bf = new StringBuffer();
        bf.append("{").append(params).append("}");

        return bf.toString();
    }
}
