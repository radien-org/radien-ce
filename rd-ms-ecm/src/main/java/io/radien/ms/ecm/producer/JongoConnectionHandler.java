/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.producer;

import io.radien.exception.SystemException;
import io.radien.ms.authz.util.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

@ApplicationScoped
public class JongoConnectionHandler {

    @Inject
    @ConfigProperty(name = "oak.mongo.db")
    private String mongoDB;
    @Inject
    @ConfigProperty(name = "mongo.uri")
    private String mongoUri;
    
    public <R> R apply(Function<MongoCollection, R> function, String collectionName) throws SystemException {
        try (MongoClient client = new MongoClient(mongoUri)) {
            DB db = client.getDB(mongoDB);
            Jongo jongo = new Jongo(db);
            
            return function.apply(jongo.getCollection(collectionName));
        }

    }
}
