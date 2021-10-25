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
package io.radien.webapp.util;

import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for {@link TenantRoleUtil} manager
 * @author Newton Carvalho
 */
@RunWith(PowerMockRunner.class)
public class TenantRoleUtilTest {

    @InjectMocks
    private TenantRoleUtil target;

    @Mock
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Corresponds to the successful case regarding tenant role id retrieval
     * @throws SystemException for any kind of error regarding communication with
     * the endpoint
     * @throws TenantRoleIllegalArgumentException in case of not informed mandatory params
     */
    @Test
    public void testGetIdByTenantRole() throws SystemException, TenantRoleIllegalArgumentException {
        Long tenant = 1L, role = 1L, tenantRoleId = 2L;
        when(tenantRoleRESTServiceAccess.getIdByTenantRole(tenant, role)).
                thenReturn(Optional.of(tenantRoleId));
        assertEquals(tenantRoleId, target.getTenantRoleId(tenant, role));
    }

    /**
     * Corresponds to the unsuccessful case regarding tenant role id retrieval.
     * Tenant role id could not be found.
     * @throws SystemException for any kind of error regarding communication with
     * the endpoint
     * @throws SystemException for any kind of error regarding communication with
     * the endpoint
     * @throws TenantRoleIllegalArgumentException in case of not informed mandatory params
     */
    @Test(expected = NotFoundException.class)
    public void testIdNotFound() throws SystemException, TenantRoleIllegalArgumentException {
        Long tenant = 1L, role = 1L, tenantRoleId = 2L;
        when(tenantRoleRESTServiceAccess.getIdByTenantRole(tenant, role)).
                thenReturn(Optional.empty());
        target.getTenantRoleId(tenant, role);
    }

    /**
     * Corresponds to another unsuccessful case regarding tenant role id retrieval.
     * SystemException raised during communication with the endpoint
     * @throws SystemException for any kind of error regarding communication with
     * the endpoint.
     * @throws TenantRoleIllegalArgumentException in case of not informed mandatory params
     */
    @Test(expected = SystemException.class)
    public void testTryToGetIdWithSystemException() throws SystemException, TenantRoleIllegalArgumentException {
        Long tenant = 1L, role = 1L, tenantRoleId = 2L;
        when(tenantRoleRESTServiceAccess.getIdByTenantRole(tenant, role)).
                thenThrow(SystemException.class);
        target.getTenantRoleId(tenant, role);
    }

    /**
     * Corresponds to another unsuccessful case regarding tenant role id retrieval.
     * When tenant is not informed
     * @throws SystemException for any kind of error regarding communication with
     * the endpoint.
     * @throws TenantRoleIllegalArgumentException in case of not informed mandatory params
     */
    @Test(expected = TenantRoleIllegalArgumentException.class)
    public void testTryToGetIdWithNotInformedTenant() throws SystemException, TenantRoleIllegalArgumentException {
        Long role = 1L;
        target.getTenantRoleId(null, role);
    }

    /**
     * Corresponds to another unsuccessful case regarding tenant role id retrieval.
     * When role is not informed
     * @throws SystemException for any kind of error regarding communication with
     * the endpoint.
     * @throws TenantRoleIllegalArgumentException in case of not informed mandatory params
     */
    @Test(expected = TenantRoleIllegalArgumentException.class)
    public void testTryToGetIdWithNotInformedRole() throws SystemException, TenantRoleIllegalArgumentException {
        Long tenant = 1L;
        target.getTenantRoleId(tenant, null);
    }
}
