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
package io.radien.api.search;

import java.util.Collection;

/**
 * Abstract class to reduce code duplication
 * regarding the common fields replicated (in this case ids) around filter
 * implementations
 */
public class SearchableByIds implements SystemSearchableByIds {
    private Collection<Long> ids;

    /**
     * Default constructor
     */
    public SearchableByIds() {}

    /**
     * Constructor where is possible to inform a list of ids
     * @param ids list of ids
     */
    public SearchableByIds(Collection<Long> ids) {
        this.ids = ids;
    }

    /**
     * Search filter get ids
     * @return ids for search filter
     */
    @Override
    public Collection<Long> getIds() { return ids; }

    /**
     * Search filter ids setter
     * @param ids to be set and replace
     */
    @Override
    public void setIds(Collection<Long> ids) { this.ids = ids; }
}
