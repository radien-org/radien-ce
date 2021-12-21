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
package io.radien.ms.ecm.resource;

import io.radien.api.entity.Page;
import io.radien.api.service.legal.LegalDocumentTypeServiceAccess;
import io.radien.exception.LegalDocumentTypeNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import io.radien.ms.legal.entities.LegalDocumentTypeEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link LegalDocumentTypeResource}
 */
public class LegalDocumentTypeResourceTest {

    @InjectMocks
    LegalDocumentTypeResource resource;

    @Mock
    LegalDocumentTypeServiceAccess serviceAccess;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getAll(String, int, int, List, boolean)}
     */
    @Test
    public void testPagination() {
        String search = "Test";
        int pageNo = 1, pageSize = 10;
        List<String> order = Arrays.asList("id", "name");
        when(serviceAccess.getAll(search, pageNo, pageSize, order, true)).
                thenReturn(new Page<>());
        Response response = resource.getAll(search, pageNo, pageSize, order, true);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getAll(String, int, int, List, boolean)}
     * Scenario: Exception thrown during page retrieval
     */
    @Test
    public void testPaginationExceptionCase() {
        String search = "Test";
        int pageNo = 1, pageSize = 10;
        List<String> order = Arrays.asList("id", "name");
        when(serviceAccess.getAll(search, pageNo, pageSize, order, true)).
                thenThrow(RuntimeException.class);
        Response response = resource.getAll(search, pageNo, pageSize, order, true);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getLegalDocumentTypes(String, Long, Boolean, Boolean, List, boolean, boolean)}
     */
    @Test
    public void testGetByFilter() {
        when(serviceAccess.getLegalDocumentTypes(any())).thenReturn(new ArrayList<>());
        Response response = resource.getLegalDocumentTypes("ToU", 1L, true,
                true, null, true, true);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getLegalDocumentTypes(String, Long, Boolean, Boolean, List, boolean, boolean)}
     * Scenario: Exception thrown during list retrieval
     */
    @Test
    public void testGetByFilterWithException() {
        when(serviceAccess.getLegalDocumentTypes(any())).thenThrow(new RuntimeException("db error"));
        Response response = resource.getLegalDocumentTypes("ToU", 1L, true,
                true, null, true, true);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getById(Long)}
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testGetById() throws LegalDocumentTypeNotFoundException {
        when(serviceAccess.get(1L)).thenReturn(new LegalDocumentTypeEntity());
        Response response = resource.getById(1L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getById(Long)}
     * Scenario: Exception thrown during Legal Document Type retrieval
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testGetByIdWithException() throws LegalDocumentTypeNotFoundException {
        when(serviceAccess.get(1L)).thenThrow(RuntimeException.class);
        Response response = resource.getById(1L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#getById(Long)}
     * Scenario: Fail due attempt to retrieve a non exist Legal document type
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testGetByIdForNonExistentObject() throws LegalDocumentTypeNotFoundException {
        when(serviceAccess.get(1L)).thenThrow(LegalDocumentTypeNotFoundException.class);
        Response response = resource.getById(1L);
        assertEquals(404, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#delete(long)}
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testDelete() throws LegalDocumentTypeNotFoundException {
        doNothing().when(serviceAccess).delete(1L);
        Response response = resource.delete(1L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#delete(long)}
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testDeleteWithException() throws LegalDocumentTypeNotFoundException {
        doThrow(RuntimeException.class).when(serviceAccess).delete(1L);
        Response response = resource.delete(1L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#delete(long)}
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testDeleteForNonExistentObject() throws LegalDocumentTypeNotFoundException {
        doThrow(LegalDocumentTypeNotFoundException.class).when(serviceAccess).delete(1L);
        Response response = resource.delete(1L);
        assertEquals(404, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#create(LegalDocumentType)}
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     */
    @Test
    public void testCreate() throws UniquenessConstraintException {
        doNothing().when(serviceAccess).create(any());
        Response response = resource.create(new LegalDocumentType());
        assertEquals(200, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#create(LegalDocumentType)}
     * Scenario: Fail due trying to insert Legal Document Type with repeated name and tenant
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     */
    @Test
    public void testCreateUniquenessViolationCase() throws UniquenessConstraintException {
        doThrow(UniquenessConstraintException.class).when(serviceAccess).create(any());
        Response response = resource.create(new LegalDocumentType());
        assertEquals(400, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#create(LegalDocumentType)}
     * Scenario: Db fail during Legal Document Type insertion
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     */
    @Test
    public void testCreateExceptionCase() throws UniquenessConstraintException {
        doThrow(RuntimeException.class).when(serviceAccess).create(any());
        Response response = resource.create(new LegalDocumentType());
        assertEquals(500, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#update(long, LegalDocumentType)}
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testUpdate() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        LegalDocumentType ldt = new LegalDocumentType();
        ldt.setId(1L);
        doNothing().when(serviceAccess).update(ldt);
        Response response = resource.update(ldt.getId(), ldt);
        assertEquals(200, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#update(long, LegalDocumentType)}
     * Scenario: Fail due trying to insert Legal Document Type with repeated name and tenant
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testUpdateUniquenessViolationCase() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        doThrow(UniquenessConstraintException.class).when(serviceAccess).update(any());
        Response response = resource.update(1L, new LegalDocumentType());
        assertEquals(400, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#update(long, LegalDocumentType)}
     * Scenario: Fail due trying to insert Legal Document Type with repeated name and tenant
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testUpdateNotFound() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        doThrow(LegalDocumentTypeNotFoundException.class).when(serviceAccess).update(any());
        Response response = resource.update(1L, new LegalDocumentType());
        assertEquals(404, response.getStatus());
    }

    /**
     * Test for method {@link LegalDocumentTypeResource#update(long, LegalDocumentType)}
     * Scenario: Failure during db insert
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     * @throws LegalDocumentTypeNotFoundException when the legal document type could not be found for an specific id
     */
    @Test
    public void testUpdateExceptionCase() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        doThrow(RuntimeException.class).when(serviceAccess).update(any());
        Response response = resource.update(1L, new LegalDocumentType());
        assertEquals(500, response.getStatus());
    }
}
