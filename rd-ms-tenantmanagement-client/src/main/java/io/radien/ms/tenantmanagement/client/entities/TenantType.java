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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenant.SystemTenantType;

import java.util.Arrays;

/**
 * Tenant type discriminator
 * @author Newton Carvalho
 */
public enum TenantType implements SystemTenantType {

    ROOT_TENANT(1L, "ROOT"),
    CLIENT_TENANT(2L, "CLIENT"),
    SUB_TENANT(3L, "SUB");

    /**
     * Tenant type constructor
     * @param id of the tenant type enum
     * @param description of the tenant type enum
     */
    private TenantType(Long id, String description) {
        setId(id);
        setDescription(description);
    }

    private Long id;
    private String description;

    /**
     * Tenant type id getter
     * @return the tenant type id
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Tenant type id setter
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Tenant type name getter
     * @return the tenant type name
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Tenant type name setter
     * @param description to be set
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method to retrieve the correct tenant type by a given specified name
     * @param description of the tenant tye to be retrieved
     * @return the requested tenant type
     */
    public static TenantType getByDescription(String description) {
        return Arrays.stream(TenantType.values()).
                filter(a -> a.getDescription().equalsIgnoreCase(description)).findFirst().orElse(null);
    }

    /**
     * Method to retrieve the correct tenant type by a given specified id
     * @param id of the tenant tye to be retrieved
     * @return the requested tenant type
     */
    public static TenantType getById(Long id) {
        return Arrays.stream(TenantType.values()).
                filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Returns a string representation of the object. In general, the toString method returns a string
     * that "textually represents" this object. The result should be a concise but informative representation
     * that is easy for a person to read. It is recommended that all subclasses override this method.
     * The toString method for class Object returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `@', and the unsigned hexadecimal representation of the
     * hash code of the object. In other words, this method returns a string equal to the value of:
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"description\":\"" + description + "\"" +
                "}";
    }
}
