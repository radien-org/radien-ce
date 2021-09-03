/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
/**
 * Class that aggregates UnitTest cases for ModelResponseExceptionMapper
 *
 * @author Rajesh Gavvala
 */
public class ModelResponseExceptionMapperTest {

    @Before
    public void before() {
        MockitoAnnotations.initMocks( this );
    }

    /**
     * Test method handles()
     * Asserts handle exception status codes
     */
    @Test
    public void testHandles() {
        ModelResponseExceptionMapper modelResponseExceptionMapper = new ModelResponseExceptionMapper();
        boolean handlesException200 = modelResponseExceptionMapper.handles(200, null);
        boolean handlesException400 = modelResponseExceptionMapper.handles(400, null);
        boolean handlesException401 = modelResponseExceptionMapper.handles(401, null);
        boolean handlesException404 = modelResponseExceptionMapper.handles(404, null);
        boolean handlesException500 = modelResponseExceptionMapper.handles(500, null);

        assertFalse(handlesException200);
        assertTrue(handlesException400);
        assertTrue(handlesException401);
        assertTrue(handlesException404);
        assertTrue(handlesException500);
    }

    /**
     * Test method toThrowable()
     * Asserts throwable exception status codes
     */
    @Test
    public void testToThrowable() {

        ModelResponseExceptionMapper modelResponseExceptionMapper = new ModelResponseExceptionMapper();
        String respMessage_OK = "200 : Ok.";
        Response mockResponse_OK = getMockResponse(Status.OK, respMessage_OK);
        Exception exceptionOk = modelResponseExceptionMapper.toThrowable(mockResponse_OK);
        assertNull(exceptionOk);

        String respMessage_BadRequest = "400 : BadRequest.";
        Response mockResponse_BadRequest = getMockResponse(Status.BAD_REQUEST, respMessage_BadRequest);
        Exception exception_BadRequest = modelResponseExceptionMapper.toThrowable(mockResponse_BadRequest);
        assertTrue(exception_BadRequest instanceof BadRequestException);
        assertEquals(respMessage_BadRequest,exception_BadRequest.getMessage());

        String respMessage_TokenExpired = "401 : TokenExpired.";
        Response mockResponse_TokenExpired = getMockResponse(Status.UNAUTHORIZED, respMessage_TokenExpired);
        Exception exception_TokenExpired = modelResponseExceptionMapper.toThrowable(mockResponse_TokenExpired);
        assertTrue(exception_TokenExpired instanceof TokenExpiredException);
        assertEquals(respMessage_TokenExpired,exception_TokenExpired.getMessage());

        String respMessage_NotFound = "404 : NotFound.";
        Response mockResponse_NotFound = getMockResponse(Status.NOT_FOUND, respMessage_NotFound);
        Exception exception_NotFound = modelResponseExceptionMapper.toThrowable(mockResponse_NotFound);
        assertTrue(exception_NotFound instanceof NotFoundException);
        assertEquals(respMessage_NotFound,exception_NotFound.getMessage());

        String respMessage_InternalServerError = "500 : InternalServerError.";
        Response mockResponse_InternalServerError = getMockResponse(Status.INTERNAL_SERVER_ERROR, respMessage_InternalServerError);
        Exception exception_InternalServerError = modelResponseExceptionMapper.toThrowable(mockResponse_InternalServerError);
        assertTrue(exception_InternalServerError instanceof InternalServerErrorException);
        assertEquals(respMessage_InternalServerError,exception_InternalServerError.getMessage());
    }

    /**
     * Method that handles status response
     *
     * @param status to be passed
     * @param msgEntity status message to be passed
     * @return mock response
     */
    private Response getMockResponse(Status status, String msgEntity) {
        Response mockResponse = mock(Response.class);
        Mockito.when(mockResponse.readEntity(String.class)).thenReturn(msgEntity);
        Mockito.when(mockResponse.getStatus()).thenReturn(status.getStatusCode());
        Mockito.when(mockResponse.getStatusInfo()).thenReturn(status);

        return mockResponse;
    }
}