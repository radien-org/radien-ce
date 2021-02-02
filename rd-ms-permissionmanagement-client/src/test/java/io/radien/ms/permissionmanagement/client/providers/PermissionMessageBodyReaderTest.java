package io.radien.ms.permissionmanagement.client.providers;

import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
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
        Permission p = PermissionFactory.create(permissionName, action,
                createUser);
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
        compareActions(p.getAction(), p2.getAction());

        // Setting others fields with null (id, action, createUser, lastUpdateUser,..., etc)
        p = PermissionFactory.create(permissionName, null, null);

        json = getJsonString(p);
        in = new ByteArrayInputStream(json.getBytes());
        p2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(p.getId(), p2.getId());
        Assert.assertEquals(p.getCreateUser(), p2.getCreateUser());
        Assert.assertEquals(p.getLastUpdateUser(), p2.getLastUpdateUser());
        Assert.assertEquals(p.getName(), p2.getName());
        compareActions(p.getAction(), p2.getAction());

        p.setLastUpdateUser(111111L);
        action = ActionFactory.create("Update-Radien-User", ActionType.WRITE, 28L);
        action.setId(11L);
        p.setAction(action);

        json = getJsonOmittingNullFields(p);
        in = new ByteArrayInputStream(json.getBytes());
        p2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(p.getId(), p2.getId());
        Assert.assertEquals(p.getCreateUser(), p2.getCreateUser());
        Assert.assertEquals(p.getLastUpdateUser(), p2.getLastUpdateUser());
        Assert.assertEquals(p.getName(), p2.getName());
        compareActions(p.getAction(), p2.getAction());
    }

    private void compareActions(Action a, Action b) {
        if (a == null) {
            Assert.assertNull(b);
            return;
        }

        Assert.assertNotNull(b);
        Assert.assertEquals(a.getName(), b.getName());
        Assert.assertEquals(a.getType(), b.getType());
        Assert.assertEquals(a.getId(), b.getId());
    }

    private java.lang.String getJsonString(Permission p) {
        StringBuffer params = new StringBuffer();

        params.append("\"id\":").append(p.getId()).append(",");
        params.append("\"createUser\":").append(p.getCreateUser()).append(",");
        params.append("\"lastUpdateUser\":").append(p.getLastUpdateUser()).append(",");
        params.append("\"name\":\"").append(p.getName()).append("\"");

        params.append(",");
        params.append("\"action\":");
        if (p.getAction() != null) {
            params.append(ActionFactory.convertToJsonObject(p.getAction()));
        }
        else {
            params.append("null");
        }

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

        if (p.getAction() != null) {
            params.append(",");
            params.append("\"action\":").append(ActionFactory.convertToJsonObject(p.getAction()));
        }

        StringBuffer bf = new StringBuffer();
        bf.append("{").append(params).append("}");

        return bf.toString();
    }
}
