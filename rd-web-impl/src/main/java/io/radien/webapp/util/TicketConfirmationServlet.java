package io.radien.webapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.ticket.TicketRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ticketmanagement.client.entities.TicketType;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet
@Named("/confirmData")
public class TicketConfirmationServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TicketConfirmationServlet.class);

    @Inject
    private TicketRESTServiceAccess ticketService;
    @Inject
    private UserRESTServiceAccess userService;
    @Inject
    private TenantRoleUserRESTServiceAccess tenantRoleUserService;
    @Inject
    private TenantRoleRESTServiceAccess tenantRoleService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String ticketUuid = req.getParameter("ticket");
        if(StringUtils.isEmpty(ticketUuid)) {
            try {
                resp.sendError(400, "Missing parameter");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return;
        }
        try {
            SystemTicket ticket = ticketService.getTicketByToken(ticketUuid);
            if(ticket.getTicketType().equals(TicketType.GDPR_DATA_REQUEST.getId())) {
                processDataRequest(resp, ticket.getUserId());
            }
            ticketService.delete(ticket.getId());
        } catch (SystemException | IOException e) {
            log.error(e.getMessage());
            try {
                resp.sendError(500, "Could not retrieve information");
            } catch (IOException ex) {
                log.error(e.getMessage());
            }
        }

    }

    private void processDataRequest(HttpServletResponse resp, Long userId) throws SystemException, IOException {
        SystemUser user = userService.getUserById(userId).orElseThrow(() -> new IllegalStateException("No user fond"));
        List<? extends SystemTenant> userTenants =  tenantRoleUserService.getTenants(userId, null);
        List<SystemRole> userTenantRoles = new ArrayList<>();
        userTenants.forEach(tenant -> {
            try {
                userTenantRoles.addAll(tenantRoleService.getRolesForUserTenant(userId, tenant.getId()));
            } catch (SystemException e) {
                log.error("Could not find details for {}", tenant);
            }
        });

        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.setHeader("Content-disposition", "attachment; filename=user-data.json");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            String userObj = objectMapper.writeValueAsString(user);
            String tenantInfo = objectMapper.writeValueAsString(userTenants);
            String roleInfo = objectMapper.writeValueAsString(userTenantRoles);
            resp.getOutputStream().write(MessageFormat.format("'{'userData: {0},tenantData: {1}, roleData: {2}'}'", userObj, tenantInfo, roleInfo).getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            resp.sendError(500, "Could not retrieve information");
        }
    }
}
