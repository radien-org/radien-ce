/*
 * Copyright (c) 2006-present radien GmbH & its legal owners.
 * All rights reserved.<p>Licensed under the Apache License, Version 2.0
 * (the "License");you may not use this file except in compliance with the
 * License.You may obtain a copy of the License at<p>http://www.apache.org/licenses/LICENSE-2.0<p>Unless required by applicable law or
 * agreed to in writing, softwaredistributed under the License is distributed
 * on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.See the License for the specific language
 * governing permissions andlimitations under the License.
 */

package io.radien.ms.ticketmanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.ticket.SystemTicket;
import java.time.LocalDateTime;

public class Ticket extends AbstractModel implements SystemTicket {

    private Long userId;
    private Long ticketType;
    private LocalDateTime expireDate;
    private String token;
    private String data;

    public Ticket(){}

    public Ticket(Long userId, Long ticketType, LocalDateTime expireDate, String token, String data) {
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
    public LocalDateTime getExpireDate() {
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

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setData(String data) {
        this.data = data;
    }
}
