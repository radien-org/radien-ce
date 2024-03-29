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

package io.radien.api.service.ticket;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.ticket.SystemTicketSearchFilter;
import io.radien.exception.SystemException;

import java.util.List;
import java.util.Optional;

public interface TicketRESTServiceAccess extends Appframeable{

    public Page<? extends SystemTicket> getAll(SystemTicketSearchFilter filter,
                                               int pageNo,
                                               int pageSize,
                                               List<String> sortBy,
                                               boolean isAscending) throws SystemException;

    public Optional<SystemTicket> getTicketById(Long id) throws SystemException;

    public boolean create(SystemTicket contract) throws SystemException;

    public boolean delete(long ticketId) throws SystemException;

    public boolean update(SystemTicket ticket) throws SystemException;


    SystemTicket getTicketByToken(String token) throws SystemException;
}
