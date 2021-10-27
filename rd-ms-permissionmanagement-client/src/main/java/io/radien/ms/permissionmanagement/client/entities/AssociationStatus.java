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
package io.radien.ms.permissionmanagement.client.entities;

/**
 * This class describes the result of an association operation
 *
 * @author Newton Carvalho
 */
public class AssociationStatus {

    private final boolean successful;
    private String message;

    /**
     * Association Status success constructor
     */
    public AssociationStatus() {
        this(true, "");
    }

    /**
     * Association status constructor
     * @param success value of success or failure of the association
     * @param message in case of failure detailed message of the issue
     */
    public AssociationStatus(boolean success, String message) {
        this.successful = success;
        this.message = message;
    }

    /**
     * Indicates if the operation was successful or not
     * @return true if operation was a success
     */
    public boolean isOK() {
        return successful;
    }

    /**
     * Describes the issue occurred during the operation
     * @return the detailed issue message
     */
    public String getMessage() {
        return message;
    }
}
