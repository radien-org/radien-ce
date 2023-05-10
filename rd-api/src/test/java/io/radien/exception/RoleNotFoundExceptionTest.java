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

import io.radien.api.service.role.exception.RoleNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Class that aggregates UnitTest RoleNotFoundException
 *
 * @author Rajesh Gavvala
 */
public class RoleNotFoundExceptionTest {
    /**
     * Asserts RoleNotFoundException
     */
    @Test
    public void testRoleNotFoundException(){
        RoleNotFoundException exception = new RoleNotFoundException("roleNotFound_exception");
        assertEquals("roleNotFound_exception",exception.getMessage());
    }
}