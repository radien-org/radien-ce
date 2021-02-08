package io.radien.ms.permissionmanagement.client.providers;

import io.radien.api.model.permission.SystemAction;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class ActionMessageBodyReaderTest {

    @InjectMocks
    ActionMessageBodyReader reader;

    @Test
    public void testIsReadable() {
        Assert.assertFalse(reader.isReadable(null, null, null, null));
        Assert.assertTrue(reader.isReadable(Action.class, null, null, null));
    }

    @Test
    public void testRead() throws IOException {
        String actionName = "action-radien-a";
        Long id = 111L;
        Long createUser = 222L;
        Long updateUser = 333L;

        Action a = ActionFactory.create(actionName, createUser);
        a.setLastUpdateUser(updateUser);
        a.setId(id);
        String json = getJsonString(a);

        InputStream in = new ByteArrayInputStream(json.getBytes());
        Action a2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(a.getId(), a2.getId());
        Assert.assertEquals(a.getCreateUser(), a2.getCreateUser());
        Assert.assertEquals(a.getLastUpdateUser(), a2.getLastUpdateUser());
        Assert.assertEquals(a.getName(), a2.getName());

        // Setting others fields with null (id, action, createUser, lastUpdateUser,..., etc)
        a = ActionFactory.create(actionName, null);

        json = getJsonString(a);
        in = new ByteArrayInputStream(json.getBytes());
        a2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(a.getId(), a2.getId());
        Assert.assertEquals(a.getCreateUser(), a2.getCreateUser());
        Assert.assertEquals(a.getLastUpdateUser(), a2.getLastUpdateUser());
        Assert.assertEquals(a.getName(), a2.getName());

        a.setLastUpdateUser(111111L);
        json = getJsonOmittingNullFields(a);
        in = new ByteArrayInputStream(json.getBytes());
        a2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(a.getId(), a2.getId());
        Assert.assertEquals(a.getCreateUser(), a2.getCreateUser());
        Assert.assertEquals(a.getLastUpdateUser(), a2.getLastUpdateUser());
        Assert.assertEquals(a.getName(), a2.getName());
    }

    private String getJsonString(Action a) {
        StringBuffer params = new StringBuffer();

        params.append("\"id\":").append(a.getId()).append(",");
        params.append("\"createUser\":").append(a.getCreateUser()).append(",");
        params.append("\"lastUpdateUser\":").append(a.getLastUpdateUser()).append(",");
        params.append("\"name\":\"").append(a.getName()).append("\"");

        StringBuffer bf = new StringBuffer();
        bf.append("{").append(params).append("}");

        return bf.toString();
    }

    public String getJsonOmittingNullFields(Action a) {
        StringBuffer params = new StringBuffer();

        if (a.getId() != null) {
            params.append("\"id\":").append(a.getId());
        }

        if (a.getCreateUser() != null) {
            if (params.length() > 0)
                params.append(",");
            params.append("\"createUser\":").append(a.getCreateUser());
        }

        if (a.getLastUpdateUser() != null) {
            if (params.length() > 0)
                params.append(",");
            params.append("\"lastUpdateUser\":").append(a.getLastUpdateUser());
        }

        if (a.getName() != null) {
            if (params.length() > 0)
                params.append(",");
            params.append("\"name\":\"").append(a.getName()).append("\"");
        }

        StringBuffer bf = new StringBuffer();
        bf.append("{").append(params).append("}");

        return bf.toString();
    }
}
