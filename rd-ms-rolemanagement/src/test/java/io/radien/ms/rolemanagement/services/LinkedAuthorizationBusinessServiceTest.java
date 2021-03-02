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

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorizationSearchFilter;
import io.radien.ms.rolemanagement.factory.LinkedAuthorizationFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationBusinessServiceTest extends TestCase {

    @InjectMocks
    @Spy
    LinkedAuthorizationBusinessService linkedAuthorizationBusinessService;
    @Mock
    LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;

    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Mock
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Mock
    private RoleServiceAccess roleServiceAccess;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAssociationById() throws LinkedAuthorizationNotFoundException {
        SystemLinkedAuthorization systemLinkedAuthorization = LinkedAuthorizationFactory.create(3L, 3L, 3L, 3L, 3L);
        when(linkedAuthorizationServiceAccess.getAssociationById(3L)).thenReturn(systemLinkedAuthorization);
        SystemLinkedAuthorization result = linkedAuthorizationBusinessService.getAssociationById(3L);
        assertEquals(systemLinkedAuthorization,result);
    }

    @Test
    public void testGetAll() {
        Page<SystemLinkedAuthorization> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(linkedAuthorizationServiceAccess.getAll(1, 10)).thenReturn(p);
        Page<? extends SystemLinkedAuthorization> result = linkedAuthorizationBusinessService.getAll(1,10);
        assertEquals(p,result);
    }

    @Test
    public void testGetSpecificAssociation() {
        List<? extends SystemLinkedAuthorization> list = linkedAuthorizationBusinessService.getSpecificAssociation(new LinkedAuthorizationSearchFilter
                (2L, 2L, 2L, 2L, true));
        assertEquals(0,list.size());
    }

    @Test
    public void testSave() throws Exception {
        LinkedAuthorization u = LinkedAuthorizationFactory.create(4L, 4L, 4L, 4L, 4L);
        doReturn(true).when(linkedAuthorizationBusinessService).checkIfFieldsAreValid(any());
        linkedAuthorizationBusinessService.save(u);
    }

    @Test
    public void testCheckIfFieldsAreValid() throws Exception {
        LinkedAuthorization u = LinkedAuthorizationFactory.create(4L, 4L, 4L, 4L, 4L);
        when(permissionRESTServiceAccess.isPermissionExistent(any(), any())).thenReturn(true);
        when(tenantRESTServiceAccess.isTenantExistent(any())).thenReturn(true);
        boolean success = false;
        try{
            linkedAuthorizationBusinessService.save(u);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testSaveException() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        LinkedAuthorization u = LinkedAuthorizationFactory.create(4L, 4L, 4L, 4L, 4L);
        doThrow(new LinkedAuthorizationNotFoundException("")).when(linkedAuthorizationServiceAccess).save(u);
        boolean success = false;
        try{
            linkedAuthorizationServiceAccess.save(u);
        } catch (LinkedAuthorizationNotFoundException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testCheckIfRoleExists() {
        when(roleServiceAccess.checkIfRolesExist(3L, null)).thenReturn(true);
        boolean result = roleServiceAccess.checkIfRolesExist(3L, null);
        assertTrue(result);
    }

    @Test
    public void testCheckIfRoleExistsException() {
        when(roleServiceAccess.checkIfRolesExist(any(), any())).thenReturn(false);
        boolean result = roleServiceAccess.checkIfRolesExist(3L, null);
        assertFalse(result);
    }

    @Test
    public void testDeleteAssociationException() throws LinkedAuthorizationNotFoundException {
        LinkedAuthorization u = LinkedAuthorizationFactory.create(4L, 4L, 4L, 4L, 4L);
        u.setId(10L);
        doThrow(new LinkedAuthorizationNotFoundException("")).when(linkedAuthorizationServiceAccess).deleteAssociation(u.getId());
        boolean success = false;
        try{
            linkedAuthorizationBusinessService.deleteAssociation(u.getId());
        } catch (LinkedAuthorizationNotFoundException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testValidateRole() {
        boolean success = false;
        try{
            SystemLinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(2L, 2L, 2L, 2L, true);
            linkedAuthorizationBusinessService.existsSpecificAssociation(filter);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }
}