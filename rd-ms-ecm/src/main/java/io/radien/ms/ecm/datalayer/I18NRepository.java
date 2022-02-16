package io.radien.ms.ecm.datalayer;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.producer.JongoConnectionHandler;
import io.radien.ms.ecm.util.i18n.JongoQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

@RequestScoped
public class I18NRepository {
    @Inject
    private JongoConnectionHandler jongoConnectionHandler;

    public String getTranslation(String key, String language, String application) throws IllegalStateException, SystemException {
        SystemI18NProperty property = findByKeyAndApplication(key, application);
        if (property != null) {
            String finalLanguage = language;
            Optional<SystemI18NTranslation> languageTranslation = property.getTranslations().stream().filter(obj -> obj.getLanguage().equals(finalLanguage))
                    .findFirst();

            if (languageTranslation.isPresent()) {
                return languageTranslation.get().getValue();
            }
            language = Locale.forLanguageTag(language).getLanguage();
            if (language.equals(finalLanguage)) {
                return key;
            }
            return getTranslation(key, language, application);
        }
        return key;
    }

    public void save(SystemI18NProperty entity) throws SystemException {
        jongoConnectionHandler.apply(input -> {
            if(!existsKeyAndApplication(input, entity.getKey(), entity.getApplication())) {
                input.insert(entity);
            } else {
                String query = new JongoQueryBuilder()
                    .addEquality("key", entity.getKey())
                    .addEquality("application", entity.getApplication())
                    .build();
                input
                    .update(query)
                    .with(entity);
            }
            return null;
        }, I18NProperty.class.getSimpleName());
    }

    public void deleteProperty(SystemI18NProperty property) throws SystemException {
        jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                    .addEquality("key", property.getKey())
                    .addEquality("application", property.getApplication())
                    .build();
            input.remove(query);
            return null;
        }, I18NProperty.class.getSimpleName());
    }

    public void deleteApplication(String application) throws SystemException {
        jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                            .addEquality("application", application)
                            .build();
            input.remove(query);
            return null;
        }, I18NProperty.class.getSimpleName());
    }

    public List<SystemI18NProperty> findAllByApplication(String application) throws SystemException {
        return jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                    .addEquality("application", application)
                    .build();
            List<SystemI18NProperty> results = new ArrayList<>();
            input.find(query)
                    .as(SystemI18NProperty.class)
                    .forEach(results::add);
            return results;
        }, I18NProperty.class.getSimpleName());
    }

    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws IllegalStateException, SystemException {
        return jongoConnectionHandler.apply((input -> {
            String query = new JongoQueryBuilder()
                    .addEquality("key", key)
                    .addEquality("application", application)
                    .build();
            MongoCursor<I18NProperty> entities = input
                    .find(query).as(I18NProperty.class);
            if(entities.count() > 1) {
                throw new IllegalStateException("Multiple values found for the same key");
            }

            return entities.count() != 0 ? entities.next() : null;
        }), I18NProperty.class.getSimpleName());
    }

    private boolean existsKeyAndApplication(MongoCollection collection, String key, String application) {
        String query = new JongoQueryBuilder()
                .addEquality("key", key)
                .addEquality("application", application)
                .build();
        return collection.count(query) != 0;
    }

}
