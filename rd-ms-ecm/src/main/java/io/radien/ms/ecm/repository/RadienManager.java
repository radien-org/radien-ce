/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
 */
package io.radien.ms.ecm.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;


/**
 * @author Marco Weiland
 *
 */
@ApplicationScoped
public class RadienManager {
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
    private AtomicInteger modelIdGenerator = new AtomicInteger(0);

    private ConcurrentMap<String, EnterpriseContent> inMemoryStore = new ConcurrentHashMap<>();

    public RadienManager() {
    	EnterpriseContent model = new GenericEnterpriseContent();
        model.setViewId(getNextId());
        model.setHtmlContent("This is how to microprofile");
        inMemoryStore.put(model.getViewId(), model);
    }

    private String getNextId() {
        String date = LocalDate.now().format(formatter);
        return String.format("%04d-%s", modelIdGenerator.incrementAndGet(), date);
    }

    public String add(EnterpriseContent model) {
        String id = getNextId();
        model.setViewId(id);
        inMemoryStore.put(id, model);
        return id;
    }

    public EnterpriseContent getModel(String id) {
        return inMemoryStore.get(id);
    }

    public List<EnterpriseContent> getAll() {
        List<EnterpriseContent> models = new ArrayList<>();
        models.addAll(inMemoryStore.values());
        return models;
    }
}
