package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.tenant.SystemPagedTenantSearchFilter;
import io.radien.api.model.tenant.SystemTenantType;
import io.radien.api.model.user.SystemPagedUserSearchFilter;
import io.radien.api.search.SearchFilterCriteria;
import java.util.Collection;

public class PagedTenantSearchFilter extends SearchFilterCriteria implements SystemPagedTenantSearchFilter {
    private Collection<Long> ids;
    private String name;
    private String tenantKey;
    private SystemTenantType tenantType;
    private String clientAddress;
    private String clientZipCode;
    private String clientCity;
    private String clientCountry;
    private String clientPhoneNumber;
    private String clientEmail;
    private Long parentId;

    public PagedTenantSearchFilter() {
    }

    public PagedTenantSearchFilter(boolean isLogicConjunction, Collection<Long> ids, String name, String tenantKey, SystemTenantType tenantType,
                                   String clientAddress, String clientZipCode, String clientCity, String clientCountry,
                                   String clientPhoneNumber, String clientEmail, Long parentId) {
        super(isLogicConjunction);
        this.ids = ids;
        this.name = name;
        this.tenantKey = tenantKey;
        this.tenantType = tenantType;
        this.clientAddress = clientAddress;
        this.clientZipCode = clientZipCode;
        this.clientCity = clientCity;
        this.clientCountry = clientCountry;
        this.clientPhoneNumber = clientPhoneNumber;
        this.clientEmail = clientEmail;
        this.parentId = parentId;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTenantKey() {
        return tenantKey;
    }

    @Override
    public void setTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
    }

    @Override
    public SystemTenantType getTenantType() {
        return tenantType;
    }

    @Override
    public void setTenantType(SystemTenantType tenantType) {
        this.tenantType = tenantType;
    }

    @Override
    public String getClientAddress() {
        return clientAddress;
    }

    @Override
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Override
    public String getClientZipCode() {
        return clientZipCode;
    }

    @Override
    public void setClientZipCode(String clientZipCode) {
        this.clientZipCode = clientZipCode;
    }

    @Override
    public String getClientCity() {
        return clientCity;
    }

    @Override
    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    @Override
    public String getClientCountry() {
        return clientCountry;
    }

    @Override
    public void setClientCountry(String clientCountry) {
        this.clientCountry = clientCountry;
    }

    @Override
    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    @Override
    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    @Override
    public String getClientEmail() {
        return clientEmail;
    }

    @Override
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
