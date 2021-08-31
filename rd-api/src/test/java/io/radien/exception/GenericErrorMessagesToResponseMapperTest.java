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
package io.radien.exception;

import org.junit.Test;

import org.powermock.core.classloader.annotations.PrepareForTest;
/**
 * Class that aggregates UnitTest GenericErrorMessagesToResponseMapper
 *
 * @author Rajesh Gavvala
 */
@PrepareForTest(GenericErrorMessagesToResponseMapper.class)
public class GenericErrorMessagesToResponseMapperTest {

    /**
     * Asserts exception
     * Test method getGenericError()
     */
    @Test(expected = Exception.class)
    public void testGetGenericError(){
        GenericErrorMessagesToResponseMapper.getGenericError(new Exception("error"));
    }

    /**
     * Asserts exception
     * Test method getInvalidRequestResponse()
     */
    @Test(expected = Exception.class)
    public void testGetInvalidRequestResponse(){
        GenericErrorMessagesToResponseMapper.getInvalidRequestResponse("error");
    }

    /**
     * Asserts exception
     * Test method getResourceNotFoundException()
     */
    @Test(expected = Exception.class)
    public void testGetResourceNotFoundException(){
        GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
    }

    /**
     * Asserts exception
     * Test method getNotInformedParametersResponse()
     */
    @Test(expected = Exception.class)
    public void testGetNotInformedParametersResponse(){
        GenericErrorMessagesToResponseMapper.getNotInformedParametersResponse("parameterName");
    }

    /**
     * Asserts exception
     * Test method getForbiddenResponse()
     */
    @Test(expected = Exception.class)
    public void testGetForbiddenResponse(){
        GenericErrorMessagesToResponseMapper.getForbiddenResponse();
    }
}