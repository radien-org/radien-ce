/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.rolemanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.role.exception.TenantRoleUserNotFoundException;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.*;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUserSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;

import java.util.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Class that aggregates UnitTest cases for
 * TenantRoleUserBusinessService
 *
 * @author Rajesh Gavvala
 */
public class TenantRoleUserBusinessServiceTest {

    @InjectMocks
    private TenantRoleUserBusinessService tenantRoleUserBusinessService;

    @Mock
    private TenantRoleBusinessService tenantRoleService;

    @Mock
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    @Mock
    private RoleServiceAccess roleServiceAccess;

    @Mock
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;
    @Mock
    private TenantRESTServiceAccess tenantRESTService;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    Long userId = 1L;
    Long tenantId = 2L;
    Collection<Long> roleIds;
    List<Long> tenantRoleIds = new ArrayList<>();
    List<Long> tenantRoleUserIds;


    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp(){
        roleIds = new HashSet<>( Arrays.asList(3L, 4L));
        tenantRoleIds.add(5L);
        tenantRoleUserIds = Arrays.asList(6L, 7L);
    }

    @Test
    public void testGet() {
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(new TenantRoleUser());
        assertNotNull(tenantRoleUserBusinessService.get(1L));
    }

    @Test
    public void testGetAll() {
        Page<SystemTenantRoleUser> result = new Page<>(new ArrayList<>(), 1, 1, 1);
        when(tenantRoleUserServiceAccess.getAll(anyLong(), anyLong(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(result);

        assertEquals(result, tenantRoleUserBusinessService.getAll(1L, 1L, 1, 1, new ArrayList<>(), false));
    }


    @Test
    public void testGetNotFound() {
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(null);
        assertThrows(TenantRoleUserNotFoundException.class, () -> tenantRoleUserBusinessService.get(1L));
    }


    @Test
    public void testGetAllUserIds() {
        Page<Long> result = new Page<>(new ArrayList<>(), 1, 1, 1);
        when(tenantRoleUserServiceAccess.getAllUserIds(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(result);

        assertEquals(result, tenantRoleUserBusinessService.getAllUserIds(1L, 1L, 1, 1));
    }

    @Test
    public void testGetFiltered() {
        assertTrue(tenantRoleUserBusinessService.getFiltered(new TenantRoleUserSearchFilter()).isEmpty());
    }

    @Test
    public void testAssignUser() throws InvalidArgumentException, UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), anyLong()))
                .thenReturn(false);

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        tenantRoleUserBusinessService.assignUser(tenantRoleUser);
        verify(tenantRoleUserServiceAccess).create(any(TenantRoleUserEntity.class));
    }

    @Test
    public void testAssignUserAlreadyExisting() throws InvalidArgumentException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any()))
                .thenReturn(true);

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.assignUser(tenantRoleUser));
    }

    @Test
    public void testAssignUserInvalidArgument() throws InvalidArgumentException, UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any()))
                .thenReturn(false);
        doThrow(new InvalidArgumentException("error"))
                .when(tenantRoleUserServiceAccess).create(any(SystemTenantRoleUser.class));


        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.assignUser(tenantRoleUser));
    }

    @Test
    public void testAssignUserDuplicatedData() throws InvalidArgumentException, UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any()))
                .thenReturn(false);
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantRoleUserServiceAccess).create(any(SystemTenantRoleUser.class));


        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        assertThrows(RoleException.class, () -> tenantRoleUserBusinessService.assignUser(tenantRoleUser));
    }

    @Test
    public void testAssignUserIfProcessingIsLocked() throws SystemException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(userRESTServiceAccess.isProcessingLocked(anyLong())).thenReturn(true);

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);

        assertThrows(ProcessingLockedException.class, () -> {
                tenantRoleUserBusinessService.assignUser(tenantRoleUser);
        });
        verify(tenantRoleService,never()).getById(anyLong());
    }

    @Test
    public void testUpdate() throws InvalidArgumentException, UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), anyLong()))
                .thenReturn(false);

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        tenantRoleUserBusinessService.update(2L, tenantRoleUser);
        verify(tenantRoleUserServiceAccess).update(any(TenantRoleUserEntity.class));
    }

    @Test
    public void testUpdateIfProcessingIsLocked() throws SystemException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(userRESTServiceAccess.isProcessingLocked(1L)).thenReturn(true);
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);

        assertThrows(ProcessingLockedException.class, () -> {
            tenantRoleUserBusinessService.update(2L, tenantRoleUser);
        });
        verify(tenantRoleService,never()).getById(anyLong());
    }

    @Test
    public void testUpdateAlreadyExisting() throws InvalidArgumentException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any()))
                .thenReturn(true);

        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.update(2L, tenantRoleUser));
    }

    @Test
    public void testUpdateInvalidArgument() throws InvalidArgumentException, UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any()))
                .thenReturn(false);
        doThrow(new InvalidArgumentException("error"))
                .when(tenantRoleUserServiceAccess).update(any(SystemTenantRoleUser.class));


        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.update(2L, tenantRoleUser));
    }

    @Test
    public void testUpdateDuplicatedData() throws InvalidArgumentException, UniquenessConstraintException {
        SystemTenantRole tenantRole = new TenantRoleEntity();
        tenantRole.setRoleId(1L);
        tenantRole.setTenantId(1L);
        when(tenantRoleService.getById(anyLong()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any()))
                .thenReturn(false);
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantRoleUserServiceAccess).update(any(SystemTenantRoleUser.class));


        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        assertThrows(RoleException.class, () -> tenantRoleUserBusinessService.update(2L, tenantRoleUser));
    }

    @Test
    public void testDeleteStillAssociated() throws InvalidArgumentException {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(tenantRoleUser);
        when(tenantRoleService.getById(tenantRoleUser.getTenantRoleId()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.delete(anyLong()))
                .thenReturn(true);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(1L, 1L))
                .thenReturn(true);

        tenantRoleUserBusinessService.delete(1L);
        verify(tenantRoleUserServiceAccess).delete(1L);
    }

    @Test
    public void testDeleteStillAssociatedInvalidArgument() throws InvalidArgumentException {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(tenantRoleUser);
        when(tenantRoleService.getById(tenantRoleUser.getTenantRoleId()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.delete(anyLong()))
                .thenReturn(true);
        when(tenantRoleUserServiceAccess.isAssociatedWithTenant(1L, 1L))
                .thenThrow(new InvalidArgumentException("error"));

        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.delete(1L));
    }

    @Test
    public void testDeleteNoMoreAssociation() throws InvalidArgumentException, SystemException {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(tenantRoleUser);
        when(tenantRoleService.getById(tenantRoleUser.getTenantRoleId()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.delete(anyLong()))
                .thenReturn(true);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(1L, 1L))
                .thenReturn(false);

        tenantRoleUserBusinessService.delete(1L);
        verify(tenantRoleUserServiceAccess).delete(1L);
        verify(activeTenantRESTServiceAccess).deleteByTenantAndUser(1L, 1L);
    }

    @Test
    public void testDeleteNoMoreAssociationRoleException() throws InvalidArgumentException, SystemException {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(tenantRoleUser);
        when(tenantRoleService.getById(tenantRoleUser.getTenantRoleId()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.delete(anyLong()))
                .thenReturn(true);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(1L, 1L))
                .thenReturn(false);
        doThrow(new SystemException("error"))
                .when(activeTenantRESTServiceAccess).deleteByTenantAndUser(1L, 1L);

        assertThrows(RoleException.class, () -> tenantRoleUserBusinessService.delete(1L));
    }

    @Test
    public void testDeleteNotFound() {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        TenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(tenantRoleUser);
        when(tenantRoleService.getById(tenantRoleUser.getTenantRoleId()))
                .thenReturn(tenantRole);
        when(tenantRoleUserServiceAccess.delete(anyLong()))
                .thenReturn(false);

        assertThrows(TenantRoleUserNotFoundException.class, () -> tenantRoleUserBusinessService.delete(1L));
    }

    @Test
    public void testDeleteIfProcessingIsLocked() throws SystemException {
        TenantRoleUser tenantRoleUser = new TenantRoleUser();
        tenantRoleUser.setTenantRoleId(1L);
        tenantRoleUser.setUserId(1L);
        when(tenantRoleUserServiceAccess.get(anyLong()))
                .thenReturn(tenantRoleUser);

        when(userRESTServiceAccess.isProcessingLocked(anyLong())).thenReturn(true);
        assertThrows(ProcessingLockedException.class, () -> tenantRoleUserBusinessService.delete(1L));
        verify(tenantRoleService,never()).getById(anyLong());
    }

    @Test
    public void testUnAssignUser() throws InvalidArgumentException {
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyLong(), anyList(), anyLong()))
                .thenReturn(Collections.singletonList(1L));
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(1L, 1L))
                .thenReturn(true);

        tenantRoleUserBusinessService.unAssignUser(1L, Collections.singletonList(1L), 1L);
        verify(tenantRoleUserServiceAccess).delete(anyList());
    }

    @Test
    public void testUnAssignUserInvalidArgument() throws InvalidArgumentException {
        Collection<Long> roles = Arrays.asList(1L, 2L);
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyLong(), anyList(), anyLong()))
                .thenReturn(Collections.singletonList(1L));
        doThrow(new InvalidArgumentException("error"))
                .when(tenantRoleUserServiceAccess).delete(anyList());

        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.unAssignUser(1L, roles, 1L));
    }

    @Test
    public void testUnAssignUserNotFound() throws InvalidArgumentException {
        Collection<Long> roles = Arrays.asList(1L, 2L);
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyLong(), anyList(), anyLong()))
                .thenReturn(new ArrayList<>());

        assertThrows(TenantRoleUserNotFoundException.class, () -> tenantRoleUserBusinessService.unAssignUser(1L, roles, 1L));
    }

    @Test
    public void testGetTenants() throws InvalidArgumentException, SystemException {
        List<Long> tenantIds = Arrays.asList(1L, 2L);
        when(tenantRoleUserServiceAccess.getTenants(anyLong(), anyLong()))
                .thenReturn(tenantIds);
        when(tenantRESTService.getTenantsByIds(tenantIds))
                .thenReturn(new ArrayList<>());

        assertTrue(tenantRoleUserBusinessService.getTenants(1L, 1L).isEmpty());
    }

    @Test
    public void testUnAssignUserIfProcessingIsLocked() throws SystemException, InvalidArgumentException {
        when(userRESTServiceAccess.isProcessingLocked(anyLong())).thenReturn(true);
        Collection<Long> roles = Collections.singletonList(1L);
        assertThrows(ProcessingLockedException.class, () -> {
            tenantRoleUserBusinessService.unAssignUser(1L, roles, 1L);
        });
        verify(tenantRoleUserServiceAccess,never()).getTenantRoleUserIds(1L, Collections.singletonList(1L), 1L);
    }

    @Test
    public void testGetTenantsSystemException() throws InvalidArgumentException, SystemException {
        List<Long> tenantIds = Arrays.asList(1L, 2L);
        when(tenantRoleUserServiceAccess.getTenants(anyLong(), anyLong()))
                .thenReturn(tenantIds);
        when(tenantRESTService.getTenantsByIds(tenantIds))
                .thenThrow(new SystemException("error"));

        assertThrows(RoleException.class, () -> tenantRoleUserBusinessService.getTenants(1L, 1L));
    }

    @Test
    public void testGetTenantsInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleUserServiceAccess.getTenants(anyLong(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));

        assertThrows(BadRequestException.class, () -> tenantRoleUserBusinessService.getTenants(1L, 1L));
    }

    @Test
    public void testGetRolesForUserTenant() {
        assertTrue(tenantRoleUserBusinessService.getRolesForUserTenant(1L, 1L).isEmpty());
    }

    @Test
    public void testGetCount() {
        when(tenantRoleUserServiceAccess.count()).thenReturn(10L);
        assertEquals(10L, tenantRoleUserBusinessService.getCount());
    }

    @Test
    public void testCheckIfUserIsLocked() throws SystemException {
        when(userRESTServiceAccess.isProcessingLocked(0L)).thenReturn(false);
        assertFalse(tenantRoleUserBusinessService.checkIfUserIsLocked(0L));

        when(userRESTServiceAccess.isProcessingLocked(0L)).thenReturn(true);
        assertTrue(tenantRoleUserBusinessService.checkIfUserIsLocked(0L));
    }


    @Test
    public void testCheckIfUserIsLockedThrowsException() throws SystemException, RuntimeException {
        when(userRESTServiceAccess.isProcessingLocked(0L)).thenThrow(new SystemException());
        assertThrows(RuntimeException.class, () ->
                tenantRoleUserBusinessService.checkIfUserIsLocked(0L));
    }
}