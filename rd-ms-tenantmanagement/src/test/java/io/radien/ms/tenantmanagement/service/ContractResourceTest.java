package io.radien.ms.tenantmanagement.service;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;


import javax.ws.rs.core.Response;

import io.radien.api.service.contract.ContractServiceAccess;
import io.radien.ms.tenantmanagement.entities.Contract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;


/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ContractResourceTest {

    @InjectMocks
    ContractResource contractResource;

    @Mock
    ContractServiceAccess contractService;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the Get All request which will return a success message code 200.
     */
    @Test
    public void testGetAll() {
        Response response = contractResource.get(null);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test the Get All request Exception which will return a generic error message code 500.
     */
    @Test
    public void testGetAllGenericException() throws MalformedURLException {
        when(contractResource.get(null))
                .thenThrow(new RuntimeException());
        Response response = contractResource.get(null);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test that will test the error message 404 User Not Found
     */
    @Test
    public void testGetById404() {
        Response response = contractResource.getById(9L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }

    /**
     * Get by ID with success should return a 200 code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetById() throws UserNotFoundException {
        when(contractService.get(1L)).thenReturn(new Contract());
        Response response = contractResource.getById(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get by ID exception which will return a 500 error code message
     * @throws UserNotFoundException in case of user not found
     */
    @Test
    public void testGetByIdGenericException() throws UserNotFoundException {
        when(contractService.get(1L)).thenThrow(new RuntimeException());
        Response response = contractResource.getById(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Test Get Contract by should return success with a 200 code
     */
    @Test
    public void testGetContractByName() {
        Response response = contractResource.get("subj");
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }


    @Test
    public void testGetContractById() throws UserNotFoundException {
        when(contractService.get(anyLong())).thenReturn(new Contract());
        Response response = contractResource.getById(1L);

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with success, should return a 200 code message
     */
    @Test
    public void testDelete() {
        Response response = contractResource.delete(1L);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Deletion of the record with error, should return a generic 500 error code message
     */
    @Test
    public void testDeleteGenericError() throws UserNotFoundException, SystemException {
        doThrow(new RuntimeException()).when(contractService).delete(1L);
        Response response = contractResource.delete(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testCreate() {
        Response response = contractResource.create(new Contract());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contract not found
     */
    @Test
    public void testCreateInvalid() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new UniquenessConstraintException()).when(contractService).create(any());
        Response response = contractResource.create(new Contract());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contact not found
     */
    @Test
    public void testCreateGenericError() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new RuntimeException()).when(contractService).create(any());
        Response response = contractResource.create(new Contract());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }


    /**
     * Creation with success of a record. Should return a 200 code message
     */
    @Test
    public void testUpdate() {
        Response response = contractResource.update(1l,new Contract());
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    /**
     * Creation with error of a record. Should return a 400 code message Invalid Requested Exception
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contract not found
     */
    @Test
    public void testUpdateInvalid() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new UniquenessConstraintException()).when(contractService).update(any());
        Response response = contractResource.update(1l,new Contract());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }

    /**
     * Creation of a record with error. Should return a generic error message 500
     * @throws UniquenessConstraintException in case of request could not be performed by any specific and justified in the
     * message reason
     * @throws UserNotFoundException in case of contact not found
     */
    @Test
    public void testUpdateGenericError() throws UniquenessConstraintException, UserNotFoundException, SystemException {
        doThrow(new RuntimeException()).when(contractService).update(any());
        Response response = contractResource.update(1l,new Contract());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());
    }
}