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
package io.radien.ms.ecm.util;

import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.jnosql.diana.mongodb.document.MongoDBDocumentCollectionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Entity Manager for the I18N producer
 *
 * @author andresousa
 */
@ApplicationScoped
public class EntityManagerProducer {
    private static final String DATABASE = "cms_radien";

    @Inject
    @ConfigurationUnit(name = "document")
    private DocumentCollectionManagerFactory<MongoDBDocumentCollectionManager> managerFactory;

    @Produces
    public DocumentCollectionManager getEntityManager() {
        return managerFactory.get(DATABASE);
    }

    public void close(@Disposes DocumentCollectionManager entityManager) {
        entityManager.close();
    }
}
