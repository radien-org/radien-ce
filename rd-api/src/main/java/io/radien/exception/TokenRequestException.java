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

/**
 * Exception to describe issues regarding (OIDC) Token Request process
 * @author Newton Carvalho
 */
public class TokenRequestException extends Exception {

    private static final long serialVersionUID = -5086226744247455421L;

    /**
     * Token request exception constructor
     * @param message to be added into the token request exception
     */
    public TokenRequestException(String message) { super(message); }

    /**
     * Token request exception constructor
     * @param message message to be added into the token request exception
     * @param cause exception to be added into the token request exception
     */
    public TokenRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Token request exception constructor
     * @param e exception to be added into the token request exception
     */
    public TokenRequestException(Exception e) { super(e); }
}
