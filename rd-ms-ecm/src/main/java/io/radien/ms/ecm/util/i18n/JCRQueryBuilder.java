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

package io.radien.ms.ecm.util.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class JCRQueryBuilder {
    private String from;
    private String fromAlias;
    private final List<String> where;
    private String joinTarget;
    private String joinTargetAlias;

    public JCRQueryBuilder() {
        where = new ArrayList<>();
    }

    public JCRQueryBuilder addFrom(String from, String fromAlias) {
        this.from = from;
        this.fromAlias = fromAlias;
        return this;
    }

    public JCRQueryBuilder addWhere(String clause) {
        where.add(clause);
        return this;
    }
    
    public JCRQueryBuilder addInnerJoin(String target, String targetAlias) {
        joinTarget = target;
        joinTargetAlias = targetAlias;
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        builder.append(MessageFormat.format("SELECT {0}.* FROM [{1}] AS {0}",
                fromAlias, from));
        if(joinTarget != null && !joinTarget.isEmpty()) {
            builder.append(MessageFormat.format(" INNER JOIN [{0}] AS {1} ON ISCHILDNODE({2})",
                    joinTarget, joinTargetAlias, String.join(", ", joinTargetAlias, fromAlias)));
        }
        if(!where.isEmpty()) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", where));
        }
        return builder.toString();
    }
}
