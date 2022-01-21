package io.radien.api.service.ticket;

import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;

import java.util.List;

public interface TicketServiceAccess extends ServiceAccess {

    public void create(SystemTicket ticket) throws TicketException, UniquenessConstraintException;

    public SystemTicket get(Long ticketId) throws SystemException;

    public Page<SystemTicket> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    public void update(SystemTicket ticket) throws UniquenessConstraintException, TicketException;

    public boolean delete(Long ticketId);
}
