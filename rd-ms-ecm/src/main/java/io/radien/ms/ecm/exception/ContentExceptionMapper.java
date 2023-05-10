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
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ContentExceptionMapper implements ExceptionMapper<ContentException> {
    private static final Logger log = LoggerFactory.getLogger(ContentExceptionMapper.class);


    @Override
    public Response toResponse(ContentException exception) {
        log.error("[ContentException] - {} - {}", exception.getStatus().getStatusCode(), exception.getMessage());
        return Response.status(exception.getStatus())
                .entity(exception.getMessage())
                .build();
    }
}
