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
package io.radien.api.service.batch;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for BatchSummary
 *
 * @author Rajesh Gavvala
 */
public class BatchSummaryTest {
    @InjectMocks
    private BatchSummary batchSummary;

    @Mock
    Collection<DataIssue> dataIssueCollection;

    /**
     * Prepares mock objects
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks( this );

        dataIssueCollection = new ArrayList<>();
        dataIssueCollection.add(new DataIssue(0L, "Issue-0L"));
        dataIssueCollection.add(new DataIssue(1L, "Issue-1L"));
    }

    /**
     * Asserts BatchSummary() constructors
     */
    @Test
    public void testDataIssue(){
        new BatchSummary(0);
        new BatchSummary(0, dataIssueCollection);

        assertEquals(0, batchSummary.getTotalProcessed());
        assertEquals(0, batchSummary.getTotal());
    }

    /**
     * Asserts Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDataIssueStringException(){
        new BatchSummary(0, null);
    }

    /**
     * Asserts test method addNonProcessedItem()
     */
    @Test
    public void testAddNonProcessedItem(){
        batchSummary.addNonProcessedItem(new DataIssue(1L, "Issue-0L"));
        assertEquals(0L, batchSummary.getTotalNonProcessed());
    }

    /**
     * Asserts test method addNonProcessedItems()
     */
    @Test
    public void testAddNonProcessedItems(){
        batchSummary.addNonProcessedItems(dataIssueCollection);
        assertNotNull(batchSummary.getNonProcessedItems());
    }

    /**
     * Asserts test method getInternalStatus()
     */
    @Test
    public void testGetInternalStatus(){
        assertEquals(BatchSummary.ProcessingStatus.SUCCESS, batchSummary.getInternalStatus());

        when(batchSummary.getTotalProcessed()).thenReturn(1);
        assertEquals(BatchSummary.ProcessingStatus.PARTIAL_SUCCESS, batchSummary.getInternalStatus());
    }

}