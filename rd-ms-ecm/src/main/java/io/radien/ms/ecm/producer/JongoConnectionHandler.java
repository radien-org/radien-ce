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
