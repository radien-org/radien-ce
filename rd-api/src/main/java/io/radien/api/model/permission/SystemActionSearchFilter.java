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
package io.radien.api.model.permission;

/**
 * Contract description for actions search filter
 *
 * @author Newton Carvalho
 */
public interface SystemActionSearchFilter {

    /**
     * System Action search filter name getter
     * @return the system action search filter name
     */
    String getName();

    /**
     * System Action search filter name setter
     * @param name to be set
     */
    void setName(String name);

    /**
     * System Action search filter is to be search exact
     * @return is to be exact value
     */
    boolean isExact();

    /**
     * System Action search filter is to be search exact
     * @param exact to be set
     */
    void setExact(boolean exact);

    /**
     * System Action is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    boolean isLogicConjunction();

    /**
     * System Action filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    void setLogicConjunction(boolean logicConjunction);
}
