package io.radien.api.service.ticket;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.exception.SystemException;

import java.util.List;
import java.util.Optional;

public interface TicketRESTServiceAccess {

    public Page<? extends SystemTicket> getAll(String search,
                                               int pageNo,
                                               int pageSize,
                                               List<String> sortBy,
                                               boolean isAscending) throws SystemException;

    public Optional<SystemTicket> getTicketById(Long id) throws SystemException;

    public boolean create(SystemTicket contract) throws SystemException;

    public boolean delete(long ticketId) throws SystemException;

    public boolean update(SystemTicket ticket) throws SystemException;



}
