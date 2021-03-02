/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.rolemanagement.services;

import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationResourceTest {

    @InjectMocks
    LinkedAuthorizationResource linkedAuthorizationResource;
    @Mock
    LinkedAuthorizationService linkedAuthorizationsService;
    @Mock
    LinkedAuthorizationBusinessService linkedAuthorizationBusinessService;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test reposnse of the get all method
     */
    @Test
    public void testGetAllAssociations() {
        Response response = linkedAuthorizationResource.getAllAssociations(1,10);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get all
     */
    @Test
    public void testGetAllGenericException() {
        when(linkedAuthorizationResource.getAllAssociations(1,10))
                .thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.getAllAssociations(1,10);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response of the get specific association
     */
    @Test
    public void testGetSpecificAssociation() {
        Response response = linkedAuthorizationResource.getSpecificAssociation(2L,2L,2L, 2L,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get specific association
     */
    @Test
    public void testGetSpecificAssociationException() {
        when(linkedAuthorizationBusinessService.getSpecificAssociation(any())).thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.getSpecificAssociation(2L,2L,2L, 2L,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests return message from the get association by id
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testGetAssociationById() throws LinkedAuthorizationNotFoundException {
        when(linkedAuthorizationsService.getAssociationById(1L)).thenReturn(new LinkedAuthorization());
        Response response = linkedAuthorizationResource.getAssociationById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get by id association
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testGetAssociationByIdGenericError() throws LinkedAuthorizationNotFoundException{
        when(linkedAuthorizationBusinessService.getAssociationById(any())).thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.getAssociationById(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests exception from the get by id association
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testGetAssociationByIdNotFoundError() throws LinkedAuthorizationNotFoundException {
        when(linkedAuthorizationBusinessService.getAssociationById(any())).thenThrow(new LinkedAuthorizationNotFoundException());
        Response response = linkedAuthorizationResource.getAssociationById(1L);
        assertEquals(404,response.getStatus());
    }

    /**
     * Tests delete association return message
     */
    @Test
    public void testDeleteAssociation() {
        Response response = linkedAuthorizationResource.deleteAssociation(1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests Runtime exception from the delete method
     * @throws LinkedAuthorizationNotFoundException in case object was not found
     */
    @Test
    public void testDeleteGenericError() throws LinkedAuthorizationNotFoundException {
        doThrow(new RuntimeException()).when(linkedAuthorizationBusinessService).deleteAssociation(any());
        Response response = linkedAuthorizationResource.deleteAssociation(1l);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests the delete not found exception record
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testDeleteNotFoundError() throws LinkedAuthorizationNotFoundException {
        when(linkedAuthorizationBusinessService.getAssociationById(any())).thenThrow(new LinkedAuthorizationNotFoundException());
        Response response = linkedAuthorizationResource.deleteAssociation(1l);
        assertEquals(404,response.getStatus());
    }

    /**
     * Tests the save association method
     */
    @Test
    public void testSaveAssociation() {
        Response response = linkedAuthorizationResource.saveAssociation(new LinkedAuthorization());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests creation of an invalid association, will return UniquenessConstraintException
     * @throws UniquenessConstraintException in case record has duplicated fields
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testCreateInvalid() throws Exception {
        doThrow(new UniquenessConstraintException()).when(linkedAuthorizationBusinessService).save(any());
        Response response = linkedAuthorizationResource.saveAssociation(new LinkedAuthorization());
        assertEquals(400,response.getStatus());
    }

    /**
     * Tests creation of an invalid association, will return RuntimeException
     * @throws UniquenessConstraintException in case record has duplicated fields
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testCreateGenericError() throws Exception {
        doThrow(new RuntimeException()).when(linkedAuthorizationBusinessService).save(any());
        Response response = linkedAuthorizationResource.saveAssociation(new LinkedAuthorization());
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests creation of an invalid association, will return LinkedAuthorizationNotFoundException
     * @throws UniquenessConstraintException in case record has duplicated fields
     * @throws LinkedAuthorizationNotFoundException in case record not found
     */
    @Test
    public void testCreateNotFoundError() throws Exception {
        doThrow(new LinkedAuthorizationNotFoundException()).when(linkedAuthorizationBusinessService).save(any());
        Response response = linkedAuthorizationResource.saveAssociation(new LinkedAuthorization());
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testValidateRole() {
        when(linkedAuthorizationBusinessService.existsSpecificAssociation(any())).thenReturn(true);
        Response response = linkedAuthorizationResource.existsSpecificAssociation(2L, 2L, 2L, 2L, true);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testValidateRoleException() {
        when(linkedAuthorizationBusinessService.existsSpecificAssociation(any())).thenReturn(false);
        Response response = linkedAuthorizationResource.existsSpecificAssociation(2L, 2L, 2L, 2L, true);
        assertEquals(404 ,response.getStatus());
    }
}