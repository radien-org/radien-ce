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

import io.radien.api.service.permission.exception.PermissionIllegalArgumentException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class that aggregates UnitTest PermissionIllegalArgumentException
 *
 * @author Rajesh Gavvala
 */
public class PermissionIllegalArgumentExceptionTest {
    /**
     * Asserts PermissionIllegalArgumentException
     */
    @Test
    public void testPermissionIllegalArgumentException(){
        PermissionIllegalArgumentException exception = new PermissionIllegalArgumentException("permissionIllegalArgumentException");
        assertEquals("permissionIllegalArgumentException",exception.getMessage());
    }

}