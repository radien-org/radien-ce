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
package io.radien.api.service.ecm.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Class that aggregates UnitTest ContentNotAvailableException
 *
 * @author Rajesh Gavvala
 */
public class ContentNotAvailableExceptionTest {
    /**
     * Asserts ContentNotAvailable exception
     */
    @Test
    public void testContentNotAvailableException(){
        ContentNotAvailableException exception = new ContentNotAvailableException();
        assertNotNull(exception);

        ContentNotAvailableException exception2 = new ContentNotAvailableException("contentNotAvailable_expired");
        assertEquals("Message(s): contentNotAvailable_expired", exception2.getMessage());
    }
}