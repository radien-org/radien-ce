/**
 * 
 */
package io.radien.ms.contractmanagement.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

import io.radien.ms.contractmanagement.model.RadienModel;


/**
 * @author mawe
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
