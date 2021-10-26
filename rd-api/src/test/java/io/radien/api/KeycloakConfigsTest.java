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
package io.radien.api;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for KeycloakConfigs
 */
public class KeycloakConfigsTest {

    /**
     * Test enumeration values
     */
    @Test
    public void testEnumerationValue() {

        Assert.assertEquals(KeycloakConfigs.ADMIN_URL,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.ADMIN_URL.name()));

        Assert.assertEquals(KeycloakConfigs.ADMIN_REALM,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.ADMIN_REALM.name()));

        Assert.assertEquals(KeycloakConfigs.ADMIN_CLIENT_ID,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.ADMIN_CLIENT_ID.name()));

        Assert.assertEquals(KeycloakConfigs.ADMIN_CLIENT_SECRET,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.ADMIN_CLIENT_SECRET.name()));

        Assert.assertEquals(KeycloakConfigs.APP_REALM,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.APP_REALM.name()));

        Assert.assertEquals(KeycloakConfigs.IDP_URL,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.IDP_URL.name()));

        Assert.assertEquals(KeycloakConfigs.TOKEN_PATH,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.TOKEN_PATH.name()));

        Assert.assertEquals(KeycloakConfigs.RADIEN_CLIENT_ID,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.RADIEN_CLIENT_ID.name()));

        Assert.assertEquals(KeycloakConfigs.RADIEN_SECRET,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.RADIEN_SECRET.name()));

        Assert.assertEquals(KeycloakConfigs.RADIEN_TOKEN_PATH,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.RADIEN_TOKEN_PATH.name()));

        Assert.assertEquals(KeycloakConfigs.RADIEN_USERNAME,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.RADIEN_USERNAME.name()));

        Assert.assertEquals(KeycloakConfigs.RADIEN_PASSWORD,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.RADIEN_PASSWORD.name()));
    }

}
