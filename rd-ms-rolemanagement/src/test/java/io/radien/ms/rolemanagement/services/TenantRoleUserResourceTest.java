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

import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;

import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleUserException;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
/**
 * TenantRoleUser resource requests test
 * {@link io.radien.ms.rolemanagement.services.TenantRoleUserResource}
 *
 * @author Newton Carvalho
 */
public class TenantRoleUserResourceTest {

    @InjectMocks
    TenantRoleUserResource tenantRoleUserResource;

    @Mock
    TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    @Mock
    TenantRoleUserBusinessService tenantRoleUserBusinessService;

    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests response from getUsers method
     */
    @Test
    public void testGetUsers() {
        Response response = tenantRoleUserResource.getAll(1L,
                2, 3);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getUsers method when exceptions occur
     * during the processing
     */
    @Test
    public void testGetUserWithException() {
        doThrow(new RuntimeException("error")).
                when(tenantRoleUserServiceAccess).getAll(1L,
                2, 3);
        Response response = tenantRoleUserResource.getAll(1L,
                2, 3);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test asserts Response from
     * UnAssignedUserTenantRoles
     */
    @Test
    public void testUnAssignUserTenantRoles() {
        Response response = tenantRoleUserResource.unAssignUserTenantRoles(1L, 1L, new HashSet<>(Arrays.asList(1L, 2L)));
        assertEquals(200, response.getStatus());
    }

    /**
     * Test asserts Response from
     * UnAssignedUserTenantRoles
     */
    @Test
    public void testUnAssignUserTenantRolesException() throws TenantRoleException, TenantRoleUserException {
        doThrow(TenantRoleUserException.class).when(tenantRoleUserBusinessService).unAssignUserTenantRoles(anyLong(), anyLong(), anyCollection());
        Response response = tenantRoleUserResource.unAssignUserTenantRoles(1L,
        1L, new HashSet<>( Arrays.asList(1L, 1L)));
        assertEquals(400, response.getStatus());

        doThrow(TenantRoleException.class).when(tenantRoleUserBusinessService).unAssignUserTenantRoles(anyLong(), anyLong(), anyCollection());
        Response response1 = tenantRoleUserResource.unAssignUserTenantRoles(1L,
                1L, new HashSet<>( Arrays.asList(1L, 1L)));
        assertEquals(500, response1.getStatus());
    }
}
