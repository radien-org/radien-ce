package io.radien.ms.permissionmanagement.service;

import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.model.Action;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ActionControllerTest {

    @InjectMocks
    ActionController actionController;

    @Mock
    ActionServiceAccess actionServiceAccess;
    
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = actionController.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() throws MalformedURLException {
        when(actionController.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = actionController.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test that will test the error message 404 permission Not Found
     */
    @Test
    public void testGetById404() {
        Response response = actionController.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws ActionNotFoundException in case of action not found
     */
    @Test
    public void testGetById() throws ActionNotFoundException {
        when(actionServiceAccess.get(1L)).thenReturn(new Action());
        Response response = actionController.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws ActionNotFoundException in case of action not found
     */
    @Test
    public void testGetByIdGenericException() throws ActionNotFoundException {
        when(actionServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = actionController.getById(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test Get Actions by should return success with a 200 code
     */
    @Test
    public void testGetActionsBy() {
        Response response = actionController.getActions("action-name",
                ActionType.WRITE.getName(), true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get Actions by should return error with a 500 error code message
     */
    @Test
    public void testGetPermissionsByException() {
        doThrow(new RuntimeException()).when(actionServiceAccess).getActions(any());
        Response response = actionController.getActions("action-name",
                ActionType.WRITE.getName(), true,true);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testGetPermissionInformingInvalidType() {
        Response response = actionController.getActions("action-name",
                "UNKNOWN-TYPE", true,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = actionController.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(actionServiceAccess).delete(1l);
        Response response = actionController.delete(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Response response = actionController.save(new io.radien.ms.permissionmanagement.client.entities.Action());
        assertEquals(200,response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(actionServiceAccess).save(any());
        Response response = actionController.save(new io.radien.ms.permissionmanagement.client.entities.Action());
        assertEquals(400,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException {
        doThrow(new RuntimeException()).when(actionServiceAccess).save(any());
        Response response = actionController.save(new io.radien.ms.permissionmanagement.client.entities.Action());
        assertEquals(500,response.getStatus());
    }
}