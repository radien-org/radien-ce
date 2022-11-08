package io.radien.webapp.util;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.ticket.TicketRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.entities.TicketType;
import io.radien.ms.usermanagement.client.entities.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TicketConfirmationServletTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private TicketConfirmationServlet servlet;

    @Mock
    private TicketRESTServiceAccess ticketService;
    @Mock
    private UserRESTServiceAccess userService;
    @Mock
    private TenantRoleUserRESTServiceAccess tenantRoleUserService;
    @Mock
    private TenantRoleRESTServiceAccess tenantRoleService;

    @Test
    public void testDoGetNoTicketId() throws IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        servlet.doGet(req, res);

        verify(res).sendError(400, "Missing parameter");
    }

    @Test
    public void testDoGetProcessException() throws SystemException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getParameter("ticket")).thenReturn("ticketUuid");
        when(ticketService.getTicketByToken("ticketUuid")).thenThrow(new SystemException());

        servlet.doGet(req, res);
        verify(res).sendError(500, "Could not retrieve information");
    }

    @Test
    public void testDoGet() throws SystemException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter("ticket")).thenReturn("ticketUuid");
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(res.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

        SystemTicket mockTicket = new Ticket();
        mockTicket.setId(1L);
        mockTicket.setUserId(1L);
        mockTicket.setTicketType(TicketType.GDPR_DATA_REQUEST.getId());

        SystemUser mockUser = new User();
        List tenantList = new ArrayList<>();
        SystemTenant mockTenant = new Tenant();
        mockTenant.setId(1L);
        tenantList.add(mockTenant);

        when(tenantRoleUserService.getTenants(1L, null)).thenReturn(tenantList);
        when(tenantRoleService.getRolesForUserTenant(1L, 1L)).thenReturn(new ArrayList<>());
        when(ticketService.getTicketByToken("ticketUuid")).thenReturn(mockTicket);
        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));

        servlet.doGet(req, res);
        verify(res).setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testDoGetResponseMappingException() throws SystemException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter("ticket")).thenReturn("ticketUuid");
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(res.getOutputStream()).thenThrow(new IOException());

        SystemTicket mockTicket = new Ticket();
        mockTicket.setId(1L);
        mockTicket.setUserId(1L);
        mockTicket.setTicketType(TicketType.GDPR_DATA_REQUEST.getId());

        SystemUser mockUser = new User();
        List tenantList = new ArrayList<>();
        SystemTenant mockTenant = new Tenant();
        mockTenant.setId(1L);
        tenantList.add(mockTenant);

        when(tenantRoleUserService.getTenants(1L, null)).thenReturn(tenantList);
        when(tenantRoleService.getRolesForUserTenant(1L, 1L)).thenReturn(new ArrayList<>());
        when(ticketService.getTicketByToken("ticketUuid")).thenReturn(mockTicket);
        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));

        servlet.doGet(req, res);
        verify(res).sendError(500, "Could not retrieve information");
    }
}