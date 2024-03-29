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
package io.radien.api.model.docmanagement.propertydefinition;

import io.radien.api.Model;
import javax.jcr.nodetype.PropertyDefinition;

public interface SystemPropertyDefinition extends Model, PropertyDefinition {
    String getName();

    void setName(String name);

    void setMandatory(boolean mandatory);

    // necessary to avoid java reserved keyword
    void setProtekted(boolean protekted);
    // necessary to avoid java reserved keyword
    boolean isProtekted();

    void setProtected(boolean protekted);

    void setRequiredType(int propertyType);

    void setMultiple(boolean multiple);
}
