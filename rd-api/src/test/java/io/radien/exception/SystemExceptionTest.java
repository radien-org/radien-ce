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
package io.radien.exception;

import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Class that aggregates UnitTest SystemException
 *
 * @author Rajesh Gavvala
 */
public class SystemExceptionTest {
    /**
     * Asserts SystemException empty constructor
     */
    @Test
    public void testSystemException() {
        SystemException systemException = new SystemException();
        assertNotNull(systemException);
    }

    /**
     * Asserts SystemException exception constructor
     */
    @Test
    public void testSystemExceptionOfException(){
        Exception exception = new Exception("exception");
        SystemException systemException = new SystemException(exception);
        assertNotNull(systemException);
    }

    /**
     * Asserts SystemException message constructor
     * Test method {@link SystemException#getMessage()}
     * Test method {@link SystemException#getMessages()}
     */
    @Test
    public void testSystemExceptionOfMessages(){
        SystemException systemException = new SystemException("message");
        systemException.addMessage("addMessage");
        assertNotNull(systemException);

        List<String> list = systemException.getMessages();
        assertNotNull(list);

        systemException.getMessage();
    }

    /**
     * Asserts SystemException message and exception constructor
     */
    @Test
    public void testSystemExceptionOfMessageAndException(){
        Exception exception = new Exception("exception");
        SystemException systemException = new SystemException("message", exception);
        assertNotNull(systemException);
    }
}