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

/**
 * Permission specific Bad request exception
 * this exception is to be thrown when the user requests something that will have wrong requested data
 *
 * @author Newton Carvalho
 */
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 5485575161734022041L;

    /**
     * Bad Request exception empty constructor
     */
    public BadRequestException() {
        super();
    }

    /**
     * Bad Request exception message constructor
     * @param message to be added into the bad request exception
     */
    public BadRequestException(String message) {
        super(message);
    }
}
