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

package io.radien.ms.tenantmanagement.service;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 * Contract/tenant management entity holder for the correct persistence unit
 * @author Bruno Gama
 */
@Stateful
public class EntityManagerHolder {

    @PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    /**
     * Entity manager getter
     * @return the correct specified and requested entity properties
     */
    public EntityManager getEm() {
        return em;
    }
}
