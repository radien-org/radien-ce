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

import javax.ws.rs.core.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class that aggregates UnitTest GenericErrorMessagesToResponseMapper
 *
 * @author Rajesh Gavvala
 */
public class GenericErrorMessagesToResponseMapperTest {

    /**
     * Asserts exception
     * Test method {@link GenericErrorMessagesToResponseMapper#getGenericError(Exception)}
     */
    @Test
    public void testGetGenericError(){
        Exception exception = new InternalServerErrorException("Generic Error.");
        Response response = GenericErrorMessagesToResponseMapper.getGenericError(exception);
        assertEquals(500, response.getStatus());
    }

    /**
     * Asserts exception
     * Test method {@link GenericErrorMessagesToResponseMapper#getInvalidRequestResponse(String)}
     */
    @Test
    public void testGetInvalidRequestResponse(){
        Response response = GenericErrorMessagesToResponseMapper.getInvalidRequestResponse("invalid");
        assertEquals(400, response.getStatus());
    }

    /**
     * Asserts exception
     * Test method {@link GenericErrorMessagesToResponseMapper#getNotInformedParametersResponse(String)}
     */
    @Test
    public void testGetNotInformedParametersResponse(){
        Response response = GenericErrorMessagesToResponseMapper.getNotInformedParametersResponse("not informed");
        assertEquals(400, response.getStatus());
    }

    /**
     * Asserts exception
     * Test method {@link GenericErrorMessagesToResponseMapper#getResourceNotFoundException()}
     */
    @Test
    public void testGetResourceNotFoundException(){
        Response response = GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        assertEquals(404, response.getStatus());
    }

    /**
     * Asserts exception
     * Test method {@link GenericErrorMessagesToResponseMapper#getForbiddenResponse()}
     */
    @Test
    public void testGetForbiddenResponse(){
        Response response = GenericErrorMessagesToResponseMapper.getForbiddenResponse();
        assertEquals(403, response.getStatus());
    }

}