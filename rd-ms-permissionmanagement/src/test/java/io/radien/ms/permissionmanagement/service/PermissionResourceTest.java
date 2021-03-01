package io.radien.ms.permissionmanagement.service;

import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Permission;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class PermissionResourceTest {

    @InjectMocks
    PermissionResource permissionResource;
    @Mock
    PermissionServiceAccess permissionServiceAccess;
    @Mock
    PermissionBusinessService permissionBusinessService;
    
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = permissionResource.getAll(null,1,10,null,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() {
        when(permissionResource.getAll(null,1,10,null,true))
                .thenThrow(new RuntimeException());
        Response response = permissionResource.getAll(null,1,10,null,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test that will test the error message 404 permission Not Found
     */
    @Test
    public void testGetById404() {
        Response response = permissionResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testGetById() throws PermissionNotFoundException {
        when(permissionServiceAccess.get(1L)).thenReturn(new Permission());
        Response response = permissionResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testGetByIdGenericException() throws PermissionNotFoundException {
        when(permissionServiceAccess.get(1L)).thenThrow(new RuntimeException());
        Response response = permissionResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test Get permissions by should return success with a 200 code
     */
    @Test
    public void testGetPermissionsBy() {
        Response response = permissionResource.getPermissions("permission", 11L, 100L,true,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test Get permissions by should return error with a 500 error code message
     */
    @Test
    public void testGetPermissionsByException() {
        doThrow(new RuntimeException()).when(permissionServiceAccess).getPermissions(any());
        Response response = permissionResource.getPermissions("perm", 1l, 2l,true,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = permissionResource.delete(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(permissionServiceAccess).delete(1l);
        Response response = permissionResource.delete(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testSave() {
        Response response = permissionResource.save(new Permission());
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testSuccessfulAssociationWithAction() throws UniquenessConstraintException {
        when(permissionBusinessService.associate(any(), any(), any())).thenReturn(new AssociationStatus());
        Response response = permissionResource.associate(11l, 12L, 13L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testUnsuccessfulAssociationWithAction() throws UniquenessConstraintException {
        when(permissionBusinessService.associate(any(), any(), any())).thenReturn(
                new AssociationStatus(false, "unsuccessful operation"));
        Response response = permissionResource.associate(11l, 12L, 13L);
        assertEquals(400,response.getStatus());
        assertEquals(response.getEntity().getClass(), String.class);
        assertEquals(response.getEntity().toString(), "unsuccessful operation");
    }

    @Test
    public void testUnsuccessfulAssociationByException() throws UniquenessConstraintException {
        when(permissionBusinessService.associate(any(), any(), any())).
                thenThrow(new RuntimeException("unsuccessful operation"));
        Response response = permissionResource.associate(11l, 12L, 13L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testSuccessfulDissociation() throws UniquenessConstraintException {
        Long permissionId = 11L;
        when(permissionBusinessService.dissociation(permissionId)).thenReturn(new AssociationStatus());
        Response response = permissionResource.dissociate(permissionId);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testUnsuccessfulDissociation() throws UniquenessConstraintException {
        Long permissionId = 11L;
        when(permissionBusinessService.dissociation(permissionId)).thenThrow(
                new RuntimeException("something happen"));
        Response response = permissionResource.dissociate(permissionId);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testUnsuccessfulDissociationByException() throws UniquenessConstraintException {
        AssociationStatus associationStatus = new AssociationStatus(false, "something happen...");
        Long permissionId = 11L;
        when(permissionBusinessService.dissociation(permissionId)).thenReturn(associationStatus);
        Response response = permissionResource.dissociate(permissionId);
        assertEquals(400,response.getStatus());
        assertEquals(response.getEntity().getClass(), String.class);
        assertEquals(response.getEntity().toString(), associationStatus.getMessage());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException()).when(permissionServiceAccess).save(any());
        Response response = permissionResource.save(new Permission());
        assertEquals(400,response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws PermissionNotFoundException in case of permission not found
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException {
        doThrow(new RuntimeException()).when(permissionServiceAccess).save(any());
        Response response = permissionResource.save(new Permission());
        assertEquals(500,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using just Id parameter
     */
    @Test
    public void testExistsUsingPermissionId() {
        when(permissionServiceAccess.exists(anyLong(), nullable(String.class))).thenReturn(true);
        Response response = permissionResource.exists(2L, null);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using just Name parameter
     */
    @Test
    public void testExistsUsingPermissionName() {
        when(permissionServiceAccess.exists(nullable(Long.class), anyString())).thenReturn(true);
        Response response = permissionResource.exists(null, "add-user");
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") using id and permission name as parameter
     * (All possible parameters)
     */
    @Test
    public void testExistsUsingPermissionIdAndName()  {
        // case 1: Returning true
        when(permissionServiceAccess.exists(anyLong(), anyString())).thenReturn(true);
        Response response = permissionResource.exists(2L, "add-user");
        assertEquals(200, response.getStatus());

        // case 2: Returning false
        when(permissionServiceAccess.exists(anyLong(), anyString())).thenReturn(false);
        response = permissionResource.exists(2L, "add-user");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }


    /**
     * Test the  endpoint ("exists") omitting all required parameters
     */
    @Test
    public void testExistsWithNoParameters() {
        Response response = permissionResource.exists(null, null);
        assertEquals(400,response.getStatus());
    }

    /**
     * Test the  endpoint ("exists") for cases whether there is no permission
     * for an informed Id
     */
    @Test
    public void testWhenHasNoPermission() {
        when(permissionServiceAccess.getPermissionByActionAndResourceNames("add", "contract")).
                thenReturn(null);
        Response response = permissionResource.hasPermission("add", "contract");
        assertEquals(response.getStatusInfo().toEnum(), Response.Status.NOT_FOUND);
    }

    @Test
    public void testingHasPermissionWhenErrorOccurs() {
        when(permissionServiceAccess.getPermissionByActionAndResourceNames("add", "contract")).
                thenThrow(new RuntimeException("something happen during the search process"));
        Response response = permissionResource.hasPermission("add", "contract");
        assertEquals(response.getStatusInfo().toEnum(), Response.Status.INTERNAL_SERVER_ERROR);
    }
}