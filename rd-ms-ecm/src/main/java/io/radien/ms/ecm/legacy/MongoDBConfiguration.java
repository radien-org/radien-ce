package io.radien.ms.ecm.legacy;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBConfiguration {

    @Inject
    @ConfigProperty(name = "oak.mongo.db")
    private String mongoDbName;
    
    @Inject
    @ConfigProperty(name = "oak.mongo.uri")
    private String mongoDbHostname;

    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoDbHostname));
    }

    protected String getDatabaseName() {
        return mongoDbName;
    }

}