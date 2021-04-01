/*
	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantType;

import java.time.LocalDate;

/**
 * @author Bruno Gama
 */
public class Tenant extends AbstractModel implements SystemTenant {

    private Long id;
    private String name;
    private String key;
    private TenantType type;
    private LocalDate start;
    private LocalDate end;

    private String clientAddress;
    private String clientZipCode;
    private String clientCity;
    private String clientCountry;
    private Long clientPhoneNumber;
    private String clientEmail;

    private Long parentId;
    private Long clientId;

    public Tenant(){
    }

    /**
     * Single and independent field for tenant constructor
     * @param id to be used
     * @param name of the tenant
     * @param key value of the tenant
     * @param type of the tenant
     * @param start date of the tenant
     * @param end date of the tenant
     * @param clientAddress in case of type being a client tenant client address
     * @param clientZipCode in case of type being a client tenant client address zip code
     * @param clientCity in case of type being a client tenant client city
     * @param clientCountry in case of type being a client tenant client country
     * @param clientPhoneNumber in case of type being a client tenant client phone number
     * @param clientEmail in case of type being a client tenant client email address
     * @param parentId tenant id to whom this tenant is bellow
     * @param clientId tenant id of the client tenant this tenant belongs to
     */
    public Tenant(Long id, String name, String key, TenantType type, LocalDate start, LocalDate end,
                    String clientAddress, String clientZipCode, String clientCity, String clientCountry,
                  Long clientPhoneNumber, String clientEmail, Long parentId, Long clientId) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.type = type;
        this.start = start;
        this.end = end;
        this.parentId = parentId;

        isClientTenantWithMultipleFields(type, clientAddress, clientZipCode, clientCity, clientCountry,
                clientPhoneNumber, clientEmail, clientId);
    }

    /**
     * Tenant constructor
     * @param tenant information to be created
     */
    public Tenant(Tenant tenant) {
        this.id = tenant.getId();
        this.name = tenant.getName();
        this.key = tenant.getKey();
        this.type = tenant.getType();
        this.start = tenant.getStart();
        this.end = tenant.getEnd();
        this.parentId = tenant.getParentId();

        isClientTenant(tenant);
    }

    /**
     * Manager for constructing tenants, will validate the type of the tenant and give the correct predefined values
     * to predefined fields
     * @param type of the tenant
     * @param clientAddress in case of type being a client tenant client address
     * @param clientZipCode in case of type being a client tenant client address zip code
     * @param clientCity in case of type being a client tenant client city
     * @param clientCountry in case of type being a client tenant client country
     * @param clientPhoneNumber in case of type being a client tenant client phone number
     * @param clientEmail in case of type being a client tenant client email address
     * @param clientId tenant id of the client tenant this tenant belongs to
     */
    private void isClientTenantWithMultipleFields(TenantType type, String clientAddress, String clientZipCode, String clientCity, String clientCountry,
                                                  Long clientPhoneNumber, String clientEmail, Long clientId) {
        if(type != null && type.equals(TenantType.CLIENT_TENANT)) {
            this.clientAddress = clientAddress;
            this.clientZipCode = clientZipCode;
            this.clientCity = clientCity;
            this.clientCountry = clientCountry;
            this.clientPhoneNumber = clientPhoneNumber;
            this.clientEmail = clientEmail;
            this.clientId = null;
        } else {
            this.clientAddress = null;
            this.clientZipCode = null;
            this.clientCity = null;
            this.clientCountry = null;
            this.clientPhoneNumber = null;
            this.clientEmail = null;
            this.clientId = clientId;
        }
    }

    /**
     * Manager for constructing tenants, will validate the type of the tenant and give the correct predefined values
     * to predefined fields
     * @param tenant information to be created
     */
    private void isClientTenant(Tenant tenant) {
        if(type != null && type.equals(TenantType.CLIENT_TENANT)) {
            this.clientAddress = tenant.getClientAddress();
            this.clientZipCode = tenant.getClientZipCode();
            this.clientCity = tenant.getClientCity();
            this.clientCountry = tenant.getClientCountry();
            this.clientPhoneNumber = tenant.getClientPhoneNumber();
            this.clientEmail = tenant.getClientEmail();
            this.clientId = null;
        } else {
            this.clientAddress = null;
            this.clientZipCode = null;
            this.clientCity = null;
            this.clientCountry = null;
            this.clientPhoneNumber = null;
            this.clientEmail = null;
            this.clientId = tenant.getClientId();
        }
    }

    /**
     * Getter for the tenant id
     * @return id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Setter for the tenant id
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the tenant name
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Setter for the tenant name
     * @param name to be set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the tenant key
     * @return key
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Setter for the tenant key
     * @param key to be set
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Getter for the tenant type
     * @return type
     */
    @Override
    public TenantType getType() {
        return type;
    }

    /**
     * Setter for the tenant type
     * @param type to be set
     */
    @Override
    public void setType(SystemTenantType type) {
        this.setType((TenantType) type);
    }

    /**
     * Setter for the tenant type
     * @param type to be set
     */
    public void setType(TenantType type) {
        this.type = type;
    }

    /**
     * Getter for the tenant start date
     * @return start date
     */
    @Override
    public LocalDate getStart() {
        return start;
    }

    /**
     * Setter for the tenant start date
     * @param start date to be set
     */
    @Override
    public void setStart(LocalDate start) {
        this.start = start;
    }

    /**
     * Getter for the tenant end date
     * @return end date
     */
    @Override
    public LocalDate getEnd() {
        return end;
    }

    /**
     * Setter for the tenant end date
     * @param end date to be set
     */
    @Override
    public void setEnd(LocalDate end) {
        this.end = end;
    }

    /**
     * Getter for the client address
     * @return client address
     */
    @Override
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * Setter for the client address
     * @param clientAddress to be set
     */
    @Override
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    /**
     * Getter for the client address zip code
     * @return client address zip code
     */
    @Override
    public String getClientZipCode() {
        return clientZipCode;
    }

    /**
     * Setter for the client address zip code
     * @param clientZipCode to be set
     */
    @Override
    public void setClientZipCode(String clientZipCode) {
        this.clientZipCode = clientZipCode;
    }

    /**
     * Getter for the client city
     * @return client city
     */
    @Override
    public String getClientCity() {
        return clientCity;
    }

    /**
     * Setter for the client city
     * @param clientCity to be set
     */
    @Override
    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    /**
     * Getter for the client country
     * @return client country
     */
    @Override
    public String getClientCountry() {
        return clientCountry;
    }

    /**
     * Setter for the client country
     * @param clientCountry to be set
     */
    @Override
    public void setClientCountry(String clientCountry) {
        this.clientCountry = clientCountry;
    }

    /**
     * Getter for the client phone number
     * @return client phone number
     */
    @Override
    public Long getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    /**
     * Setter for the client phone number
     * @param clientPhoneNumber to be set
     */
    @Override
    public void setClientPhoneNumber(Long clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    /**
     * Getter for the client email
     * @return client email
     */
    @Override
    public String getClientEmail() {
        return clientEmail;
    }

    /**
     * Setter for the client email
     * @param clientEmail to be set
     */
    @Override
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    /**
     * Getter for the tenant parent Id
     * @return parent id
     */
    @Override
    public Long getParentId() {
        return parentId;
    }

    /**
     * Setter for the parent id
     * @param parentId to be set
     */
    @Override
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * Getter for the tenant client Id
     * @return client id
     */
    @Override
    public Long getClientId() {
        return clientId;
    }

    /**
     * Setter for the client id
     * @param clientId to be set
     */
    @Override
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
