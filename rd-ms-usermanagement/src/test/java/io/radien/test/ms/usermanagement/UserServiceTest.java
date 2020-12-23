package io.radien.test.ms.usermanagement;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.client.exceptions.NotFoundException;
import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserService;
import io.radien.ms.usermanagement.service.UserEndpoint;
import junit.framework.TestCase;

import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.Properties;

public class UserServiceTest extends TestCase {

    Properties p;
    UserService userService;

    public UserServiceTest() throws Exception{
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:appframedb");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");


        final Context context = EJBContainer.createEJBContainer(p).getContext();

        userService = (UserService) context.lookup("java:global/rd-ms-usermanagement//UserService");
    }

    @Test
    public void testGetById() throws InvalidRequestException, NotFoundException {
        User u = new User();
        u.setFirstname("a");
        u.setLastname("b");
        userService.save(u);
        SystemUser result = userService.get(2L);
        assertNotNull(result);
        assertEquals(u.getFirstname(),result.getFirstname());
        assertEquals(u.getLastname(),result.getLastname());

    }

}
