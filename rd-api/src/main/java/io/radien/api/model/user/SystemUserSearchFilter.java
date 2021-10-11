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

package io.radien.api.model.user;

import io.radien.api.search.SystemSearchableByIds;

/**
 * System User search filter interface class
 * 
 * @author Bruno Gama
 */
public interface SystemUserSearchFilter extends SystemSearchableByIds {

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
}
