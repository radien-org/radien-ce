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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenant.SystemTenantType;

import java.util.Arrays;

/**
 * @author Newton Carvalho
 * Tenant type descriminator
 */
public enum TenantType implements SystemTenantType {

    ROOT_TENANT(1L, "ROOT"),
    CLIENT_TENANT(2L, "CLIENT"),
    SUB_TENANT(3L, "SUB");

    private TenantType(Long id, String name) {
        setId(id);
        setName(name);
    }

    private Long id;
    private String name;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public static TenantType getByName(String name) {
        return Arrays.stream(TenantType.values()).
                filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static TenantType getById(Long id) {
        return Arrays.stream(TenantType.values()).
                filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"name\":\"" + name + "\"" +
                "}";
    }
}
