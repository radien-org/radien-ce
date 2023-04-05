package io.radien.api.model.tenant;

import java.util.Collection;

public interface SystemPagedTenantSearchFilter {
    Collection<Long> getIds();
    void setIds(Collection<Long> ids);
    String getName();
    void setName(String name);
    String getTenantKey();
    void setTenantKey(String tenantKey);
    SystemTenantType getTenantType();
    void setTenantType(SystemTenantType tenantType);
    String getClientAddress();
    void setClientAddress(String clientAddress);
    String getClientZipCode();
    void setClientZipCode(String clientZipCode);
    String getClientCity();
    void setClientCity(String clientCity);
    String getClientCountry();
    void setClientCountry(String clientCountry);
    String getClientPhoneNumber();
    void setClientPhoneNumber(String clientPhoneNumber);
    String getClientEmail();
    void setClientEmail(String clientEmail);
    Long getParentId();
    void setParentId(Long parentId);
    boolean isExact();
    void setExact(boolean exact);
    boolean isLogicConjunction();
    void setLogicConjunction(boolean logicConjunction);
}
