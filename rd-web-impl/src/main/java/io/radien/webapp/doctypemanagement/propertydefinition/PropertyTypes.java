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

package io.radien.webapp.doctypemanagement.propertydefinition;

public enum PropertyTypes {
    STRING("String", 1),
    BINARY("Binary", 2),
    LONG("Long", 3),
    DOUBLE("Double", 4),
    DATE("Date", 5),
    BOOLEAN("Boolean", 6),
    DECIMAL("Decimal", 12);

    private String typeName;
    private int typeValue;

    PropertyTypes(String typeName, int typeValue) {
        this.typeName = typeName;
        this.typeValue = typeValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeValue() {
        return typeValue;
    }
}
