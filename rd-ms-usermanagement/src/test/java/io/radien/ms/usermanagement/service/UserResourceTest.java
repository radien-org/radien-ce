package io.radien.ms.usermanagement.service;

import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

import static org.mockito.ArgumentMatchers.any;
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
    UserBusinessService userBusinessService;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() throws MalformedURLException {
        when(userResource.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = userResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetById404() {
        Response response = userResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetById() throws UserNotFoundException {
        when(userBusinessService.get(1L)).thenReturn(new User());
        Response response = userResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetByIdGenericException() throws UserNotFoundException {
        when(userBusinessService.get(1L)).thenThrow(new RuntimeException());
        Response response = userResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test Get users by should return success with a 200 code
     */
    @Test
    public void testGetUsersBy() {
        Response response = userResource.getUsers("subj","email@email.pt","logon",true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get users by should return error with a 500 error code message
     */
    @Test
    public void testGetUsersByException() {
        doThrow(new RuntimeException()).when(userBusinessService).getUsers(any());
        Response response = userResource.getUsers("subj","email@email.pt","logon",true,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = userResource.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(userBusinessService).delete(1l);
        Response response = userResource.delete(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Response response = userResource.save(new User());
        assertEquals(200,response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException, UserNotFoundException {
        doThrow(new UniquenessConstraintException()).when(userBusinessService).save(any());
        Response response = userResource.save(new User());
        assertEquals(400,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException, UserNotFoundException {
        doThrow(new RuntimeException()).when(userBusinessService).save(any());
        Response response = userResource.save(new User());
        assertEquals(500,response.getStatus());
    }
}