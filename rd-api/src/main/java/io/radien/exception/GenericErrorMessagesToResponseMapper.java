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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unirest.shaded.org.apache.http.HttpStatus;

import javax.ws.rs.core.Response;

/**
 * Converts error messages into responses to be retrieved to the user
 *
 * @author Bruno Gama
 **/
public class GenericErrorMessagesToResponseMapper {

    private static final Logger log = LoggerFactory.getLogger(GenericErrorMessagesToResponseMapper.class);

    /**
     * Generic error exception. Launches a 500 Error Code to the user.
     * @param e exception to be throw
     * @return code 500 message Generic Exception
     */
    public static Response getGenericError(Exception e) {
        String message = GenericErrorCodeMessage.GENERIC_ERROR.toString();
        log.error(message, e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }

    /**
     * Invalid Request error exception. Launches a 400 Error Code to the user.
     * @param errorMessage exception message to be throw
     * @return code 400 message Generic Exception
     */
    public static Response getInvalidRequestResponse(String errorMessage) {
        return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
    }

    /**
     * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
     * @return code 100 message Resource not found.
     */
    public static Response getResourceNotFoundException() {
        return Response.status(Response.Status.NOT_FOUND).entity(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
    }

    /**
     * Returns a Bad Request response when some mandatory parameter is not present
     * @param parameterName value that will indicate which field(s) have not been informed
     * @return code 400 message describing a not informed parameter
     */
    public static Response getNotInformedParametersResponse(String parameterName) {
        String message = GenericErrorCodeMessage.PERMISSION_PARAMETERS_NOT_INFORMED.toString(parameterName);
        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
    }

    /**
     * Returns Forbidden response type in case of missing certain user roles
     * @return code 403 message Forbidden response
     */
    public static Response getForbiddenResponse() {
        return Response.status(HttpStatus.SC_FORBIDDEN).
                entity("No Role available to perform this task").build();
    }
}
