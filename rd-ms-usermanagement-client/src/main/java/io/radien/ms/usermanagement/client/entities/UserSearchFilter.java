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
package io.radien.ms.usermanagement.client.entities;

import io.radien.api.model.user.SystemUserSearchFilter;

import java.util.Collection;

/**
 * Encapsulates the parameters applied to search for users
 *
 * @author Nuno Santana
 */
public class UserSearchFilter implements SystemUserSearchFilter {
    private String sub;
    private String email;
    private String logon;
    private Collection<Long> ids;
    private boolean isExact;
    private boolean isLogicConjunction;

    /**
     * User search filter empty constructor
     */
    public UserSearchFilter(){}

    /**
     * User search field constructor
     * @param sub to be found
     * @param email to be found
     * @param logon to be found
     * @param ids to be search
     * @param isExact should the requested value be exact to the given one
     * @param isLogicConjunction true in case search option is and conjunction
     */
    public UserSearchFilter(String sub, String email, String logon, Collection<Long> ids, boolean isExact, boolean isLogicConjunction) {
        this.sub = sub;
        this.email = email;
        this.logon = logon;
        this.ids = ids;
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
    }

    /**
     * User search filter subject getter
     * @return the user search filter subject
     */
    @Override
    public String getSub() {
        return sub;
    }

    /**
     * User search filter subject setter
     * @param sub to be set
     */
    @Override
    public void setSub(String sub) {
        this.sub = sub;
    }

    /**
     * User search filter email getter
     * @return the user search filter email
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * User search filter email setter
     * @param email to be set
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * User search filter logon getter
     * @return the user search filter logon
     */
    @Override
    public String getLogon() {
        return logon;
    }

    /**
     * User search filter logon setter
     * @param logon to be set
     */
    @Override
    public void setLogon(String logon) {
        this.logon = logon;
    }

    /**
     * User search filter is to be search exact
     * @return is to be exact value
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * User search filter is to be search exact
     * @param exact to be set
     */
    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    /**
     * User is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * User filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }

    /**
     * User search filter get ids
     * @return ids for search filter
     */
    @Override
    public Collection<Long> getIds() { return ids; }

    /**
     * User search filter ids setter
     * @param ids to be set and replace
     */
    @Override
    public void setIds(Collection<Long> ids) { this.ids = ids; }
}
