package io.radien.ms.usermanagement.client.entities;

import io.radien.api.model.user.SystemUserSearchFilter;

public class UserSearchFilter implements SystemUserSearchFilter {
    private String sub;
    private String email;
    private String logon;
    private boolean isExact;
    private boolean isLogicConjunction;

    public UserSearchFilter(){}

    public UserSearchFilter(String sub, String email, String logon, boolean isExact, boolean isLogicConjunction) {
        this.sub = sub;
        this.email = email;
        this.logon = logon;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
    }

    @Override
    public String getSub() {
        return sub;
    }

    @Override
    public void setSub(String sub) {
        this.sub = sub;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getLogon() {
        return logon;
    }

    @Override
    public void setLogon(String logon) {
        this.logon = logon;
    }

    @Override
    public boolean isExact() {
        return isExact;
    }

    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
