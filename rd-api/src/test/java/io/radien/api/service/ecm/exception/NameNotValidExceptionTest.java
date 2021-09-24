/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.radien.api.service.ecm.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Class that aggregates UnitTest NameNotValidException
 *
 * @author Rajesh Gavvala
 */
public class NameNotValidExceptionTest {
    /**
     * Asserts NameNotValid exception
     */
    @Test
    public void testNameNotValidException(){
        NameNotValidException exception = new NameNotValidException("nameNotValid_expired");
        assertEquals("Message(s): nameNotValid_expired", exception.getMessage());
    }
}