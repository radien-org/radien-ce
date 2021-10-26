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
package io.radien.aws.utils.s3.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Class that aggregates UnitTest cases for
 * RemoteResourceNotAvailableException
 * @author Rajesh Gavvala
 */
public class RemoteResourceNotAvailableExceptionTest {
    /**
     * Asserts Constructor of RemoteResourceNotAvailableException
     * with messages and Throwable cause parameters
     */
    @Test
    public void testRemoteResourceNotAvailableException(){
        RemoteResourceNotAvailableException exception_string =
                new RemoteResourceNotAvailableException("TestException");

        assertEquals("TestException",exception_string.getMessage());

        Throwable throwable_cause = new RuntimeException("Test Cause");
        RemoteResourceNotAvailableException exception_string_cause =
                new RemoteResourceNotAvailableException("TestExceptionString", throwable_cause);

        assertEquals("TestExceptionString",exception_string_cause.getMessage());
        assertEquals(throwable_cause,exception_string_cause.getCause());
    }
}