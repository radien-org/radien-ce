package io.radien.security.openid.model;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test class for {@link OpenIdConnectUserDetails}
 */
public class OpenIdConnectUserDetailsTest {

    /**
     * Test for Constructor
     */
    @Test
    public void testConstructor() {
        Map<String, String> map = new HashMap<>();
        map.put("sub", "1111-2222-3333-4444-55555");
        map.put("email", "echo.bunnyman@gmail.com");
        map.put("preferred_username", "echo.bunnyman");
        map.put("given_name", "echo");
        map.put("family_name", "bunnyman");

        OpenIdConnectUserDetails oud = new OpenIdConnectUserDetails(map);
        assertEquals(map.get("sub"), oud.getSub());
        assertEquals(map.get("email"), oud.getUserEmail());
        assertEquals(map.get("preferred_username"), oud.getUsername());
        assertEquals(map.get("given_name"), oud.getGivenname());
        assertEquals(map.get("family_name"), oud.getFamilyname());

        String formattedFullName = map.get("given_name") + " " + map.get("family_name");
        assertEquals(formattedFullName, oud.getFullName());
    }

    /**
     * Test for setter method {@link OpenIdConnectUserDetails#setSub(String)}
     */
    @Test
    public void testSetterSub() {
        String sub = "111";
        OpenIdConnectUserDetails oud = new OpenIdConnectUserDetails(new HashMap<>());
        oud.setSub(sub);
        assertEquals(sub, oud.getSub());
    }

    /**
     * Test for setter method {@link OpenIdConnectUserDetails#setGivenname(String)}
     */
    @Test
    public void testSetterGivenName() {
        String givenName = "a";
        OpenIdConnectUserDetails oud = new OpenIdConnectUserDetails(new HashMap<>());
        oud.setGivenname(givenName);
        assertEquals(givenName, oud.getGivenname());
    }

    /**
     * Test for setter method {@link OpenIdConnectUserDetails#setFamilyname(String)}
     */
    @Test
    public void testSetterFamilyName() {
        String familyName = "a";
        OpenIdConnectUserDetails oud = new OpenIdConnectUserDetails(new HashMap<>());
        oud.setFamilyname(familyName);
        assertEquals(familyName, oud.getFamilyname());
    }

    /**
     * Test for setter method {@link OpenIdConnectUserDetails#setFamilyname(String)}
     */
    @Test
    public void testSetterFullName() {
        String fullName = "a";
        OpenIdConnectUserDetails oud = new OpenIdConnectUserDetails(new HashMap<>());
        oud.setFullName(fullName);
        assertEquals(fullName, oud.getFullName());
    }
}
