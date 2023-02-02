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

import io.radien.api.model.ticket.SystemTicketType;

import java.util.Arrays;

public enum TicketType implements SystemTicketType {

    EMAIL_CHANGE(1L, "email_change", 5),
    REFERENCE_LINK(2L, "reference_link", 14400),
    GDPR_DATA_REQUEST(3L, "gdpr_data_request", 34560);

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
