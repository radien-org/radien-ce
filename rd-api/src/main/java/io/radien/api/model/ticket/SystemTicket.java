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

package io.radien.api.model.ticket;

import io.radien.api.Model;

import java.time.LocalDateTime;

public interface SystemTicket extends Model {

    public Long getUserId();

    public Long getTicketType();

    public LocalDateTime getExpireDate();

    public String getToken();

    public String getData();

    public void setUserId(Long userId);

    public void setTicketType(Long ticketType);

    public void setToken(String token);

    public void setData(String data);

    public void setExpireDate(LocalDateTime expireDate);

}
