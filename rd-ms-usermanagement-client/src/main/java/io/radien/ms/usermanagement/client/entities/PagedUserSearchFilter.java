package io.radien.ms.usermanagement.client.entities;

import io.radien.api.model.user.SystemPagedUserSearchFilter;
import io.radien.api.search.SearchFilterCriteria;
import java.util.Collection;

public class PagedUserSearchFilter extends SearchFilterCriteria implements SystemPagedUserSearchFilter {
    private Collection<Long> ids;
    private String sub;
    private String firstName;
    private String lastName;
    private String email;
    private String logon;
    private Boolean enabled;
    private Boolean processingLocked;

    public PagedUserSearchFilter() {
    }

    public PagedUserSearchFilter(boolean isLogicConjunction, Collection<Long> ids, String sub, String firstName, String lastName, String email,
                                 String logon, Boolean enabled, Boolean processingLocked) {
        super(isLogicConjunction);
        this.ids = ids;
        this.sub = sub;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.logon = logon;
        this.enabled = enabled;
        this.processingLocked = processingLocked;
    }

    @Override
    public Collection<Long> getIds() {
        return ids;
    }

    @Override
    public void setIds(Collection<Long> ids) {
        this.ids = ids;
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
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isProcessingLocked() {
        return processingLocked;
    }

    @Override
    public void setProcessingLocked(Boolean processingLocked) {
        this.processingLocked = processingLocked;
    }
}
