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

package io.radien.ms.ticketmanagement.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "TIC01")
public class TicketEntity extends io.radien.ms.ticketmanagement.client.entities.Ticket{

    private static final long serialVersionUID = -8383376728635914496L;

    public TicketEntity(){}

    public TicketEntity(io.radien.ms.ticketmanagement.client.entities.Ticket ticket){super(ticket);}

    @Id
    @TableGenerator(name = "GEN_SEQ_TIC01", allocationSize = 100)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_TIC01")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column
    @Override
    public Long getUserId() {
        return super.getUserId();
    }

    @Column
    @Override
    public Long getTicketType() {
        return super.getTicketType();
    }

    @Column
    @Override
    public LocalDateTime getExpireDate() {
        return super.getExpireDate();
    }

    @Column (unique = true)
    @Override
    public String getToken() {
        return super.getToken();
    }

    @Column
    @Override
    public String getData() {
        return super.getData();
    }

}
