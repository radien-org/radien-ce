package io.radien.ms.ecm.util.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JongoQueryBuilder {
    private List<String> query;

    public JongoQueryBuilder() {
        query = new ArrayList<>();
    }

    public JongoQueryBuilder addEquality(String key, String value) {
        query.add(String.format("%s:'%s'", key, value));
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(query.stream().collect(Collectors.joining(", ")));
        builder.append("}");
        return builder.toString();
    }
}
