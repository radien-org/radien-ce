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

import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.ticket.SystemTicketSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;

import java.util.List;

public interface TicketServiceAccess extends ServiceAccess {

    public void create(SystemTicket ticket) throws TicketException, UniquenessConstraintException;

    public SystemTicket get(Long ticketId) throws SystemException;

    public Page<SystemTicket> getAll(SystemTicketSearchFilter filter, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    public void update(SystemTicket ticket) throws UniquenessConstraintException, TicketException;

    public boolean delete(Long ticketId);
}
