/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.api.search;

/**
 * Generic search filter criteria for searching clauses
 * is exact, is logical conjunction
 *
 * @author Bruno Gama
 **/
public class SearchFilterCriteria implements SystemSearchFilterCriteria{

    private boolean isExact;
    private boolean isLogicConjunction;

    /**
     * Default constructor
     */
    public SearchFilterCriteria() {}

    /**
     * Constructor where is possible to inform a the specific criteria
     * @param isLogicConjunction should the search criteria be with a and or a or
     */
    public SearchFilterCriteria(boolean isLogicConjunction) {
        this.isLogicConjunction = isLogicConjunction;
    }

    /**
     * Constructor where is possible to inform a the specific criteria
     * @param isExact should the search only look for exact values
     * @param isLogicConjunction should the search criteria be with a and or a or
     */
    public SearchFilterCriteria(boolean isExact, boolean isLogicConjunction) {
        this.isExact = isExact;
        this.isLogicConjunction = isLogicConjunction;
    }


    /**
     * Tenant search filter get is exact search
     * @return true or false value
     */
    @Override
    public boolean isExact() {
        return isExact;
    }

    /**
     * Tenant search filter set exact
     * @param exact to be set and updated
     */
    @Override
    public void setExact(boolean exact) {
        isExact = exact;
    }

    /**
     * Tenant search filter get is logical conjunction
     * @return true or false value
     */
    @Override
    public boolean isLogicConjunction() {
        return isLogicConjunction;
    }

    /**
     * Tenant search filter set is logic conjunction
     * @param logicConjunction to be set and updated
     */
    @Override
    public void setLogicConjunction(boolean logicConjunction) {
        isLogicConjunction = logicConjunction;
    }
}
