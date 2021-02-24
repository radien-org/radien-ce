package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.tenant.SystemTenant;

public class Tenant extends AbstractModel implements SystemTenant {

    private Long id;
    private String name;

    public Tenant(){
    }

    public Tenant(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tenant(Tenant other) {
        this.id = other.getId();
        this.name = other.getName();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id=id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
