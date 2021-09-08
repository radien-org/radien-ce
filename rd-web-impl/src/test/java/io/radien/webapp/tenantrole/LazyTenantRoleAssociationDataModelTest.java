/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.services.RoleFactory;
import io.radien.ms.rolemanagement.client.services.TenantRoleFactory;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.client.services.TenantFactory;
import io.radien.webapp.LazyAbstractDataModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for {@link LazyTenantRoleAssociationDataModel}
 * @author Newton Carvalho
 */
public class LazyTenantRoleAssociationDataModelTest {

    @Mock
    private TenantRoleRESTServiceAccess service;

    @Mock
    private RoleRESTServiceAccess roleService;

    @Mock
    private TenantRESTServiceAccess tenantService;

    private final Map<String, FilterMeta> filterMetaMap = new HashMap<>();
    private final Map<String, SortMeta> sortMetaMap = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Given a size, assemblies a very simple mocked page
     * @param size informed page size
     * @return a mocked page containing SystemTenantRole instances
     */
    protected Page<? extends SystemTenantRole> setupMockedPage(int size) {
        List<SystemTenantRole> list = new ArrayList<>();

        Page<SystemTenantRole> page = new Page<>();
        for (long i=1; i<=size; i++) {
            SystemTenantRole tenantRole = TenantRoleFactory.create(i, i, null);
            tenantRole.setId(i);
            list.add(tenantRole);
        }
        page.setResults(list);
        page.setCurrentPage(1);
        page.setTotalPages(1);
        page.setTotalResults(size);

        return page;
    }

    /**
     * Assemblies a mocked role collection for a given id list
     * @param ids parameter that corresponds to the role ids, and will be used to create
     *               others attribute values as well
     * @return a mocked tenant list
     */
    protected List<SystemRole> setupMockedRoleList(List<Long> ids) {
        List<SystemRole> roles = new ArrayList<>();
        ids.forEach(id -> {
            SystemRole systemRole = RoleFactory.create(String.valueOf(id), String.valueOf(id), null);
            systemRole.setId(id);
            roles.add(systemRole);
        });
        return roles;
    }

    /**
     * Assemblies a mocked tenant collection for a given id list
     * @param ids parameter that corresponds to the role ids, and will be used to create
     *               others attribute values as well
     * @return a mocked tenant list
     */
    protected List<SystemTenant> setupMockedTenantList(List<Long> ids) {
        List<SystemTenant> tenants = new ArrayList<>();
        ids.forEach(id -> {
            SystemTenant tenant = TenantFactory.create(String.valueOf(id), String.valueOf(id),
                    TenantType.SUB_TENANT.getName(), null, null, null,
                    null, null, null, null,
                    null, null, null, null);
            tenant.setId(id);
            tenants.add(tenant);
        });
        return tenants;
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

        List<Long> ids = LongStream.rangeClosed(1, pageSize)
                .boxed().collect(Collectors.toList());

        when(service.getAll(pageNo+1, pageSize)).then(i -> setupMockedPage(pageSize));
        when(roleService.getRolesByIds(ids)).then(i -> setupMockedRoleList(ids));
        when(tenantService.getTenantsByIds(ids)).then(i -> setupMockedTenantList(ids));

        LazyTenantRoleAssociationDataModel lazyModel = new LazyTenantRoleAssociationDataModel(service,
                tenantService, roleService);
        List<SystemTenantRole> toBeShown = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        // Evaluating collection retrieved
        assertNotNull(toBeShown);
        assertFalse(toBeShown.isEmpty());
        assertEquals(pageSize, toBeShown.size());

        // Evaluating information stored into the caches
        assertEquals(lazyModel.getRoleName(1L), String.valueOf(1L));
        assertEquals(lazyModel.getTenantName(1L), String.valueOf(1L));

        assertNull(lazyModel.getRoleName(100000L));
        assertNull(lazyModel.getTenantName(111111L));

        toBeShown = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        assertNotNull(toBeShown);
        assertFalse(toBeShown.isEmpty());
    }

    /**
     * Test an hypothetical case in which an exception occurs when retrieving
     * role information.
     *
     * Page will not be retrieved, list will be empty and there
     * will be an exception appended into the log.
     *
     * The idea is to intercept and infer whats was written into the log
     *
     * @throws SystemException always threw when communication issue occurs
     * @throws Exception still threw by role rest client
     */
    @Test
    public void testGetDataWhenErrorOccursDuringRoleRetrieval() throws SystemException, Exception {
        int pageNo = 0;
        int pageSize = 1;
        String msgError = "Communication breakdown - role endpoint";

        // Get Logback Logger
        Logger lazyDataModelLogger = (Logger) LoggerFactory.getLogger(LazyAbstractDataModel.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // Add the appender to the logger
        lazyDataModelLogger.addAppender(listAppender);

        // Mocking the processing
        List<Long> ids = Arrays.asList(1L);
        when(service.getAll(pageNo+1, pageSize)).then(i -> setupMockedPage(pageSize));
        when(tenantService.getTenantsByIds(ids)).then(i -> setupMockedTenantList(ids));
        when(roleService.getRolesByIds(ids)).thenThrow(new SystemException(msgError));

        LazyTenantRoleAssociationDataModel lazyModel = new
                LazyTenantRoleAssociationDataModel(service, tenantService, roleService);

        // Retrieving the outcome
        List<SystemTenantRole> listToBeLoaded = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        assertNotNull(listToBeLoaded);
        assertTrue(listToBeLoaded.isEmpty());

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains(msgError));
    }
}
