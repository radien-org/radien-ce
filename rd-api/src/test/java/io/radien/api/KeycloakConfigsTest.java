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

        Assert.assertEquals(KeycloakConfigs.ADMIN_USER,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.ADMIN_USER.name()));

        Assert.assertEquals(KeycloakConfigs.ADMIN_PASSWORD,
                Enum.valueOf(KeycloakConfigs.class, KeycloakConfigs.ADMIN_PASSWORD.name()));

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
