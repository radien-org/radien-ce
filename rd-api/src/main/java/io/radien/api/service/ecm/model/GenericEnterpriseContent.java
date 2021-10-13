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
package io.radien.api.service.ecm.model;

import io.radien.api.service.ecm.exception.NameNotValidException;

/**
 * Generic Enterprise Content Manager class
 *
 * @author Marco Weiland
 */
public class GenericEnterpriseContent extends AbstractECMModel implements EnterpriseContent {
    private static final long serialVersionUID = 1L;

    public GenericEnterpriseContent(String name) throws NameNotValidException {
        this.name = name;
        if (name == null) {
            throw new NameNotValidException("name :" + name + " is not valid!");
        }
    }

    @Override
    public int compareTo(EnterpriseContent o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
