package io.radien.ms.ecm.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class I18NProperty implements Serializable {
    @JsonProperty("key")
    private String key;

    @NotNull
    @JsonProperty("type")
    private LabelTypeEnum type;

    @JsonProperty("translations")
    private List<Translation> translations;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LabelTypeEnum getType() {
        return type;
    }

    public void setType(LabelTypeEnum type) {
        this.type = type;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18NProperty that = (I18NProperty) o;
        return key.equals(that.key) && type == that.type && translations.equals(that.translations);
    }

}
