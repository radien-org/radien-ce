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
package io.radien.ms.rolemanagement.client.exception;

import io.radien.exception.BadRequestException;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.ModelResponseExceptionMapper;
import io.radien.exception.NotFoundException;
import io.radien.exception.TokenExpiredException;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
/**
 * @author Bruno Gama
 */
public class RoleResponseExceptionMapperTest {

    @InjectMocks
    private RoleResponseExceptionMapper roleResponseExceptionMapper;

    @Mock
    private ModelResponseExceptionMapper modelResponseExceptionMapper;

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks( this );
    }

    /**
     * Test method handles() that
     * Asserts handle exception status codes
     */
    @Test
    public void testHandles() {
        doReturn(false).when(modelResponseExceptionMapper).handles(200, null);
        boolean handlesException200 = roleResponseExceptionMapper.roleHandles(200, null);

        doReturn(true).when(modelResponseExceptionMapper).handles(400, null);
        boolean handlesException400 = roleResponseExceptionMapper.roleHandles(400, null);

        doReturn(true).when(modelResponseExceptionMapper).handles(401, null);
        boolean handlesException401 = roleResponseExceptionMapper.roleHandles(401, null);

        doReturn(true).when(modelResponseExceptionMapper).handles(404, null);
        boolean handlesException404 = roleResponseExceptionMapper.roleHandles(404, null);

        doReturn(true).when(modelResponseExceptionMapper).handles(500, null);
        boolean handlesException500 = roleResponseExceptionMapper.roleHandles(500, null);

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
        String respMessage_BadRequest = "400 : BadRequest.";
        Response mockResponse_BadRequest = getMockResponse(Response.Status.BAD_REQUEST, respMessage_BadRequest);
        doReturn(new io.radien.exception.BadRequestException(mockResponse_BadRequest.readEntity(String.class))).when(modelResponseExceptionMapper).toThrowable(mockResponse_BadRequest);

        Exception exception_BadRequest = roleResponseExceptionMapper.roleToThrowable(mockResponse_BadRequest);
        assertTrue(exception_BadRequest instanceof BadRequestException );
        assertEquals(respMessage_BadRequest,exception_BadRequest.getMessage());

        String respMessage_TokenExpiredException = "401 : TokenException.";
        Response mockResponse_TokenExpiredException = getMockResponse(Response.Status.UNAUTHORIZED, respMessage_TokenExpiredException);
        doReturn(new TokenExpiredException(mockResponse_TokenExpiredException.readEntity(String.class))).
                when(modelResponseExceptionMapper).toThrowable(mockResponse_TokenExpiredException);

        Exception exception_TokenExpiredException = roleResponseExceptionMapper.roleToThrowable(mockResponse_TokenExpiredException);
        assertTrue(exception_TokenExpiredException instanceof TokenExpiredException );
        assertEquals(respMessage_TokenExpiredException,exception_TokenExpiredException.getMessage());

        String respMessage_NotFoundException = "404 : NotFound.";
        Response mockResponse_NotFoundException = getMockResponse(Response.Status.NOT_FOUND, respMessage_NotFoundException);
        doReturn(new NotFoundException(mockResponse_NotFoundException.readEntity(String.class))).
                when(modelResponseExceptionMapper).toThrowable(mockResponse_NotFoundException);

        Exception exception_NotFoundException = roleResponseExceptionMapper.roleToThrowable(mockResponse_NotFoundException);
        assertTrue(exception_NotFoundException instanceof NotFoundException);
        assertEquals(respMessage_NotFoundException,exception_NotFoundException.getMessage());

        String respMessage_InternalServerException = "500 : Internal Server.";
        Response mockResponse_InternalServerException = getMockResponse(Response.Status.INTERNAL_SERVER_ERROR, respMessage_InternalServerException);
        doReturn(new io.radien.exception.InternalServerErrorException(mockResponse_InternalServerException.readEntity(String.class))).
                when(modelResponseExceptionMapper).toThrowable(mockResponse_InternalServerException);

        Exception exception_InternalServerException = roleResponseExceptionMapper.roleToThrowable(mockResponse_InternalServerException);
        assertTrue(exception_InternalServerException instanceof InternalServerErrorException );
        assertEquals(respMessage_InternalServerException,exception_InternalServerException.getMessage());

    }

    /**
     * Method that handles status response
     *
     * @param status to be passed
     * @param msgEntity status message to be passed
     * @return mock response
     */
    private Response getMockResponse(Response.Status status, String msgEntity) {
        Response mockResponse = mock(Response.class);
        Mockito.when(mockResponse.readEntity(String.class)).thenReturn(msgEntity);
        Mockito.when(mockResponse.getStatus()).thenReturn(status.getStatusCode());
        Mockito.when(mockResponse.getStatusInfo()).thenReturn(status);

        return mockResponse;
    }
}