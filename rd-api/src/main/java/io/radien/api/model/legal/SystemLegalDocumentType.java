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
package io.radien.api.model.legal;

import io.radien.api.Model;

/**
 * System Legal Document Type contract
 *
 * @author Newton Carvalho
 */
public interface SystemLegalDocumentType extends Model {

    /**
     * Contract for getter method that retrieves type name
     * @return String for type name
     */
    String getName();

    /**
     * Contract for getter method that defines type name
     * @param name type name
     */
    void setName(String name);

    /**
     * Contract for getter method that retrieves tenant id
     * @return Long for tenant id
     */
    Long getTenantId();

    /**
     * Contract for setter method that retrieves tenant id
     * @param tenantId Long for tenant id
     */
    void setTenantId(Long tenantId);

    /**
     * Contract for getter method that retrieves value for toBeShown property
     * @return boolean value for toBeShown property
     */
    boolean isToBeShown();

    /**
     * Contract for setter method that defines value for toBeShown property
     * @param toBeShown boolean value to be defined
     */
    void setToBeShown(boolean toBeShown);

    /**
     * Contract for getter method that retrieves value for toBeAccepted property
     * @return boolean value for toBeAccepted property
     */
    boolean isToBeAccepted();

    /**
     * Contract for setter method that defines value for toBeAccepted property
     * @param toBeAccepted boolean value to be defined
     */
    void setToBeAccepted(boolean toBeAccepted);
}
