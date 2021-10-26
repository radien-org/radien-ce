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
package io.radien.ms.authz.client.exception;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class NotFoundExceptionTestCase {

    @Test
    public void testBadRequestException(){
        NotFoundException exception = new NotFoundException("message");
        assertNotNull(exception);
        assertEquals("message", exception.getMessage());

        NotFoundException exception2 = new NotFoundException("message",
                new RuntimeException("test"));
        assertNotNull(exception2);
        assertEquals("message", exception2.getMessage());
        assertNotNull(exception2.getCause());
    }
}
