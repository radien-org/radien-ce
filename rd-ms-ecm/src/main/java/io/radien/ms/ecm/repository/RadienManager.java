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

import io.radien.ms.ecm.model.RadienModel;


/**
 * @author Marco Weiland
 *
 */
@ApplicationScoped
public class RadienManager {
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
    private AtomicInteger modelIdGenerator = new AtomicInteger(0);

    private ConcurrentMap<String, RadienModel> inMemoryStore = new ConcurrentHashMap<>();

    public RadienManager() {
        RadienModel model = new RadienModel();
        model.setId(getNextId());
        model.setMessage("This is how to microprofile");
        inMemoryStore.put(model.getId(), model);
    }

    private String getNextId() {
        String date = LocalDate.now().format(formatter);
        return String.format("%04d-%s", modelIdGenerator.incrementAndGet(), date);
    }

    public String add(RadienModel model) {
        String id = getNextId();
        model.setId(id);
        inMemoryStore.put(id, model);
        return id;
    }

    public RadienModel getModel(String id) {
        return inMemoryStore.get(id);
    }

    public List<RadienModel> getAll() {
        List<RadienModel> models = new ArrayList<>();
        models.addAll(inMemoryStore.values());
        return models;
    }
}
