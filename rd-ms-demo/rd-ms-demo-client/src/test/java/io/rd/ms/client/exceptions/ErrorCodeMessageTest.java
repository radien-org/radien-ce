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
package io.rd.ms.client.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorCodeMessageTest {
    @Test
    public void testToString() {
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: %s\"}", ErrorCodeMessage.DUPLICATED_FIELD.toString());
        assertEquals("{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than one resource with the same value for the field: Email Address\"}", ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
    }
}
