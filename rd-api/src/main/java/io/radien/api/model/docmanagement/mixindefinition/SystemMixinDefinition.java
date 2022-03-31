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

package io.radien.api.model.docmanagement.mixindefinition;

import io.radien.api.model.Model;
import java.util.List;

public interface SystemMixinDefinition<T> extends Model {

    String getName();

    void setName(String name);

    String getNamespace();

    void setNamespace(String namespace);

    List<T> getPropertyDefinitions();

    void setPropertyDefinitions(List<T> propertyDefinitions);

    boolean isAbstract();

    void setAbstract(boolean abstrakt);

    boolean isQueryable();

    void setQueryable(boolean queryable);

    boolean isMixin();

    void setMixin(boolean mixin);
}

