package io.radien.ms.usermanagement.client.entities;

import org.junit.Test;

import static io.radien.api.SystemVariables.CONFIRM_NEW_PASSWORD;
import static io.radien.api.SystemVariables.NEW_PASSWORD;
import static io.radien.api.SystemVariables.OLD_PASSWORD;
import static io.radien.exception.GenericErrorCodeMessage.INVALID_VALUE_FOR_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

/**
 * Test class for {@link UserPasswordChanging}
 * @author newton carvalho
 */
public class UserPasswordChangingTest {

    /**
     * Test for constructor
     */
    @Test
    public void testConstructor() {
        UserPasswordChanging u = new UserPasswordChanging();
        assertNotNull(u);
        assertNull(u.getLogin());
        assertNull(u.getOldPassword());
        assertNull(u.getNewPassword());
        assertNull(u.getConfirmNewPassword());
    }

    /**
     * Test setting null into old password with null
     */
    @Test
    public void testSettingOldPasswordWithNull() {
        UserPasswordChanging u = new UserPasswordChanging();
        String msg = INVALID_VALUE_FOR_PARAMETER.toString(OLD_PASSWORD.getLabel());
        IllegalArgumentException i = assertThrows(IllegalArgumentException.class, ()->u.setOldPassword(null));
        assertEquals(msg, i.getMessage());
    }

    /**
     * Test setting null into new password with null
     */
    @Test
    public void testSettingNewPasswordWithNull() {
        UserPasswordChanging u = new UserPasswordChanging();
        String msg = INVALID_VALUE_FOR_PARAMETER.toString(NEW_PASSWORD.getLabel());
        IllegalArgumentException i = assertThrows(IllegalArgumentException.class, ()->u.setNewPassword(null));
        assertEquals(msg, i.getMessage());
    }

    /**
     * Test setting null into confirm new password with null
     */
    @Test
    public void testSettingConfirmNewPasswordWithNull() {
        UserPasswordChanging u = new UserPasswordChanging();
        String msg = INVALID_VALUE_FOR_PARAMETER.toString(CONFIRM_NEW_PASSWORD.getLabel());
        IllegalArgumentException i = assertThrows(IllegalArgumentException.class, ()->u.setConfirmNewPassword(null));
        assertEquals(msg, i.getMessage());
    }

    /**
     * Test for setter {@link UserPasswordChanging#setOldPassword(String)}
     */
    @Test
    public void testSettingOldPassword() {
        UserPasswordChanging u = new UserPasswordChanging();
        String oldPass = "test";
        u.setOldPassword(oldPass);
        assertEquals(oldPass, u.getOldPassword());
    }


    /**
     * Test for setter {@link UserPasswordChanging#setOldPassword(String)}
     */
    @Test
    public void testSettingNewPassword() {
        UserPasswordChanging u = new UserPasswordChanging();
        String newPass = "test";
        u.setNewPassword(newPass);
        assertEquals(newPass, u.getNewPassword());
    }


    /**
     * Test for setter {@link UserPasswordChanging#setConfirmNewPassword(String)}
     */
    @Test
    public void testSettingConfirmNewPassword() {
        UserPasswordChanging u = new UserPasswordChanging();
        String confirmNewPass = "test";
        u.setConfirmNewPassword(confirmNewPass);
        assertEquals(confirmNewPass, u.getConfirmNewPassword());
    }

    /**
     * Test for setter for {@link UserPasswordChanging#setLogin(String)}
     */
    @Test
    public void testSettingLogin() {
        UserPasswordChanging u = new UserPasswordChanging();
        String login = "test";
        u.setLogin(login);
        assertEquals(login, u.getLogin());
    }


}
