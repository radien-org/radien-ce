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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link InvalidAccessTokenException}
 * @author Newton Carvalho
 */
public class InvalidAccessTokenExceptionTest {

    /**
     * Test for constructors
     */
    @Test
    public void testConstructors() {
        String msg = "Error";
        InvalidAccessTokenException e = new InvalidAccessTokenException(msg);
        assertEquals(msg, e.getMessage());

        Exception cause = new Exception(msg);
        e = new InvalidAccessTokenException(cause);
        assertEquals(cause, e.getCause());

        String newMsg = "new error";
        e = new InvalidAccessTokenException(newMsg, cause);
        assertEquals(cause, e.getCause());
        assertEquals(newMsg, e.getMessage());
    }

}
