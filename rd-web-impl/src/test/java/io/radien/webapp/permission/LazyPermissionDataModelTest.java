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
package io.radien.webapp.permission;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.services.ActionFactory;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;
import io.radien.ms.permissionmanagement.client.services.ResourceFactory;
import io.radien.webapp.LazyAbstractDataModel;
import java.util.ArrayList;
import java.util.Collections;
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
 * Class that aggregates UnitTest cases for {@link LazyPermissionDataModel}
 * @author Newton Carvalho
 */
public class LazyPermissionDataModelTest {

    @Mock
    private PermissionRESTServiceAccess service;

    @Mock
    private ActionRESTServiceAccess actionService;

    @Mock
    private ResourceRESTServiceAccess resourceService;

    private final Map<String, FilterMeta> filterMetaMap = new HashMap<>();
    private final Map<String, SortMeta> sortMetaMap = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Given a size, assemblies a very simple mocked page
     * @param size informed page size
     * @return a mocked page containing SystemPermission instances
     */
    protected Page<? extends SystemPermission> setupMockedPage(int size) {
        List<SystemPermission> list = new ArrayList<>();

        Page<SystemPermission> page = new Page<>();
        for (long i=1; i<=size; i++) {
            SystemPermission Permission = PermissionFactory.create(String.valueOf(i), i, i,null);
            Permission.setId(i);
            list.add(Permission);
        }
        page.setResults(list);
        page.setCurrentPage(1);
        page.setTotalPages(1);
        page.setTotalResults(size);

        return page;
    }

    /**
     * Assemblies a mocked action collection for a given id list
     * @param ids parameter that corresponds to the action ids, and will be used to create
     *               others attribute values as well
     * @return a mocked resource list
     */
    protected List<SystemAction> setupMockedActionList(List<Long> ids) {
        List<SystemAction> actions = new ArrayList<>();
        ids.forEach(id -> {
            SystemAction systemAction = ActionFactory.create(String.valueOf(id), null);
            systemAction.setId(id);
            actions.add(systemAction);
        });
        return actions;
    }

    /**
     * Assemblies a mocked resource collection for a given id list
     * @param ids parameter that corresponds to the action ids, and will be used to create
     *               others attribute values as well
     * @return a mocked resource list
     */
    protected List<SystemResource> setupMockedResourceList(List<Long> ids) {
        List<SystemResource> resources = new ArrayList<>();
        ids.forEach(id -> {
            SystemResource resource = ResourceFactory.create(String.valueOf(id), null);
            resource.setId(id);
            resources.add(resource);
        });
        return resources;
    }

    /**
     * Test for {@link LazyPermissionDataModel#getData(int, int, Map, Map)}
     *
     * In this case will be tested the workflow that corresponds the retrieval of a page
     * containing SystemPermission instances. Taking in consideration the page index/number and
     * page size.
     *
     * Once the Page is retrieved, then the associated Resources and Actions also must be retrieved
     * and stored into internal caches.
     *
     * @throws SystemException exception that describes communication issues with resource or resource action endpoint
     * @throws Exception still threw by action service
     */
    @Test
    public void testGetData() throws SystemException, Exception {
        int pageNo = 0;
        int pageSize = 10;

        List<Long> ids = LongStream.rangeClosed(1, pageSize)
                .boxed().collect(Collectors.toList());

        when(service.getAll(null,pageNo+1, pageSize, null, true)).
                then(i -> setupMockedPage(pageSize));
        when(actionService.getActionsByIds(ids)).then(i -> setupMockedActionList(ids));
        when(resourceService.getResourcesByIds(ids)).then(i -> setupMockedResourceList(ids));

        LazyPermissionDataModel lazyModel = new LazyPermissionDataModel(service,
                actionService, resourceService);
        List<SystemPermission> toBeShown = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        // Evaluating collection retrieved
        assertNotNull(toBeShown);
        assertFalse(toBeShown.isEmpty());
        assertEquals(pageSize, toBeShown.size());

        // Evaluating information stored into the caches
        assertEquals(lazyModel.getActionName(1L), String.valueOf(1L));
        assertEquals(lazyModel.getResourceName(1L), String.valueOf(1L));

        assertNull(lazyModel.getActionName(100000L));
        assertNull(lazyModel.getResourceName(111111L));

        toBeShown = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        assertNotNull(toBeShown);
        assertFalse(toBeShown.isEmpty());
    }

    /**
     * Test an hypothetical case in which an exception occurs when retrieving
     * action information.
     *
     * Page will not be retrieved, list will be empty and there
     * will be an exception appended into the log.
     *
     * The idea is to intercept and infer whats was written into the log
     *
     * @throws SystemException always threw when communication issue occurs
     * @throws Exception still threw by action rest client
     */
    @Test
    public void testGetDataWhenErrorOccursDuringActionRetrieval() throws SystemException, Exception {
        int pageNo = 0;
        int pageSize = 1;
        String msgError = "Communication breakdown - action endpoint";

        // Get Logback Logger
        Logger lazyDataModelLogger = (Logger) LoggerFactory.getLogger(LazyAbstractDataModel.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // Add the appender to the logger
        lazyDataModelLogger.addAppender(listAppender);

        // Mocking the processing
        List<Long> ids = Collections.singletonList(1L);
        when(service.getAll(null,pageNo+1, pageSize, null, true)).then(i -> setupMockedPage(pageSize));
        when(resourceService.getResourcesByIds(ids)).then(i -> setupMockedResourceList(ids));
        when(actionService.getActionsByIds(ids)).thenThrow(new SystemException(msgError));

        LazyPermissionDataModel lazyModel = new
                LazyPermissionDataModel(service, actionService, resourceService);

        // Retrieving the outcome
        List<SystemPermission> listToBeLoaded = lazyModel.load(pageNo, pageSize, sortMetaMap, filterMetaMap);

        assertNotNull(listToBeLoaded);
        assertTrue(listToBeLoaded.isEmpty());

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains(msgError));
    }
}
