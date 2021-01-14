package io.radien.api.model.user;

public interface SystemUserSearchFilter {
    String getSub();

    void setSub(String sub);

    String getEmail();

    void setEmail(String email);

    String getLogon();

    void setLogon(String logon);

    boolean isExact();

    void setExact(boolean exact);

    boolean isLogicConjunction();

    void setLogicConjunction(boolean logicConjunction);
}
