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

package io.radien.ms.ecm.exception;

import io.radien.api.service.ecm.exception.ContentException;
import javax.ws.rs.core.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContentExceptionMapperTest {
    @Test
    public void handles() {
        ContentExceptionMapper uRexceptionMapper = new ContentExceptionMapper();
        Response response = uRexceptionMapper.toResponse(new ContentException("error", Response.Status.INTERNAL_SERVER_ERROR));
        assertEquals(500, response.getStatus());
    }
}