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
package io.radien.ms.permissionmanagement.client.exceptions;

import io.radien.exception.UniquenessConstraintException;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Bruno Gama
 */
public class InvalidRequestExceptionTest extends TestCase {
    @Test
    public void testInvalidRequestException(){
        UniquenessConstraintException exception = new UniquenessConstraintException();
        assertNotNull(exception);
        UniquenessConstraintException exception2 = new UniquenessConstraintException("message");
        assertEquals("message",exception2.getMessage());
    }
}