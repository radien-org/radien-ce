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

import io.radien.exception.LinkedAuthorizationException;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.entities.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
    @Mock
    HttpServletRequest servletRequest;

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
        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
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
        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
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
        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
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
    public void testValidateRoleNotFoundCase() {
        when(linkedAuthorizationBusinessService.existsSpecificAssociation(any())).thenReturn(false);
        Response response = linkedAuthorizationResource.existsSpecificAssociation(2L, 2L, 2L, 2L, true);
        assertEquals(404 ,response.getStatus());
    }

    @Test
    public void testValidateRoleException() {
        when(linkedAuthorizationBusinessService.existsSpecificAssociation(any())).thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.existsSpecificAssociation(2L, 2L, 2L, 2L, true);
        assertEquals(500 ,response.getStatus());
    }

    /**
     * Test the Get total records count request which will return a success message code 200.
     */
    @Test
    public void testGetTotalRecordsCount() {
        Response response = linkedAuthorizationResource.getTotalRecordsCount();
        assertEquals(200,response.getStatus());
    }

    /**
     * Test the Get total records count request which will return a error message code 500.
     */
    @Test
    public void testGetTotalRecordsCountException() {
        when(linkedAuthorizationResource.getTotalRecordsCount())
                .thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.getTotalRecordsCount();
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testGetRolesByUserAndTenant() {
        Long userId = 1L;
        Long tenantId = 2L;

        List<Role> roleList = new java.util.ArrayList<>();

        Role role = new Role();
        role.setId(1L);
        role.setName("admin");
        role.setDescription("admin");
        roleList.add(role);

        when(linkedAuthorizationBusinessService.getRolesByUserAndTenant(userId, tenantId)).
                then(i -> roleList);

        Response response = linkedAuthorizationResource.getRoles(userId, tenantId);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        assertEquals(response.getEntity(), roleList);
    }

    @Test
    public void testGetRolesByUserAndTenant404Case() {
        Response response = linkedAuthorizationResource.getRoles(null, null);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testGetRolesByUserAndTenantWithException() {
        Long userId = 1L;
        Long tenantId = 2L;
        when(linkedAuthorizationResource.getRoles(userId, tenantId))
                .thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.getRoles(userId, tenantId);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCheckIfRoleExistForUserTenant() {
        Long userId = 1L;
        Long tenantId = 2L;
        String roleName = "test";
        when(linkedAuthorizationBusinessService.isRoleExistentForUser(userId, null, roleName))
                .thenReturn(Boolean.TRUE);
        when(linkedAuthorizationBusinessService.isRoleExistentForUser(userId, tenantId, roleName))
                .thenReturn(Boolean.FALSE);
        Response r = linkedAuthorizationResource.isRoleExistentForUser(userId, roleName, null);
        assertEquals(r.getEntity(), Boolean.TRUE);
        r = linkedAuthorizationResource.isRoleExistentForUser(userId, roleName, tenantId);
        assertEquals(r.getEntity(), Boolean.FALSE);
    }

    @Test
    public void testCheckIfRoleExistForUserTenant404Case() {
        Response response = linkedAuthorizationResource.isRoleExistentForUser(null, null, null);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testCheckIfRoleExistForUserTenantWithException() {
        Long userId = 1L;
        Long tenantId = 2L;
        String roleName = "test";
        when(linkedAuthorizationResource.isRoleExistentForUser(userId, roleName, tenantId))
                .thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.isRoleExistentForUser(userId, roleName, tenantId);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testGetRoles() {
        Response response = linkedAuthorizationResource.getRoles(2L, 2L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testGetRolesException() {
        when(linkedAuthorizationResource.getRoles(anyLong(), anyLong()))
                .thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.getRoles(2L, 2L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testIsRoleExistentForUser() {
        Response response = linkedAuthorizationResource.isRoleExistentForUser(2L, "test", 2L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testIsRoleExistentForUserException() {
        when(linkedAuthorizationBusinessService.isRoleExistentForUser(anyLong(), anyLong(), anyString()))
                .thenThrow(new RuntimeException());
        Response response = linkedAuthorizationResource.isRoleExistentForUser(2L, "test", 2L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCheckPermissions() {
        List<String> roleList = new ArrayList<>();
        Response response = linkedAuthorizationResource.checkPermissions(2L, roleList, 2L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testCheckPermissionsException() {
        when(linkedAuthorizationBusinessService.checkPermissions(anyLong(), anyLong(), anyList()))
                .thenThrow(new RuntimeException());
        List<String> roleList = new ArrayList<>();
        Response response = linkedAuthorizationResource.checkPermissions(2L, roleList, 2L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCheckPermissionsNotFound() {
        List<String> roleList = new ArrayList<>();
        Response response = linkedAuthorizationResource.checkPermissions(null, roleList, null);
        assertEquals(404,response.getStatus());
    }

    /**
     * Test for dissociation process (for tenant and user) when a generic exception occurs
     * Expected: Http Status 500
     */
    @Test
    public void testDissociation() {
        Response response = linkedAuthorizationResource.deleteAssociations(1l,null,null,1l);
        assertEquals(200,response.getStatus());
    }

    /**
     * Test for dissociation process (for tenant and user) when a generic exception occurs
     * Expected: Http Status 500
     */
    @Test
    public void testDissociateTenantGenericError() throws LinkedAuthorizationException, LinkedAuthorizationNotFoundException{
        doThrow(new RuntimeException()).when(linkedAuthorizationBusinessService).
                deleteAssociations(1L, 1l,1l,1L);
        Response response = linkedAuthorizationResource.deleteAssociations(1L,1l, 1l,1L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Test for dissociation process (for tenant and user) when there
     * is no associations available
     * Expected: Http Status 404
     */
    @Test
    public void testDissociateTenantUserNotFoundError() throws LinkedAuthorizationException, LinkedAuthorizationNotFoundException {
        doThrow(new LinkedAuthorizationNotFoundException()).when(linkedAuthorizationBusinessService).
                deleteAssociations(1L, 1l,1l,1L);
        Response response = linkedAuthorizationResource.deleteAssociations(1L, 1l,1l,1L);
        assertEquals(404,response.getStatus());
    }
}