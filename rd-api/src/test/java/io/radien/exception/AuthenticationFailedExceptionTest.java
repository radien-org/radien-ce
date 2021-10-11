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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Class that aggregates UnitTest AuthenticationFailedException
 *
 * @author Rajesh Gavvala
 */
public class AuthenticationFailedExceptionTest {
    /**
     * Asserts AuthenticationFailedException
     */
    @Test
    public void testAuthenticationFailedException(){
        AuthenticationFailedException exception = new AuthenticationFailedException();
        assertNotNull(exception);

        AuthenticationFailedException exception2 = new AuthenticationFailedException(new Exception("authentication_failed_error"));
        assertEquals("Message(s): wrapped exception: java.lang.Exception with message: authentication_failed_error", exception2.getMessage());

        AuthenticationFailedException exception3 = new AuthenticationFailedException("authentication_failed");
        assertEquals("Message(s): authentication_failed", exception3.getMessage());
    }
}