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

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorizationSearchFilter;
import io.radien.ms.rolemanagement.factory.LinkedAuthorizationFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationsServiceTest extends TestCase {

    @InjectMocks
    LinkedAuthorizationsService linkedAuthorizationsService;

    @Mock
    LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllAssociations() {
        String search = "";
        Page<SystemLinkedAuthorization> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(linkedAuthorizationServiceAccess.getAll(1,2)).thenReturn(p);
        Page<? extends SystemLinkedAuthorization> result = linkedAuthorizationsService.getAllAssociations(1,2);
        assertEquals(p,result);
    }

    @Test
    public void testGet() throws LinkedAuthorizationNotFoundException {
        SystemLinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);
        when(linkedAuthorizationServiceAccess.getAssociationById(2L)).thenReturn(linkedAuthorization);
        SystemLinkedAuthorization result = linkedAuthorizationsService.get(2L);
        assertEquals(linkedAuthorization,result);
    }

    @Test
    public void testDelete() throws LinkedAuthorizationNotFoundException {
        SystemLinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);
        when(linkedAuthorizationServiceAccess.getAssociationById(2L)).thenReturn(linkedAuthorization);
        SystemLinkedAuthorization result = linkedAuthorizationsService.get(2L);
        assertEquals(linkedAuthorization,result);
        linkedAuthorizationsService.deleteAssociation(2L);
    }

    @Test
    public void testGetSpecificAssociation() {
        LinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter();
        when(linkedAuthorizationServiceAccess.getSpecificAssociation(filter)).thenReturn(new ArrayList<>());
        List<? extends SystemLinkedAuthorization> results = linkedAuthorizationsService.getSpecificAssociation(filter);
        assertEquals(0,results.size());
    }

    @Test
    public void testSave() throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        LinkedAuthorization u = LinkedAuthorizationFactory.create(3L, 3L, 3L, 3L, 3L);
        doThrow(new UniquenessConstraintException("")).when(linkedAuthorizationServiceAccess).save(u);
        boolean success = false;
        try{
            linkedAuthorizationsService.save(u);
        } catch (UniquenessConstraintException e) {
            success = true;
        } catch (LinkedAuthorizationNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(success);
    }
}