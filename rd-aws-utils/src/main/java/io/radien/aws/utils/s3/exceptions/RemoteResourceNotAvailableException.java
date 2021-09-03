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

package io.radien.aws.utils.s3.exceptions;

/**
 * Remote Resource Not Available Exception
 * Exception to be thrown as the name says when the resource is not existent or not found
 *
 * @author Bruno Gama
 */
public class RemoteResourceNotAvailableException extends RuntimeException{

    /**
     * Remote resource not available exception message constructor
     * @param message to be added and throw in the exception explaining the error/issue
     */
    public RemoteResourceNotAvailableException(String message) {
        super(message);
    }

    /**
     * Remote resource not available exception message and cause constructor
     * @param message to be added and throw in the exception explaining the error/issue
     * @param cause of the error or issue
     */
    public RemoteResourceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
