package io.radien.api.model.ticket;

import io.radien.api.Model;

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDate;
import java.util.Date;

public interface SystemTicket extends Model {

    public Long getId();

    public Long getUserId();

    public Long getTicketType();

    public LocalDate getExpireDate();

    public String getToken();

    public String getData();

    public Date getCreateDate();

    public void setUserId(Long userId);

    public void setTicketType(Long ticketType);

    public void setToken(String token);

    public void setData(String data);
}
