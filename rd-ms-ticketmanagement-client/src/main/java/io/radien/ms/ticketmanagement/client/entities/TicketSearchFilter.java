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

package io.radien.ms.ticketmanagement.client.entities;

import io.radien.api.model.ticket.SystemTicketSearchFilter;
import io.radien.api.search.SearchFilterCriteria;

import java.time.LocalDate;

public class TicketSearchFilter extends SearchFilterCriteria implements SystemTicketSearchFilter {

    private Long userId;
    private Long ticketType;
    private LocalDate expireDate;
    private String token;
    private String data;

    public TicketSearchFilter(){}

    public TicketSearchFilter(Long userId, Long ticketType, LocalDate expireDate, String token, String data, boolean isLogicalConjunction) {
        super(isLogicalConjunction);
        this.userId = userId;
        this.ticketType = ticketType;
        this.expireDate = expireDate;
        this.token = token;
        this.data = data;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getTicketType() {
        return ticketType;
    }

    @Override
    public void setTicketType(Long ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public LocalDate getExpireDate() {
        return expireDate;
    }

    @Override
    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }
}
