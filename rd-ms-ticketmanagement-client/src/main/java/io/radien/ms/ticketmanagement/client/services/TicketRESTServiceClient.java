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

package io.radien.ms.ticketmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.service.ticket.TicketRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.util.ClientServiceUtil;
import io.radien.ms.ticketmanagement.client.util.TicketModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

@RequestScoped
@Default
public class TicketRESTServiceClient extends AuthorizationChecker implements TicketRESTServiceAccess {

    private static final long serialVersionUID = 4007939167636938896L;

    private static final Logger log = LoggerFactory.getLogger(TicketRESTServiceClient.class);

    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Override
    public boolean create(SystemTicket ticket) throws SystemException {
        return get(() -> {
            try {
                TicketResourceClient client = getClient();
                Response response = client.create((Ticket) ticket);
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (Exception e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_CREATING_TICKET.toString(ticket.toString()), e);
            }
        });
    }

    @Override
    public boolean delete(long ticketId) throws SystemException {
        return get(() -> {
            try {
                TicketResourceClient client = getClient();
                Response response = client.delete(ticketId);
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (Exception e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_DELETING_TICKET.toString(String.valueOf(ticketId)), e);
            }
        });
    }

    @Override
    public boolean update(SystemTicket ticket) throws SystemException {
        return get(() -> {
            try {
                TicketResourceClient client = getClient();
                Response response = client.update(ticket.getId(), (Ticket) ticket);
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (Exception e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_UPDATING_TICKET.toString(String.valueOf(ticket.getId())), e);
            }
        });
    }


    @Override
    public Page<? extends SystemTicket> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        return get(() -> {
            try {
                TicketResourceClient client = getClient();
                Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
                return TicketModelMapper.mapToPage((InputStream) response.getEntity());
            } catch (Exception e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_RETRIEVING_TICKETS.toString(), e);
            }
        });
    }

    @Override
    public Optional<SystemTicket> getTicketById(Long ticketId) throws SystemException {
        return get(() -> {
            try {
                TicketResourceClient client = getClient();
                Response response = client.getById(ticketId);
                return Optional.of(TicketModelMapper.map((InputStream) response.getEntity()));
            } catch (Exception e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_RETRIEVING_PROVIDED_TICKET.toString(String.valueOf(ticketId)), e);
            }
        });
    }

    private TicketResourceClient getClient() throws MalformedURLException {
        return clientServiceUtil.getTicketResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT));
    }

    @Override
    public OAFAccess getOAF() {
        return getOafAccess();
    }
}
