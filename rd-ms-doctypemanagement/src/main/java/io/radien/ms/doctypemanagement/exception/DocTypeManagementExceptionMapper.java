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
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class DocTypeManagementExceptionMapper implements ExceptionMapper<DocumentTypeException> {
    private static final Logger log = LoggerFactory.getLogger(DocTypeManagementExceptionMapper.class);

    @Override
    public Response toResponse(DocumentTypeException exception) {
        log.error("[DocumentTypeException] - {} - {}", exception.getStatus().getStatusCode(), exception.getMessage());
        return Response.status(exception.getStatus())
                .entity(exception.getMessage())
                .build();
    }
}
