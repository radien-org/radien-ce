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
package io.radien.api.service.batch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Class that aggregates UnitTest cases for DataIssue
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
public class DataIssueTest {
    @InjectMocks
    private DataIssue dataIssue;

    @Mock
    List<String> stringList;

    /**
     * Prepares mock objects
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks( this );

        stringList = new ArrayList<>();
        stringList.add("Issue-0");
        stringList.add("Issue-1");
    }

    /**
     * Asserts DataIssue() constructors
     */
    @Test
    public void testDataIssue(){
        new DataIssue(0, stringList);
        new DataIssue(1, "Issue-0");
        assertEquals(0, dataIssue.getRowId());
    }

    /**
     * Asserts Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDataIssueStringException(){
        new DataIssue(0, (String) null );
    }

    /**
     * Asserts Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDataIssueListException(){
        new DataIssue(0, (List<String>) null );
    }

    /**
     * Asserts test method getReasons()
     */
    @Test
    public void testReasons(){
        dataIssue.addReason("addReason");
        assertNotNull(dataIssue.getReasons());
    }
}