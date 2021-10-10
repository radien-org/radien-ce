/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

    /**
     * Principal object empty constructor
     */
    public Principal(){}

    /**
     * Principal object constructor
     * @param p principal to be created/used
     */
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

    /**
     * Principal id getter
     * @return the principal id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Principal id setter
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Principal logon getter
     * @return the principal logon
     */
    public String getLogon() {
        return logon;
    }

    /**
     * Principal logon setter
     * @param logon to be set
     */
    public void setLogon(String logon) {
        this.logon = logon;
    }

    /**
     * Principal termination date getter
     * @return the principal termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Principal termination date setter
     * @param terminationDate to be set
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Principal user email getter
     * @return the principal user email
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Principal user email setter
     * @param userEmail to be set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Principal first name getter
     * @return principal first name
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Principal first name setter
     * @param firstname to be set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Principal last name getter
     * @return principal last name
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Principal last name setter
     * @param lastname to be set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Principal is enable getter
     * @return principal is enable value
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Principal is enable setter
     * @param enabled to enable or disable user
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Principal subject getter
     * @return the principal subject
     */
    @Override
    public String getSub() {
        return sub;
    }

    /**
     * Principal subject setter
     * @param sub to be set
     */
    @Override
    public void setSub(String sub) {
        this.sub = sub;
    }

    /**
     * Principal is delegated creation getter
     * @return principal is delegated creation
     */
    public boolean isDelegatedCreation() {
        return delegatedCreation;
    }

    /**
     * Principal delegated creation setter
     * @param delegatedCreation to be set
     */
    public void setDelegatedCreation(boolean delegatedCreation) {
        this.delegatedCreation = delegatedCreation;
    }

    /**
     * Principal creation date getter
     * @return principal creation date
     */
    @Override
    public Date getCreateDate() { return createDate; }

    /**
     * Principal creation date setter
     * @param createDate to be set
     */
    @Override
    public void setCreateDate(Date createDate) { this.createDate = createDate; }

    /**
     * Principal last update getter
     * @return the principal last update date
     */
    @Override
    public Date getLastUpdate() { return lastUpdate; }

    /**
     * Principal last update date setter
     * @param lastUpdate to be set
     */
    @Override
    public void setLastUpdate(Date lastUpdate) { this.lastUpdate = lastUpdate; }

    /**
     * Principal create user getter
     * @return principal created user id
     */
    @Override
    public Long getCreateUser() { return createUser; }

    /**
     * Principal create user setter
     * @param createUser to be set
     */
    @Override
    public void setCreateUser(Long createUser) { this.createUser = createUser; }

    /**
     * Principal last update user getter
     * @return principal last update user
     */
    @Override
    public Long getLastUpdateUser() { return lastUpdateUser; }

    /**
     * Principal last update user setter
     * @param lastUpdateUser to be set
     */
    @Override
    public void setLastUpdateUser(Long lastUpdateUser) { this.lastUpdateUser = lastUpdateUser; }
}
