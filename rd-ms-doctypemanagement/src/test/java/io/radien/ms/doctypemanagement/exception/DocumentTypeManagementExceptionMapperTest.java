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

package io.radien.ms.doctypemanagement.exception;

import io.radien.api.service.docmanagement.exception.DocumentTypeException;
import io.radien.api.service.docmanagement.exception.PropertyDefinitionNotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentTypeManagementExceptionMapperTest {
    @Test
    public void testToResponse() {
        DocumentTypeManagementExceptionMapper mapper = new DocumentTypeManagementExceptionMapper();
        DocumentTypeException exception = new PropertyDefinitionNotFoundException("not found");
        Response result = mapper.toResponse(exception);
        assertEquals(500, result.getStatusInfo().getStatusCode());
        assertEquals("not found", result.readEntity(String.class));
    }
}
