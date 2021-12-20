/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
 *
 */
package io.radien.ms.ecm.client.services;

import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import io.radien.ms.ecm.client.util.LegalDocumentTypeMapper;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.junit.Assert;
import org.junit.Test;

import static io.radien.api.SystemVariables.ID;
import static io.radien.api.SystemVariables.NAME;
import static io.radien.api.SystemVariables.TENANT_ID;
import static io.radien.api.SystemVariables.TO_BE_ACCEPTED;
import static io.radien.api.SystemVariables.TO_BE_SHOWN;
import static io.radien.api.util.FactoryUtilService.addValue;

/**
 * Test class for {@link io.radien.ms.ecm.client.util.LegalDocumentTypeMapper}
 */
public class LegalDocumentTypeMapperTest {

    /**
     * Test for method {@link io.radien.ms.ecm.client.util.LegalDocumentTypeMapper#map(List)}
     */
    @Test
    public void test() {
        LegalDocumentType t = new LegalDocumentType();
        t.setId(1L);
        t.setName("ToU");
        t.setTenantId(2L);
        t.setToBeShown(true);
        t.setToBeAccepted(true);

        LegalDocumentType t2 = new LegalDocumentType();
        t2.setId(2L);
        t2.setName("Agreements");
        t2.setTenantId(3L);
        t2.setToBeShown(true);
        t2.setToBeAccepted(true);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, ID.getFieldName(), t.getId());
        addValue(builder, NAME.getFieldName(), t.getName());
        addValue(builder, TENANT_ID.getFieldName(), t.getTenantId());
        addValue(builder, TO_BE_ACCEPTED.getFieldName(), t.isToBeAccepted());
        addValue(builder, TO_BE_SHOWN.getFieldName(), t.isToBeShown());

        JsonObjectBuilder builder2 = Json.createObjectBuilder();
        addValue(builder2, ID.getFieldName(), t2.getId());
        addValue(builder2, NAME.getFieldName(), t2.getName());
        addValue(builder2, TENANT_ID.getFieldName(), t2.getTenantId());
        addValue(builder2, TO_BE_ACCEPTED.getFieldName(), t2.isToBeAccepted());
        addValue(builder2, TO_BE_SHOWN.getFieldName(), t2.isToBeShown());

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(builder.build());
        arrayBuilder.add(builder2.build());
        JsonArray expectedArray = arrayBuilder.build();

        JsonArray obtainedArray = LegalDocumentTypeMapper.map(Arrays.asList(t, t2));

        Assert.assertEquals(expectedArray, obtainedArray);
    }

}
