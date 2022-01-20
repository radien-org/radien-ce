package io.radien.ms.ticketmanagement.client.entities;

import io.radien.api.model.ticket.SystemTicketType;

import java.util.Arrays;

public enum TicketType implements SystemTicketType {

    EMAIL_CHANGE(1L, "email_change", 30),
    REFERENCE_LINK(2L, "reference_link", 10);

    TicketType(Long id, String type, int expirationPeriod){
        this.id = id;
        this.type = type;
        this.expirationPeriod = expirationPeriod;
    }

    private Long id;
    private String type;
    private int expirationPeriod;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public int getExpirationPeriod() {
        return expirationPeriod;
    }

    @Override
    public String getType() {
        return type;
    }

    public static TicketType getById(Long id) {
        return Arrays.stream(TicketType.values()).
                filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"type\":\"" + type + "\"" +
                "}";
    }
}
