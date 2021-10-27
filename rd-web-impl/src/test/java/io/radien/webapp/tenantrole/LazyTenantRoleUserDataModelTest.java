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
package io.radien.webapp.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserFactory;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.webapp.LazyAbstractDataModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

/**
 * Class that aggregates UnitTest cases for {@link LazyTenantRoleUserDataModel}
 * @author Newton Carvalho
 */
public class LazyTenantRoleUserDataModelTest {
    @Mock
    private TenantRoleUserRESTServiceAccess service;

    @Mock
    private UserRESTServiceAccess userService;

    private final Map<String, FilterMeta> filterMetaMap = new HashMap<>();
    private final Map<String, SortMeta> sortMetaMap = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Given a size, assemblies a very simple mocked page (Containing TenantRoleUser instances)
     * @param size informed page size
     * @return a mocked page containing SystemTenantRoleUser instances
     */
    protected Page<? extends SystemTenantRoleUser> setupMockedPage(int size) {
        List<SystemTenantRoleUser> list = new ArrayList<>();

        Page<SystemTenantRoleUser> page = new Page<>();
        for (long i=1; i<=size; i++) {
            SystemTenantRoleUser tenantRoleUser = TenantRoleUserFactory.create(i, i, null);
            tenantRoleUser.setId(i);
            list.add(tenantRoleUser);
        }
        page.setResults(list);
        page.setCurrentPage(1);
        page.setTotalPages(1);
        page.setTotalResults(size);

        return page;
    }

    /**
     * Assemblies a mocked user list
     * @param ids parameter that corresponds to a list containing tenant identifiers,
     *            and will be used to create others attribute values as well
     * @return a mocked list containing user
     */
    protected List<SystemUser> setupMockedUsers(List<Long> ids) {
        List<SystemUser> users = new ArrayList<>();
        ids.forEach(id -> {
            String valueAsString = String.valueOf(id);
            SystemUser user = UserFactory.create(valueAsString, valueAsString, valueAsString,
                        valueAsString, valueAsString, null);
            user.setId(id);
            users.add(user);
        });
        return users;
    }

    /**
     * Test for {@link LazyTenantRoleAssociationDataModel#getData(int, int, Map, Map)}
     *
     * In this case will be tested the workflow that corresponds the retrieval of a page
     * containing SystemTenantRole instances. Taking in consideration the page index/number and
     * page size.
     *
     * Once the Page is retrieved, then the associated Tenants and Roles also must be retrieved
     * and stored into internal caches.
     *
     * @throws SystemException exception that describes communication issues with tenant or tenant role endpoint
     * @throws Exception still threw by role service
     */
    @Test
    public void testGetData() throws SystemException, Exception {
        int pageNo = 0;
        int pageSize = 10;
        Long tenantId = 111L;
        Long roleId = 112L;
        List<Long> ids = LongStream.rangeClosed(1, pageSize).boxed().collect(Collectors.toList());

        when(service.getUsers(tenantId, roleId, pageNo+1, pageSize)).then(i -> setupMockedPage(pageSize));
        when(userService.getUsersByIds(ids)).then(i -> setupMockedUsers(ids));

        LazyTenantRoleUserDataModel lazyModel = new LazyTenantRoleUserDataModel(service, userService);
        lazyModel.setTenantId(tenantId);
        lazyModel.setRoleId(roleId);
        List<SystemTenantRoleUser> toBeShown = lazyModel.load(pageNo, pageSize,
                sortMetaMap, filterMetaMap);

        // Evaluating collection retrieved
        assertNotNull(toBeShown);
        assertFalse(toBeShown.isEmpty());
        assertEquals(toBeShown.size(), pageSize);

        // Evaluating information stored in the caches
        Long userId =  1L;
        assertNotNull(lazyModel.getUser(userId));
        assertEquals(lazyModel.getUser(userId).getId(), userId);

        // Invoke again
        toBeShown = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        // Evaluating again
        assertNotNull(toBeShown);
        assertFalse(toBeShown.isEmpty());
        assertEquals(toBeShown.size(), pageSize);
    }

    /**
     * Test for setter method {@link LazyTenantRoleUserDataModel#setTenantId(Long)}
     */
    @Test
    public void testGetterSetterForTenantId() {
        Long tenantId = 1000L;
        LazyTenantRoleUserDataModel lazyModel = new LazyTenantRoleUserDataModel(null, null);
        lazyModel.setTenantId(tenantId);
        assertEquals(tenantId, lazyModel.getTenantId());
    }

    /**
     * Test for setter method {@link LazyTenantRoleUserDataModel#setRoleId(Long)}
     */
    @Test
    public void testGetterSetterForRoleId() {
        Long roleId = 1000L;
        LazyTenantRoleUserDataModel lazyModel = new LazyTenantRoleUserDataModel(null, null);
        lazyModel.setRoleId(roleId);
        assertEquals(roleId, lazyModel.getRoleId());
    }

    /**
     * Test for method {@link LazyTenantRoleUserDataModel#getRowData(String)}
     */
    @Test
    public void testGetRowData() {
        TenantRoleUser tenantRoleUser = TenantRoleUserFactory.create(1L,1L,1L);
        tenantRoleUser.setId(111L);

        String rowKey = String.valueOf(tenantRoleUser.getId());

        LazyTenantRoleUserDataModel lazySpied = spy(new LazyTenantRoleUserDataModel(null, null));
        doReturn(tenantRoleUser).when((LazyAbstractDataModel)lazySpied).getRowData(rowKey);

        SystemTenantRoleUser systemTenantRoleUser = lazySpied.getRowData(rowKey);
        assertNotNull(systemTenantRoleUser);
        assertEquals(tenantRoleUser, systemTenantRoleUser);

        systemTenantRoleUser = lazySpied.getRowData("null");
        assertNull(systemTenantRoleUser);
    }
}