/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.ms.openid.entities;

import io.radien.api.model.user.SystemUser;

import java.util.Date;

/**
 * This pojo/bean represents the user that is currently logged into the application
 * @author Newton Carvalho
 */
public class Principal implements SystemUser {

    private Long id;

    private String logon;
    private String userEmail;
    private String firstname;
    private String lastname;
    private String sub;
    private Date terminationDate;
    private boolean enabled;
    private boolean delegatedCreation;

    private Date createDate;
    private Date lastUpdate;
    private Long createUser;
    private Long lastUpdateUser;

    public Principal(){}

    public Principal(Principal p) {
        this.id = p.getId();
        this.logon = p.getLogon();
        this.userEmail = p.getUserEmail();
        this.firstname = p.getFirstname();
        this.lastname = p.getLastname();
        this.sub = p.getSub();
        this.terminationDate = p.getTerminationDate();
        this.enabled = p.enabled;
        this.setCreateDate(p.getCreateDate());
        this.setCreateUser(p.getCreateUser());
        this.setLastUpdateUser(p.getLastUpdateUser());
        this.setLastUpdate(p.getLastUpdate());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogon() {
        return logon;
    }

    public void setLogon(String logon) {
        this.logon = logon;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getSub() {
        return sub;
    }

    @Override
    public void setSub(String sub) {
        this.sub = sub;
    }

    public boolean isDelegatedCreation() {
        return delegatedCreation;
    }

    public void setDelegatedCreation(boolean delegatedCreation) {
        this.delegatedCreation = delegatedCreation;
    }

    @Override
    public Date getCreateDate() { return createDate; }

    @Override
    public void setCreateDate(Date createDate) { this.createDate = createDate; }

    @Override
    public Date getLastUpdate() { return lastUpdate; }

    @Override
    public void setLastUpdate(Date lastUpdate) { this.lastUpdate = lastUpdate; }

    @Override
    public Long getCreateUser() { return createUser; }

    @Override
    public void setCreateUser(Long createUser) { this.createUser = createUser; }

    @Override
    public Long getLastUpdateUser() { return lastUpdateUser; }

    @Override
    public void setLastUpdateUser(Long lastUpdateUser) { this.lastUpdateUser = lastUpdateUser; }
}
