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

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.SystemVariables;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.client.PermissionClient;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * TenantRolePermissionBusinessService requests test
 * {@link TenantRolePermissionBusinessService}
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRolePermissionBusinessServiceTest {

    @InjectMocks
    TenantRolePermissionBusinessService target;

    @Mock
    PermissionRESTServiceAccess permissionClient;

    @Mock
    TenantRoleServiceAccess tenantRoleServiceAccess;

    @Mock
    TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;

    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link TenantRolePermissionBusinessService#update(TenantRolePermissionEntity)}
     * in a well succeed scenario .
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testTenantRolePermissionUpdateOK() throws SystemException {
        TenantRolePermissionEntity trp = new TenantRolePermissionEntity();
        trp.setId(1L);
        trp.setTenantRoleId(2L);
        trp.setPermissionId(3L);

        TenantRoleEntity tr = new TenantRoleEntity();
        tr.setId(trp.getTenantRoleId());

        when(permissionClient.isPermissionExistent(trp.getPermissionId(), null)).thenReturn(true);
        when(tenantRoleServiceAccess.get(trp.getTenantRoleId())).thenReturn(tr);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(trp.getPermissionId(),
                trp.getTenantRoleId(), trp.getId())).thenReturn(false);

        assertDoesNotThrow(() -> target.update(trp));
    }

    /**
     * Test for method {@link TenantRolePermissionBusinessService#update(TenantRolePermissionEntity)}
     * in a well succeed scenario .
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testUpdateTenantRoleNOK() throws SystemException {
        TenantRolePermissionEntity trp = new TenantRolePermissionEntity();
        trp.setId(1L);
        trp.setTenantRoleId(2L);
        trp.setPermissionId(3L);

        String errorMsg = TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(String.valueOf(
                trp.getTenantRoleId()));

        when(permissionClient.isPermissionExistent(trp.getPermissionId(), null)).thenReturn(true);
        when(tenantRoleServiceAccess.get(trp.getTenantRoleId())).thenReturn(null);
        TenantRoleException tre = assertThrows(TenantRoleNotFoundException.class, () -> target.update(trp));
        assertEquals(errorMsg, tre.getMessage());
    }

    /**
     * Test for method {@link TenantRolePermissionBusinessService#update(TenantRolePermissionEntity)}
     * in a bad succeed scenario where permissionId was not informed.
     */
    @Test
    void testTUpdatePermissionIdNOK() {
        String expectedErrorMsg = GenericErrorCodeMessage.
                TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel());
        TenantRolePermissionEntity trp = new TenantRolePermissionEntity();
        TenantRoleException tre = assertThrows(TenantRoleIllegalArgumentException.class, () -> target.update(trp));
        assertEquals(expectedErrorMsg, tre.getMessage());
    }

    /**
     * Test for method {@link TenantRolePermissionBusinessService#update(TenantRolePermissionEntity)}
     * in a bad succeed scenario where the combination of tenantRoleId and permissionId already
     * exist for another TenantRolePermission entity/row (Comparison taking in consideration the id).
     * @throws SystemException in case of communication issues with REST client
     */
    @Test
    void testUpdateAssociationAlreadyExist() throws SystemException {
        TenantRolePermissionEntity trp = new TenantRolePermissionEntity();
        trp.setId(1L);
        trp.setTenantRoleId(2L);
        trp.setPermissionId(3L);

        TenantRoleEntity tr = new TenantRoleEntity();
        tr.setId(trp.getTenantRoleId());
        tr.setTenantId(222L);
        tr.setRoleId(333L);

        String errorMsg = TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE.
                toString(String.valueOf(tr.getTenantId()), String.valueOf(tr.getRoleId()));

        when(permissionClient.isPermissionExistent(trp.getPermissionId(), null)).thenReturn(true);
        when(tenantRoleServiceAccess.get(trp.getTenantRoleId())).thenReturn(tr);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(trp.getPermissionId(),
                trp.getTenantRoleId(), trp.getId())).thenReturn(true);

        TenantRoleException tre = assertThrows(TenantRoleException.class, () -> target.update(trp));
        assertEquals(errorMsg, tre.getMessage());
    }
}
