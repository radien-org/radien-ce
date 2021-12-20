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

import io.radien.api.model.AbstractModel;
import io.radien.api.model.legal.SystemLegalDocumentType;

/**
 * Pojo bean that describes the Legal Document Type
 * @author Newton Carvalho
 */
public class LegalDocumentType extends AbstractModel implements SystemLegalDocumentType {

    private static final long serialVersionUID = -7525451140700503844L;
    private Long id;
    private String name;
    private Long tenantId;
    private boolean toBeShown;
    private boolean toBeAccepted;

    /**
     * Default empty constructor
     */
    public LegalDocumentType() {}

    /**
     * Constructor that uses a reference pojo to make assembling
     * @param documentType document
     */
    public LegalDocumentType(LegalDocumentType documentType) {
        this.setId(documentType.id);
        this.setName(documentType.name);
        this.setTenantId(documentType.tenantId);
        this.setToBeShown(documentType.toBeShown);
        this.setToBeAccepted(documentType.toBeAccepted);
    }

    /**
     * Retrieves identifier
     * @return Identifier value
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Defines identifier
     * @param id Identifier value to be defined
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Contract for getter method that retrieves type name
     * @return String for type name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Contract for getter method that defines type name
     * @param name type name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for Tenant Id property
     * @return tenant identifier
     */
    @Override
    public Long getTenantId() { return tenantId; }

    /**
     * Setter for Tenant Id property
     * @param tenantId Long for tenant id
     */
    @Override
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    /**
     * Contract for getter method that retrieves value for toBeShown property
     * @return boolean value for toBeShown property
     */
    @Override
    public boolean isToBeShown() {
        return toBeShown;
    }

    /**
     * Contract for setter method that defines value for toBeShown property
     * @param toBeShown boolean value to be defined
     */
    @Override
    public void setToBeShown(boolean toBeShown) {
        this.toBeShown = toBeShown;
    }

    /**
     * Contract for getter method that retrieves value for toBeAccepted property
     * @return boolean value for toBeAccepted property
     */
    @Override
    public boolean isToBeAccepted() {
        return toBeAccepted;
    }

    /**
     * Contract for setter method that defines value for toBeAccepted property
     * @param toBeAccepted boolean value to be defined
     */
    @Override
    public void setToBeAccepted(boolean toBeAccepted) {
        this.toBeAccepted = toBeAccepted;
    }
}
