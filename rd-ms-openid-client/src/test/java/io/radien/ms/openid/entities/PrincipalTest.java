package io.radien.ms.openid.entities;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class PrincipalTest {

    private Principal principal = new Principal();
    private Date terminationDate = new Date();

    public PrincipalTest() {
        principal.setId(2L);
        principal.setLogon("testLogon");
        principal.setUserEmail("testEmail@testEmail.pt");
        principal.setFirstname("testFirstName");
        principal.setLastname("testLastName");
        principal.setSub("42a64cb0-4600-11eb-b378-0242ac130002");
        principal.setTerminationDate(terminationDate);
        principal.setEnabled(true);
    }

    @Test
    public void getId() {
        assertSame(2L, principal.getId());
    }

    @Test
    public void setId() {
        principal.setId(3L);
        assertSame(3L, principal.getId());
    }

    @Test
    public void getLogon() {
        assertEquals("testLogon", principal.getLogon());
    }

    @Test
    public void setLogon() {
        principal.setLogon("testLogonSetter");
        assertEquals("testLogonSetter", principal.getLogon());
    }

    @Test
    public void getTerminationDate() {
        assertEquals(terminationDate, principal.getTerminationDate());
    }

    @Test
    public void setTerminationDate() {
        Date terminationDateSetter = new Date();
        principal.setTerminationDate(terminationDateSetter);
        assertEquals(terminationDateSetter, principal.getTerminationDate());
    }

    @Test
    public void getUserEmail() {
        assertEquals("testEmail@testEmail.pt", principal.getUserEmail());
    }

    @Test
    public void setUserEmail() {
        principal.setUserEmail("testEmailSetter@testEmailSetter.pt");
        assertEquals("testEmailSetter@testEmailSetter.pt", principal.getUserEmail());
    }

    @Test
    public void getFirstname() {
        assertEquals("testFirstName", principal.getFirstname());
    }

    @Test
    public void setFirstname() {
        principal.setFirstname("testFirstNameSetter");
        assertEquals("testFirstNameSetter", principal.getFirstname());
    }

    @Test
    public void getLastname() {
        assertEquals("testLastName", principal.getLastname());
    }

    @Test
    public void setLastname() {
        principal.setLastname("testLastNameSetter");
        assertEquals("testLastNameSetter", principal.getLastname());
    }

    @Test
    public void isEnabled() {
        assertTrue(principal.isEnabled());
    }

    @Test
    public void setEnabled() {
        principal.setEnabled(false);
        assertFalse(principal.isEnabled());
    }

    @Test
    public void getSub() {
        assertEquals("42a64cb0-4600-11eb-b378-0242ac130002", principal.getSub());
    }

    @Test
    public void setSub() {
        principal.setSub("329a6251-f891-475a-a655-1cf59dc52b25");
        assertEquals("329a6251-f891-475a-a655-1cf59dc52b25", principal.getSub());
    }
}
