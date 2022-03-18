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

package io.radien.api.service.docmanagement.exception;

import javax.ws.rs.core.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentTypeExceptionTest {
    @Test
    public void testDefaultConstructor() {
        DocumentTypeException exception = new DocumentTypeException();
        assertEquals(500, exception.getStatus().getStatusCode());
    }

    @Test
    public void testDefaultConstructorWithStatus() {
        DocumentTypeException exception = new DocumentTypeException(Response.Status.NOT_FOUND);
        assertEquals(404, exception.getStatus().getStatusCode());
    }

    @Test
    public void testDefaultConstructorWithMessage() {
        DocumentTypeException exception = new DocumentTypeException("errorMessage");
        assertEquals(500, exception.getStatus().getStatusCode());
        assertEquals("errorMessage", exception.getMessage());
    }

    @Test
    public void testDefaultConstructorWithMessageAndStatus() {
        DocumentTypeException exception = new DocumentTypeException("errorMessage", Response.Status.NOT_FOUND);
        assertEquals(404, exception.getStatus().getStatusCode());
        assertEquals("errorMessage", exception.getMessage());
    }

    @Test
    public void testDefaultConstructorWithExceptionAndStatus() {
        IllegalStateException stateException = new IllegalStateException("inner exception");
        DocumentTypeException exception = new DocumentTypeException(stateException, Response.Status.NOT_FOUND);
        assertEquals(404, exception.getStatus().getStatusCode());
        assertEquals(stateException, exception.getCause());
    }

    @Test
    public void testDefaultConstructorWithMessageAndExceptionAndStatus() {
        IllegalStateException stateException = new IllegalStateException("inner exception");
        DocumentTypeException exception = new DocumentTypeException("errorMessage", stateException, Response.Status.NOT_FOUND);
        assertEquals(404, exception.getStatus().getStatusCode());
        assertEquals("errorMessage", exception.getMessage());
        assertEquals(stateException, exception.getCause());
    }

    @Test
    public void testDefaultConstructorWithMessageAndException() {
        IllegalStateException stateException = new IllegalStateException("inner exception");
        DocumentTypeException exception = new DocumentTypeException("errorMessage", stateException);
        assertEquals(500, exception.getStatus().getStatusCode());
        assertEquals("errorMessage", exception.getMessage());
        assertEquals(stateException, exception.getCause());
    }
}
