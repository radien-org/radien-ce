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
 *
 */
package io.radien.ms.ecm.client.entities.legal;

import io.radien.api.model.legal.SystemLegalDocumentTypeSearchFilter;
import io.radien.api.search.SearchableByIds;
import java.util.Collection;

/**
 * Filter bean that encapsulates the necessary parameters to search/retrieve
 * Legal document types
 *
 * @author Newton Carvalho
 */
public class LegalDocumentTypeSearchFilter extends SearchableByIds
        implements SystemLegalDocumentTypeSearchFilter {

    private String name;
    private Long tenantId;
    private boolean toBeShown;
    private boolean toBeAccepted;

    /**
     * Default empty constructor
     */
    public LegalDocumentTypeSearchFilter() {}

    /**
     * Legal Document Type Search Filter constructor with given fields
     * @param name to be search
     * @param tenantId to be search
     * @param toBeShown to be search
     * @param toBeAccepted to be search
     * @param ids to be search
     * @param isExact to be search
     * @param isLogicConjunction to be search
     */
    public LegalDocumentTypeSearchFilter(String name, Long tenantId, boolean toBeShown,
                                         boolean toBeAccepted, Collection<Long> ids,
                                         boolean isExact, boolean isLogicConjunction) {
        super(ids, isExact, isLogicConjunction);
        this.setName(name);
        this.setTenantId(tenantId);
        this.setToBeShown(toBeShown);
        this.setToBeAccepted(toBeAccepted);
    }

    /**
     * Getter method regarding <strong>name</strong> search property
     * @return String value that corresponds to the search property (name)
     */
    public String getName() { return  name; }

    /**
     * Getter method regarding <strong>name</strong> search property
     * @param name String value that corresponds to the search property (name)
     */
    public void setName(String name) { this.name = name; }

    /**
     * Getter method regarding <strong>tenantId</strong> search property
     * @return Long value that corresponds to the search property (tenantId)
     */
    public Long getTenantId() { return this.tenantId; }

    /**
     * Setter method regarding <strong>tenantId</strong> search property
     * @param tenantId Long value that corresponds to the search property (tenantId)
     */
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    /**
     * Getter method regarding <strong>toBeShown</strong> search property
     * @return Boolean value that corresponds to the search property (toBeShown)
     */
    public Boolean isToBeShown() { return toBeShown; }

    /**
     * Setter method regarding <strong>toBeShown</strong> search property
     * @param toBeShown Boolean value that corresponds to the search property (toBeShown)
     */
    public void setToBeShown(Boolean toBeShown) { this.toBeShown = toBeShown; }

    /**
     * Getter method regarding <strong>toBeAccepted</strong> search property
     * @return Boolean value that corresponds to the search property (toBeAccepted)
     */
    public Boolean isToBeAccepted() { return this.toBeAccepted; }

    /**
     * Setter method regarding <strong>toBeAccepted</strong> search property
     * @param toBeAccepted Boolean value that corresponds to the search property (toBeAccepted)
     */
    public void setToBeAccepted(Boolean toBeAccepted) { this.toBeAccepted = toBeAccepted; }
}
