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
package io.radien.ms.rolemanagement.services;

import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleUserException;
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
public class TenantRoleUserBusinessServiceTest {

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
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        roleIds = new HashSet<>( Arrays.asList(3L, 4L));
        tenantRoleIds.add(5L);
        tenantRoleUserIds = Arrays.asList(6L, 7L);
    }

    @Test
    public void testUnAssignUserTenantRoles() throws TenantRoleException, TenantRoleUserException, SystemException {
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantId, roleIds, userId)).
                thenReturn(tenantRoleUserIds);

        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyList(), anyLong())).thenReturn(tenantRoleUserIds);
        doReturn(true).when(tenantRoleUserServiceAccess).delete(anyCollection());

        Assertions.assertEquals(tenantRoleUserIds, tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, userId));

        ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess = mock(ActiveTenantRESTServiceAccess.class);
        when(activeTenantRESTServiceAccess.deleteByTenantAndUser(tenantId, userId)).thenReturn(Boolean.TRUE);
        tenantRoleUserBusinessService.setActiveTenantRESTServiceAccess(activeTenantRESTServiceAccess);
        tenantRoleUserBusinessService.unAssignUser(tenantId, roleIds, userId);
    }

    @Test
    public void testUnAssignUserTenantRolesException() throws TenantRoleException, TenantRoleUserException {
        when(tenantRoleServiceAccess.getTenantRoleIds(anyLong(), anyCollection())).thenThrow(TenantRoleException.class);
        when(tenantRoleUserServiceAccess.getTenantRoleUserIds(anyList(), anyLong())).thenThrow(TenantRoleUserException.class);

        Assertions.assertThrows(TenantRoleException.class, () -> tenantRoleServiceAccess.getTenantRoleIds(tenantId, roleIds));
        Assertions.assertThrows(TenantRoleUserException.class, () -> tenantRoleUserServiceAccess.getTenantRoleUserIds(tenantRoleIds, userId));
    }

}