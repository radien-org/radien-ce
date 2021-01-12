package io.radien.ms.usermanagement.service;

import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.entities.User;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserResourceTest {

    @InjectMocks
    UserResource userResource;
    @Mock
    UserService userService;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetAll() {
        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testGetAllGenericException() throws MalformedURLException {
        when(userResource.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testGetById404() {
        Response response = userResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testGetById() throws UserNotFoundException {
        when(userService.get(1L)).thenReturn(new User());
        Response response = userResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testGetByIdGenericException() throws UserNotFoundException {
        when(userService.get(1L)).thenThrow(new RuntimeException());
        Response response = userResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testDelete() {
        Response response = userResource.delete(1l);
        assertEquals(200,response.getStatus());
    }
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(userService).delete(1l);
        Response response = userResource.delete(1l);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testSave() {
        Response response = userResource.save(new User());
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testCreateGenericError() throws InvalidRequestException {
        User u = new User();
        doThrow(new RuntimeException()).when(userService).save(u);
        Response response = userResource.save(u);
        assertEquals(500,response.getStatus());
    }
}