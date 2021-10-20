/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.SystemVariables;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.TenantRoleUserException;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static io.radien.exception.GenericErrorCodeMessage.GENERIC_ERROR;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for
 * TenantRoleUserBusinessService
 *
 * @author Rajesh Gavvala
 */
class TenantRoleUserBusinessServiceTest {

    @InjectMocks
    private TenantRoleUserBusinessService tenantRoleUserBusinessService;

    @Mock
    private TenantRoleServiceAccess tenantRoleServiceAccess;

    @Mock
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    Long userId = 1L;
    Long tenantId = 2L;
    Collection<Long> roleIds;
    List<Long> tenantRoleIds = new ArrayList<>();
    List<Long> tenantRoleUserIds;


    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);

        roleIds = new HashSet<>( Arrays.asList(3L, 4L));
        tenantRoleIds.add(5L);
        tenantRoleUserIds = Arrays.asList(6L, 7L);
    }

    @Test
    void testUnAssignUserTenantRoles() throws TenantRoleException, TenantRoleUserException, SystemException {
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantId, roleIds, userId)).
                thenReturn(tenantRoleUserIds);

        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyList(), anyLong())).thenReturn(tenantRoleUserIds);
        doReturn(true).when(tenantRoleUserServiceAccess).delete(anyCollection());

        assertEquals(tenantRoleUserIds, tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, userId));

        ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess = mock(ActiveTenantRESTServiceAccess.class);
        when(activeTenantRESTServiceAccess.deleteByTenantAndUser(tenantId, userId)).thenReturn(Boolean.TRUE);
        tenantRoleUserBusinessService.setActiveTenantRESTServiceAccess(activeTenantRESTServiceAccess);
        tenantRoleUserBusinessService.unAssignUser(tenantId, roleIds, userId);
    }

    @Test
    void testUnAssignUserTenantRolesException() throws TenantRoleException, TenantRoleUserException {
        when(tenantRoleServiceAccess.getTenantRoleIds(anyLong(), anyCollection())).thenThrow(TenantRoleException.class);
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyList(), anyLong())).thenThrow(TenantRoleUserException.class);

        assertThrows(TenantRoleException.class, () -> tenantRoleServiceAccess.getTenantRoleIds(tenantId, roleIds));
        assertThrows(TenantRoleUserException.class, () -> tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, userId));
    }

    /**
     * Test for method {@link TenantRoleUserBusinessService#update(TenantRoleUserEntity)}
     * in a well succeed scenario .
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testTenantRoleUserUpdateOK() throws SystemException {
        TenantRoleUserEntity trp = new TenantRoleUserEntity();
        trp.setId(1L);
        trp.setTenantRoleId(2L);
        trp.setUserId(3L);

        TenantRoleEntity tr = new TenantRoleEntity();
        tr.setId(trp.getTenantRoleId());

        when(tenantRoleServiceAccess.get(trp.getTenantRoleId())).thenReturn(tr);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(trp.getUserId(),
                trp.getTenantRoleId(), trp.getId())).thenReturn(false);

        assertDoesNotThrow(() -> tenantRoleUserBusinessService.update(trp));
    }

    /**
     * Test for method {@link TenantRoleUserBusinessService#update(TenantRoleUserEntity)}
     * in a bad succeed scenario where tenantRoleId does not exist in the database
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testUpdateTenantRoleNOK() throws SystemException {
        TenantRoleUserEntity trp = new TenantRoleUserEntity();
        trp.setId(1L);
        trp.setTenantRoleId(2L);
        trp.setUserId(3L);

        String errorMsg = TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(String.valueOf(
                trp.getTenantRoleId()));

        when(tenantRoleServiceAccess.get(trp.getTenantRoleId())).thenReturn(null);
        TenantRoleException tre = assertThrows(TenantRoleNotFoundException.class, () ->
                tenantRoleUserBusinessService.update(trp));
        assertEquals(errorMsg, tre.getMessage());
    }

    /**
     * Test for method {@link TenantRoleUserBusinessService#update(TenantRoleUserEntity)}
     * in a bad succeed scenario where userId was not informed
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testUpdateUserIdNOK() throws SystemException {
        TenantRoleUserEntity trp = new TenantRoleUserEntity();
        String errorMsg = TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.USER_ID.getLabel());
        Exception e = assertThrows(TenantRoleIllegalArgumentException.class, () ->
                tenantRoleUserBusinessService.update(trp));
        assertEquals(errorMsg, e.getMessage());
    }

    /**
     * Test for method {@link TenantRoleUserBusinessService#update(TenantRoleUserEntity)}
     * in a bad succeed scenario where tenantRoleId was not informed
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testUpdateWhenTenantRoleIdNotInformed() throws SystemException {
        TenantRoleUserEntity trp = new TenantRoleUserEntity();
        trp.setUserId(1L);
        String errorMsg = TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ROLE_ID.getLabel());
        Exception e = assertThrows(TenantRoleIllegalArgumentException.class, () ->
                tenantRoleUserBusinessService.update(trp));
        assertEquals(errorMsg, e.getMessage());
    }

    /**
     * Test for method {@link TenantRoleUserBusinessService#update(TenantRoleUserEntity)}
     * in a bad succeed scenario where the combination of tenantRoleId and permissionId already
     * exist for another TenantRoleUser entity/row (Comparison taking in consideration the id).
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testUpdateAssociationAlreadyExist() throws SystemException {
        TenantRoleUserEntity trp = new TenantRoleUserEntity();
        trp.setId(1L);
        trp.setTenantRoleId(2L);
        trp.setUserId(3L);

        TenantRoleEntity tr = new TenantRoleEntity();
        tr.setId(trp.getTenantRoleId());
        tr.setTenantId(222L);
        tr.setRoleId(333L);

        String errorMsg = GENERIC_ERROR.TENANT_ROLE_USER_IS_ALREADY_ASSOCIATED.
                toString(String.valueOf(tr.getTenantId()), String.valueOf(tr.getRoleId())) ;

        when(tenantRoleServiceAccess.get(trp.getTenantRoleId())).thenReturn(tr);
        when(tenantRoleUserServiceAccess.isAssociationAlreadyExistent(trp.getUserId(),
                trp.getTenantRoleId(), trp.getId())).thenReturn(true);

        TenantRoleException tre = assertThrows(TenantRoleException.class, () ->
                tenantRoleUserBusinessService.update(trp));
        assertEquals(errorMsg, tre.getMessage());
    }

}