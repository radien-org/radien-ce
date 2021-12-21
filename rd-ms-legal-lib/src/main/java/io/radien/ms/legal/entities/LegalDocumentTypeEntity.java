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
package io.radien.ms.legal.entities;

import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * Tenant Role User association with JPA descriptors
 * @author Newton Carvalho
 */
@Entity
@Table(name = "LGL_DOC_TYP01", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "tenantId"}))
public class LegalDocumentTypeEntity extends LegalDocumentType {

    public LegalDocumentTypeEntity() {}

    public LegalDocumentTypeEntity(LegalDocumentType documentType) {
        super(documentType);
    }

    /**
     * Retrieves identifier
     * @return Identifier value
     */
    @Id
    @TableGenerator(name = "GEN_SEQ_LGL_DOC_TYP01", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_LGL_DOC_TYP01")
    @Override
    public Long getId() { return super.getId(); }

    /**
     * Getter method that retrieves type name
     * @return String for type name
     */
    @Column(nullable = false)
    @Override
    public String getName() { return super.getName(); }

    /**
     * Getter method that retrieves tenant identifier
     * @return tenant identifier
     */
    @Column
    @Override
    public Long getTenantId() { return super.getTenantId(); }

    /**
     * Getter method that retrieves value for toBeShown property
     * @return boolean value for toBeShown property
     */
    @Column
    @Override
    public boolean isToBeShown() { return super.isToBeShown(); }

    /**
     * Getter method that retrieves value for toBeAccepted property
     * @return boolean value for toBeAccepted property
     */
    @Column
    @Override
    public boolean isToBeAccepted() { return super.isToBeAccepted(); }
}
