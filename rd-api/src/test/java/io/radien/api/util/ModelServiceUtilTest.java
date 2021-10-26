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
package io.radien.api.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for ModelServiceUtil
 *
 * @author Rajesh Gavvala
 */
@PrepareForTest(ModelServiceUtil.class)
public class ModelServiceUtilTest {
    @Mock
    CriteriaBuilder criteriaBuilder;
    @Mock
    Predicate global;
    @Mock
    EntityManager em;
    @Mock
    Root<?> objectRoot;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Asserts getFilteredPredicateFromModelService()
     */
    @Test
    public void testGetFilteredPredicateFromModelService(){
        List<Long> filterIds = new ArrayList<>();
        filterIds.add(1L);

        when(criteriaBuilder.isTrue(criteriaBuilder.literal(true))).thenReturn(global);
        when(criteriaBuilder.isTrue(criteriaBuilder.literal(true))).thenReturn(global);

        assertEquals(global, ModelServiceUtil.getFilteredPredicateFromModelService(false, filterIds, true, criteriaBuilder, objectRoot));
        assertNull(ModelServiceUtil.getFilteredPredicateFromModelService(false, filterIds, false, criteriaBuilder, objectRoot));
    }


    @Test(expected = Exception.class)
    public void testGetCountFromModelService() {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        doReturn(criteriaQuery).when(criteriaBuilder).createQuery(Long.class);
        ModelServiceUtil.getCountFromModelService(criteriaBuilder, global, objectRoot, em);
    }

    @Test
    public void testGetListOrderSortBy() {
        List<String> sortBy = new ArrayList<>();
        sortBy.add("A");

        assertNotNull(ModelServiceUtil.getListOrderSortBy(false, objectRoot,criteriaBuilder, sortBy));
    }

    /**
     * Asserts getFieldPredicateFromModelService()
     */
    @Test
    public void testGetFieldPredicateFromModelService() {
        assertNull(ModelServiceUtil.getFieldPredicateFromModelService("", true, false,
                criteriaBuilder, null, global));

        assertNull(ModelServiceUtil.getFieldPredicateFromModelService("", false, true,
                criteriaBuilder, null, global));
    }
}