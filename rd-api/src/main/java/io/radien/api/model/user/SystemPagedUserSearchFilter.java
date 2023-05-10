package io.radien.api.model.user;

import java.util.Collection;
import java.util.List;

public interface SystemPagedUserSearchFilter {
    Collection<Long> getIds();
    void setIds(Collection<Long> ids);
    String getSub();
    void setSub(String sub);
    String getFirstName();
    void setFirstName(String firstName);
    String getLastName();
    void setLastName(String lastName);
    String getEmail();
    void setEmail(String email);
    String getLogon();
    void setLogon(String logon);
    Boolean isEnabled();
    void setEnabled(Boolean enabled);
    Boolean isProcessingLocked();
    void setProcessingLocked(Boolean processingLocked);
    boolean isExact();
    void setExact(boolean exact);
    boolean isLogicConjunction();
    void setLogicConjunction(boolean logicConjunction);
}
