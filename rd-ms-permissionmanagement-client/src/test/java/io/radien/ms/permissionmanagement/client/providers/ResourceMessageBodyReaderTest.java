package io.radien.ms.permissionmanagement.client.providers;

import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.services.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class ResourceMessageBodyReaderTest {

    @InjectMocks
    ResourceMessageBodyReader reader;

    @Test
    public void testIsReadable() {
        Assert.assertFalse(reader.isReadable(null, null, null, null));
        Assert.assertTrue(reader.isReadable(Resource.class, null, null, null));
    }

    @Test
    public void testRead() throws IOException {
        String resourceName = "resource-radien-a";
        Long id = 111L;
        Long createUser = 222L;
        Long updateUser = 333L;

        Resource a = ResourceFactory.create(resourceName, createUser);
        a.setLastUpdateUser(updateUser);
        a.setId(id);
        String json = getJsonString(a);

        InputStream in = new ByteArrayInputStream(json.getBytes());
        Resource a2 = reader.readFrom(null, null,
                null, null, null, in);

        Assert.assertEquals(a.getId(), a2.getId());
        Assert.assertEquals(a.getCreateUser(), a2.getCreateUser());
        Assert.assertEquals(a.getLastUpdateUser(), a2.getLastUpdateUser());
        Assert.assertEquals(a.getName(), a2.getName());

        // Setting others fields with null (id, resource, createUser, lastUpdateUser,..., etc)
        a = ResourceFactory.create(resourceName, null);

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

    private String getJsonString(Resource a) {
        StringBuffer params = new StringBuffer();

        params.append("\"id\":").append(a.getId()).append(",");
        params.append("\"createUser\":").append(a.getCreateUser()).append(",");
        params.append("\"lastUpdateUser\":").append(a.getLastUpdateUser()).append(",");
        params.append("\"name\":\"").append(a.getName()).append("\"");

        StringBuffer bf = new StringBuffer();
        bf.append("{").append(params).append("}");

        return bf.toString();
    }

    public String getJsonOmittingNullFields(Resource a) {
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
