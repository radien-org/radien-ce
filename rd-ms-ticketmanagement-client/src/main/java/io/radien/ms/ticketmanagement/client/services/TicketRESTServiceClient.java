package io.radien.ms.ticketmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.service.ticket.TicketRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.util.ClientServiceUtil;
import io.radien.ms.ticketmanagement.client.util.TicketModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class TicketRESTServiceClient extends AuthorizationChecker implements TicketRESTServiceAccess {

    private static final long serialVersionUID = 4007939167636938896L;

    private static final Logger log = LoggerFactory.getLogger(TicketRESTServiceClient.class);

    @Inject
    private ClientServiceUtil clientServiceUtil;


    @Inject
    private OAFAccess oafAccess;

    @Override
    public boolean create(SystemTicket ticket) throws SystemException {
        try {
            return createTicket((Ticket) ticket);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createTicket((Ticket) ticket);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    @Override
    public boolean delete(long ticketId) throws SystemException {
        try {
            return deleteRequester(ticketId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteRequester(ticketId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    @Override
    public boolean update(SystemTicket ticket) throws SystemException {
        try {
            return updateTicket(ticket);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return updateTicket(ticket);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    @Override
    public Page<? extends SystemTicket> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            return getTicketPage(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTicketPage(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    private Page<Ticket> getTicketPage(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            TicketResourceClient client = clientServiceUtil.getTicketResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return TicketModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    @Override
    public Optional<SystemTicket> getTicketById(Long ticketId) throws SystemException {
        try {
            return getSystemTicket(ticketId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getSystemTicket(ticketId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    private Optional<SystemTicket> getSystemTicket(Long id) throws SystemException {
        try {
            TicketResourceClient client = clientServiceUtil.getTicketResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(TicketModelMapper.map((InputStream) response.getEntity()));
        }  catch (ExtensionException | ProcessingException | MalformedURLException | ParseException es){
            throw new SystemException(es.getMessage());
        }
    }

    private boolean createTicket(Ticket ticket) throws SystemException {
        TicketResourceClient client;
        try {
            client = clientServiceUtil.getTicketResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.create(ticket)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    private boolean deleteRequester(long tenantId) throws SystemException {
        TicketResourceClient client;
        try {
            client = clientServiceUtil.getTicketResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.delete(tenantId)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    private boolean updateTicket(SystemTicket ticket) throws SystemException {
        TicketResourceClient client;
        try {
            client = clientServiceUtil.getTicketResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.update(ticket.getId(),(Ticket) ticket)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }
}
