package io.radien.ms.ecm.producer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

@ApplicationScoped
public class JongoProducer {

    @Inject
    @ConfigProperty(name = "oak.mongo.db")
    private String mongoDB;

    private Jongo jongo;

    @PostConstruct
    public void init() {
        DB db = new MongoClient().getDB(mongoDB);
        jongo = new Jongo(db);
    }

    public MongoCollection getCollection(String collectionName) {
        return jongo.getCollection(collectionName);
    }
    
}
