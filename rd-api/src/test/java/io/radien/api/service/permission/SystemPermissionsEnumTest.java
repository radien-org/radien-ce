/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
 * Test class for {@link SystemPermissionsEnum}
 */
public class SystemPermissionsEnumTest {

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding User domain
     */
    @Test
    public void testGetPermissionNameForUserDomain() {
        assertEquals("User Management - Create", SystemPermissionsEnum.USER_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("User Management - Read", SystemPermissionsEnum.USER_MANAGEMENT_READ.getPermissionName());
        assertEquals("User Management - Update", SystemPermissionsEnum.USER_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("User Management - Delete", SystemPermissionsEnum.USER_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("User Management - All", SystemPermissionsEnum.USER_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding Role domain
     */
    @Test
    public void testGetPermissionNameForRolesDomain() {
        assertEquals("Roles Management - Create", SystemPermissionsEnum.ROLES_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Roles Management - Read", SystemPermissionsEnum.ROLES_MANAGEMENT_READ.getPermissionName());
        assertEquals("Roles Management - Update", SystemPermissionsEnum.ROLES_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Roles Management - Delete", SystemPermissionsEnum.ROLES_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Roles Management - All", SystemPermissionsEnum.ROLES_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding Permission domain
     */
    @Test
    public void testGetPermissionNameForPermissionsDomain() {
        assertEquals("Permission Management - Create", SystemPermissionsEnum.PERMISSION_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Permission Management - Read", SystemPermissionsEnum.PERMISSION_MANAGEMENT_READ.getPermissionName());
        assertEquals("Permission Management - Update", SystemPermissionsEnum.PERMISSION_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Permission Management - Delete", SystemPermissionsEnum.PERMISSION_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Permission Management - All", SystemPermissionsEnum.PERMISSION_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding Resource domain
     */
    @Test
    public void testGetPermissionNameForResourcesDomain() {

        assertEquals("Resource Management - Create", SystemPermissionsEnum.RESOURCE_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Resource Management - Read", SystemPermissionsEnum.RESOURCE_MANAGEMENT_READ.getPermissionName());
        assertEquals("Resource Management - Update", SystemPermissionsEnum.RESOURCE_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Resource Management - Delete", SystemPermissionsEnum.RESOURCE_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Resource Management - All", SystemPermissionsEnum.RESOURCE_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding Action domain
     */
    @Test
    public void testGetPermissionNameForActionsDomain() {
        assertEquals("Action Management - Create", SystemPermissionsEnum.ACTION_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Action Management - Read", SystemPermissionsEnum.ACTION_MANAGEMENT_READ.getPermissionName());
        assertEquals("Action Management - Update", SystemPermissionsEnum.ACTION_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Action Management - Delete", SystemPermissionsEnum.ACTION_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Action Management - All", SystemPermissionsEnum.ACTION_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding Tenant domain
     */
    @Test
    public void testGetPermissionNameForTenantDomain() {

        assertEquals("Tenant Management - Create", SystemPermissionsEnum.TENANT_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Tenant Management - Read", SystemPermissionsEnum.TENANT_MANAGEMENT_READ.getPermissionName());
        assertEquals("Tenant Management - Update", SystemPermissionsEnum.TENANT_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Tenant Management - Delete", SystemPermissionsEnum.TENANT_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Tenant Management - All", SystemPermissionsEnum.TENANT_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding TenantRole domain (and subdomains like TenantRoleUser
     * and TenantRolePermission)
     */
    @Test
    public void testGetPermissionNameForTenantRoleDomain() {
        assertEquals("Tenant Role Management - Create", SystemPermissionsEnum.TENANT_ROLE_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Tenant Role Management - Read", SystemPermissionsEnum.TENANT_ROLE_MANAGEMENT_READ.getPermissionName());
        assertEquals("Tenant Role Management - Update", SystemPermissionsEnum.TENANT_ROLE_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Tenant Role Management - Delete", SystemPermissionsEnum.TENANT_ROLE_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Tenant Role Management - All", SystemPermissionsEnum.TENANT_ROLE_MANAGEMENT_ALL.getPermissionName());

        assertEquals("Tenant Role Permission Management - Create", SystemPermissionsEnum.TENANT_ROLE_PERMISSION_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Tenant Role Permission Management - Read", SystemPermissionsEnum.TENANT_ROLE_PERMISSION_MANAGEMENT_READ.getPermissionName());
        assertEquals("Tenant Role Permission Management - Update", SystemPermissionsEnum.TENANT_ROLE_PERMISSION_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Tenant Role Permission Management - Delete", SystemPermissionsEnum.TENANT_ROLE_PERMISSION_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Tenant Role Permission Management - All", SystemPermissionsEnum.TENANT_ROLE_PERMISSION_MANAGEMENT_ALL.getPermissionName());

        assertEquals("Tenant Role User Management - Create", SystemPermissionsEnum.TENANT_ROLE_USER_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Tenant Role User Management - Read", SystemPermissionsEnum.TENANT_ROLE_USER_MANAGEMENT_READ.getPermissionName());
        assertEquals("Tenant Role User Management - Update", SystemPermissionsEnum.TENANT_ROLE_USER_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Tenant Role User Management - Delete", SystemPermissionsEnum.TENANT_ROLE_USER_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Tenant Role User Management - All", SystemPermissionsEnum.TENANT_ROLE_USER_MANAGEMENT_ALL.getPermissionName());
    }

    /**
     * Test for getter {@link SystemPermissionsEnum#getPermissionName()}
     * taking in account Permissions regarding Third Password domain
     */
    @Test
    public void testGetPermissionNameForThirdPartyPasswordDomain() {
        assertEquals("Third Party Password Management - Create",
                SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_CREATE.getPermissionName());
        assertEquals("Third Party Password Management - Read",
                SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_READ.getPermissionName());
        assertEquals("Third Party Password Management - Update",
                SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getPermissionName());
        assertEquals("Third Party Password Management - Delete",
                SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_DELETE.getPermissionName());
        assertEquals("Third Party Password Management - All",
                SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_ALL.getPermissionName());
    }

}
