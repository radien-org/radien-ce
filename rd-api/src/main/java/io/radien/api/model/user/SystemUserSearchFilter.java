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

package io.radien.api.model.user;

import java.util.Collection;

/**
 * System User search filter interface class
 * 
 * @author Bruno Gama
 */
public interface SystemUserSearchFilter {

    /**
     * User search filter get ids
     * @return ids for search filter
     */
    Collection<Long> getIds();

    /**
     * User search filter ids setter
     * @param ids to be set and replace
     */
    void setIds(Collection<Long> ids);

    /**
     * System User Search filter subject getter
     * @return System User Search filter subject
     */
    String getSub();

    /**
     * System User Search filter subject setter
     * @param sub to be set
     */
    void setSub(String sub);

    /**
     * System User Search filter email getter
     * @return System User Search filter email
     */
    String getEmail();

    /**
     * System User Search filter email setter
     * @param email to be set
     */
    void setEmail(String email);

    /**
     * System User Search filter logon getter
     * @return System User Search filter logon
     */
    String getLogon();

    /**
     * System User Search filter logon setter
     * @param logon to be set
     */
    void setLogon(String logon);

    /**
     * System User search filter is exact search getter
     * @return the System User search filter is exact value
     */
    boolean isExact();

    /**
     * System User search filter is exact setter
     * @param exact if true the search needs to be exactly as the given parameters
     */
    void setExact(boolean exact);

    /**
     * System User search filter is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    boolean isLogicConjunction();

    /**
     * System User search filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    void setLogicConjunction(boolean logicConjunction);
}
