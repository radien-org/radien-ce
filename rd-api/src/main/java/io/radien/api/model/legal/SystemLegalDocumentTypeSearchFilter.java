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

import io.radien.api.search.SystemSearchFilterCriteria;
import io.radien.api.search.SystemSearchableByIds;

/**
 * Contract description for Legal Document Type search filter
 *
 * @author Newton Carvalho
 */
public interface SystemLegalDocumentTypeSearchFilter extends SystemSearchFilterCriteria, SystemSearchableByIds {

    /**
     * Getter method regarding <strong>name</strong> search property
     * @return String value that corresponds to the search property (name)
     */
    String getName();

    /**
     * Getter method regarding <strong>name</strong> search property
     * @param name String value that corresponds to the search property (name)
     */
    void setName(String name);

    /**
     * Getter method regarding <strong>tenantId</strong> search property
     * @return Long value that corresponds to the search property (tenantId)
     */
    Long getTenantId();

    /**
     * Setter method regarding <strong>tenantId</strong> search property
     * @param tenantId Long value that corresponds to the search property (tenantId)
     */
    void setTenantId(Long tenantId);

    /**
     * Getter method regarding <strong>toBeShown</strong> search property
     * @return Boolean value that corresponds to the search property (toBeShown)
     */
    Boolean isToBeShown();

    /**
     * Setter method regarding <strong>toBeShown</strong> search property
     * @param toBeShown Boolean value that corresponds to the search property (toBeShown)
     */
    void setToBeShown(Boolean toBeShown);

    /**
     * Getter method regarding <strong>toBeAccepted</strong> search property
     * @return Boolean value that corresponds to the search property (toBeAccepted)
     */
    Boolean isToBeAccepted();

    /**
     * Setter method regarding <strong>toBeAccepted</strong> search property
     * @param toBeAccepted Boolean value that corresponds to the search property (toBeAccepted)
     */
    void setToBeAccepted(Boolean toBeAccepted);
}
