package io.radien.api.model.ticket;

import io.radien.api.Model;

public interface SystemTicketType {

    public Long getId();

    public int getExpirationPeriod();

    public String getType();
}
