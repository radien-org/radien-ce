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
 * Class that aggregates UnitTest AccountNotValidException
 *
 * @author Rajesh Gavvala
 */
public class AccountNotValidExceptionTest {
    /**
     * Asserts AccountNotValid exception
     */
    @Test
    public void testAccountNotValidException(){
        AccountNotValidException exception = new AccountNotValidException();
        assertNotNull(exception);

        AccountNotValidException exception2 = new AccountNotValidException(new Exception("account_validation_error"));
        assertEquals("Message(s): wrapped exception: java.lang.Exception with message: account_validation_error", exception2.getMessage());

        AccountNotValidException exception3 = new AccountNotValidException("validation_exception");
        assertEquals("Message(s): validation_exception",exception3.getMessage());
    }
}