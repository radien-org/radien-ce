package io.radien.ms.ticketmanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.ticket.SystemTicket;
import java.time.LocalDate;

public class Ticket extends AbstractModel implements SystemTicket {

    private Long userId;
    private Long ticketType;
    private LocalDate expireDate;
    private String token;
    private String data;

    public Ticket(){}

    public Ticket(Long userId, Long ticketType, LocalDate expireDate, String token, String data) {
        this.userId = userId;
        this.ticketType = ticketType;
        this.expireDate = expireDate;
        this.token = token;
        this.data = data;
    }

    public Ticket(Ticket ticket){
        this.userId = ticket.getUserId();
        this.ticketType = ticket.getTicketType();
        this.expireDate = ticket.getExpireDate();
        this.token = ticket.getToken();
        this.data = ticket.getData();
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public Long getTicketType() {
        return ticketType;
    }

    @Override
    public LocalDate getExpireDate() {
        return expireDate;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getData() {
        return data;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTicketType(Long ticketType) {
        this.ticketType = ticketType;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setData(String data) {
        this.data = data;
    }
}
