/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.api.service.permission;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link SystemResourcesEnum}
 */
public class SystemResourcesEnumTest {

    /**
     * Test for getter {@link SystemResourcesEnum#getResourceName()}
     * taking in account Permissions regarding User domain
     */
    @Test
    public void testGetResourceNameForUserDomain() {
        assertEquals("User", SystemResourcesEnum.USER.getResourceName());
        assertEquals("Roles", SystemResourcesEnum.ROLES.getResourceName());
        assertEquals("Permission", SystemResourcesEnum.PERMISSION.getResourceName());
        assertEquals("Resource", SystemResourcesEnum.RESOURCE.getResourceName());
        assertEquals("Action", SystemResourcesEnum.ACTION.getResourceName());
        assertEquals("Tenant", SystemResourcesEnum.TENANT.getResourceName());
        assertEquals("Tenant Role", SystemResourcesEnum.TENANT_ROLE.getResourceName());
        assertEquals("Tenant Role Permission", SystemResourcesEnum.TENANT_ROLE_PERMISSION.getResourceName());
        assertEquals("Tenant Role User", SystemResourcesEnum.TENANT_ROLE_USER.getResourceName());
        assertEquals("Third Party Password", SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName());
    }

}
