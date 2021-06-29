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
package io.radien.ms.usermanagement.client.exceptions;

import io.radien.exception.GenericErrorCodeMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User Management Testing criteria for the generic error code message
 */
public class ErrorCodeMessageTest {

    /**
     * Testing the generic error code messages for the user management
     */
    @Test
    public void testToString() {
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: %s\"}", GenericErrorCodeMessage.DUPLICATED_FIELD.toString());
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: Email Address\"}", GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
    }
}
