package io.radien.webapp;


import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import java.io.Serializable;
import java.util.List;

@Model
@RequestScoped
public class UserDataModel implements Serializable {
    private static final long serialVersionUID = 9136267725285788804L;
    private static final Logger log = LoggerFactory.getLogger(UserDataModel.class);

    private List<? extends SystemUser> users;

    @Inject
    private UserRESTServiceAccess userRESTServiceClient;

    @PostConstruct
    public void init() {
        try {
            users = userRESTServiceClient.getAll(null, 1,10,null,true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<? extends SystemUser> getUsers() {
        return users;
    }

    public void login(){

    }

}
