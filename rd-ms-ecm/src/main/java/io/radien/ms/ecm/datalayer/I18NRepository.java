package io.radien.ms.ecm.datalayer;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.producer.JongoProducer;
import io.radien.ms.ecm.util.i18n.JongoQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

@RequestScoped
public class I18NRepository {
    @Inject
    private JongoProducer jongoProducer;

    private MongoCollection collection;

    @PostConstruct
    public void init() {
        collection = jongoProducer.getCollection(I18NProperty.class.getSimpleName());
    }

    public String getTranslation(String key, String language, String application) throws IllegalStateException {
        SystemI18NProperty property = findByKeyAndApplication(key, application);
        String finalLanguage = language;
        Optional<SystemI18NTranslation> languageTranslation = property.getTranslations().stream().filter(obj -> obj.getLanguage().equals(finalLanguage))
                .findFirst();
        if(languageTranslation.isPresent()) {
            return languageTranslation.get().getValue();
        }
        language = Locale.forLanguageTag(language).getLanguage();
        if(language.equals(finalLanguage)) {
            return key;
        }
        return getTranslation(key, language, application);
    }

    public void save(SystemI18NProperty entity) {
        if(!existsKeyAndApplication(entity.getKey(), entity.getApplication())) {
            collection.insert(entity);
        } else {
            String query = new JongoQueryBuilder()
                .addEquality("key", entity.getKey())
                .addEquality("application", entity.getApplication())
                .build();
            collection
                .update(query)
                .with(entity);
        }
    }

    public List<SystemI18NProperty> findAllByApplication(String application) {
        String query = new JongoQueryBuilder()
            .addEquality("application", application)
            .build();
        List<SystemI18NProperty> results = new ArrayList<>();
        collection.find(query)
                .as(SystemI18NProperty.class)
                .forEach(results::add);
        return results;
    }

    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws IllegalStateException {
        String query = new JongoQueryBuilder()
            .addEquality("key", key)
            .addEquality("application", application)
            .build();
    
        MongoCursor<I18NProperty> entities = collection
                .find(query).as(I18NProperty.class);
        if(entities.count() > 1) {
            throw new IllegalStateException("Multiple values found for the same key");
        }
        return entities.next();
    }

    private boolean existsKeyAndApplication(String key, String application) {
        String query = new JongoQueryBuilder()
            .addEquality("key", key)
            .addEquality("application", application)
            .build();
        return collection.count(query) != 0;
    }

}
