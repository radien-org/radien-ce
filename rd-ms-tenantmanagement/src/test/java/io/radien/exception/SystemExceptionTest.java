/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * @author Bruno Gama
 **/
public class SystemExceptionTest extends TestCase {

    @Test
    public void testSystemException() {
        SystemException exception1 = new SystemException();
        assertNotNull(exception1);

        SystemException exception2 = new SystemException(exception1);
        assertNotNull(exception2);

        SystemException exception3 = new SystemException("message");
        assertNotNull(exception3);

        SystemException exception4 = new SystemException("message", exception1);
        assertNotNull(exception4);

        SystemException exception5 = new SystemException("message", new Exception("exception"));
        assertNotNull(exception5);
    }

    @Test
    public void testGetMessages() {
        SystemException exception1 = new SystemException();
        assertNotNull(exception1);

        SystemException exception2 = new SystemException("message", exception1);
        assertNotNull(exception2);
        exception2.addMessage("test new message");

        List<String> listException = exception2.getMessages();

        assertTrue(listException.contains("message"));
        assertTrue(listException.contains("test new message"));

        String s = exception2.getMessage();
        assertTrue(s.contains("message"));
        assertTrue(s.contains("test new message"));

    }
}