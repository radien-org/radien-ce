package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.User;

import javax.ejb.Stateless;
import java.util.Date;

@Stateless
public class UserFactory {
    public User create(String firstname, String lastname, String logon, String sub, String email){
        User u = new User();
        u.setFirstname(firstname);
        u.setLastname(lastname);
        u.setLogon(logon);
        u.setEnabled(true);
        u.setSub(sub);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
        u.setUserEmail(email);
        return u;
    }
}
