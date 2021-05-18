package io.radien.ms.rolemanagement.client.services;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.TenantRoleModelMapper;
import io.radien.ms.rolemanagement.client.util.TenantRoleUserModelMapper;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleRESTServiceClientTest {

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationRESTServiceClient.class);

    @InjectMocks
    TenantRoleRESTServiceClient target;

    @Mock
    ClientServiceUtil roleServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    private String getRoleManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetTenantRoleById() {

        SystemTenantRole str = new TenantRole();
        str.setId(1L); str.setTenantId(2L); str.setRoleId(3L);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "id", 1);
        FactoryUtilService.addValueInt(builder, "tenantId", 2);
        FactoryUtilService.addValueInt(builder, "roleId", 3);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(stream);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getById(1L)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Optional<SystemTenantRole> result = assertDoesNotThrow(() ->
                target.getTenantRoleById(1L));

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getId(), str.getId());
    }

    @Test
    public void testGetTenantRoleByIdWithStatusNeqOK() {

        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getById(1L)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Optional<SystemTenantRole> result = assertDoesNotThrow(() ->
                target.getTenantRoleById(1L));

        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetTenantRoleByIdTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getTenantRoleById(1L));
    }

    @Test
    public void testGetTenantRoleByIdException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getTenantRoleById(1L));
    }

    @Test
    public void testSave() {

        TenantRole str = new TenantRole();
        str.setId(1L); str.setTenantId(2L); str.setRoleId(3L);

        Response response = Response.ok().build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.save(str)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() -> target.save(str));
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testSaveWithStatusNeqOK() {

        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.save(any())).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() ->
                target.save(new TenantRole()));

        assertNotNull(result);
        assertFalse(result);
    }

    @Test
    public void testSaveTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.save(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.save(new TenantRole()));
    }

    @Test
    public void testSaveException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.save(any())).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.save(new TenantRole()));
    }

    @Test
    public void testExists() {

        Response response = Response.ok(Boolean.TRUE).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.exists(1L, 2L)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() -> target.exists(1L, 2L));
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    public void testExistsWithStatusNeqOK() {

        Response response = Response.status(300).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.exists(any(), any())).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() ->
                target.exists(1L, 2L));

        assertNotNull(result);
        assertFalse(result);
    }

    @Test
    public void testExistsTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.exists(any(), any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.exists(1L, 2L));
    }

    @Test
    public void testExistsException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.exists(any(), any())).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.exists(1L, 2L));
    }

    @Test
    public void testGetPermissions() {

        String jsonArray = "[{\"id\": 1}, {\"id\": 2}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getPermissions(1L, 2L, 3L)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        List<? extends SystemPermission> result = assertDoesNotThrow(() -> target.getPermissions(1L, 2L, 3L));
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetPermissionsTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getPermissions(1L, 2L, 3L));
    }

    @Test
    public void testGetPermissionsException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getPermissions(1L, 2L, 3L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getPermissions(1L, 2L, 3L));
    }

    @Test
    public void testGetTenants() {

        String jsonArray = "[{\"id\": 1, \"tenantType\": \"CLIENT\"}, {\"id\": 2, \"tenantType\": \"SUB\"}]";
        InputStream is = new ByteArrayInputStream(jsonArray.getBytes());
        Response response = Response.ok(is).build();
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.getTenants(1L, 2L)).thenReturn(response);
        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        List<? extends SystemTenant> result = assertDoesNotThrow(() -> target.getTenants(1L, 2L));
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetTenantsTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getTenants(1L, 2L));
    }

    @Test
    public void testGetTenantsException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getTenants(1L, 2L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getTenants(1L, 2L));
    }

    @Test
    public void testAssignUser() {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.assignUser(1L, 2L, 3L)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(client.assignUser(1L, 2L, 4L)).
                thenReturn(Response.status(303).build());

        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() -> target.assignUser(1L, 2L, 3L));
        assertNotNull(result);
        assertTrue(result);

        result = assertDoesNotThrow(() -> target.assignUser(1L, 2L, 4L));
        assertNotNull(result);
        assertFalse(result);
    }

    @Test
    public void testAssignUserTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignUser(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.assignUser(1L, 2L, 3L));
    }

    @Test
    public void testAssignUserException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignUser(1L, 2L, 3L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.assignUser(1L, 2L, 3L));
    }

    @Test
    public void testUnAssignUser() {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.unassignUser(1L, 2L, 3L)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(client.unassignUser(1L, 2L, 4L)).
                thenReturn(Response.status(303).build());

        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() -> target.unassignUser(1L, 2L, 3L));
        assertNotNull(result);
        assertTrue(result);

        result = assertDoesNotThrow(() -> target.unassignUser(1L, 2L, 4L));
        assertNotNull(result);
        assertFalse(result);
    }

    @Test
    public void testUnAssignUserTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unassignUser(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.unassignUser(1L, 2L, 3L));
    }

    @Test
    public void testUnAssignUserException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unassignUser(1L, 2L, 3L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.unassignUser(1L, 2L, 3L));
    }

    @Test
    public void testAssignPermissionTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignPermission(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.assignPermission(1L, 2L, 3L));
    }

    @Test
    public void testAssignPermissionException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.assignPermission(1L, 2L, 3L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.assignPermission(1L, 2L, 3L));
    }

    @Test
    public void testAssignPermission() {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.assignPermission(1L, 2L, 3L)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(client.assignPermission(1L, 2L, 4L)).
                thenReturn(Response.status(303).build());

        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() -> target.assignPermission(1L, 2L, 3L));
        assertNotNull(result);
        assertTrue(result);

        result = assertDoesNotThrow(() -> target.assignPermission(1L, 2L, 4L));
        assertNotNull(result);
        assertFalse(result);
    }


    @Test
    public void testUnAssignPermission() {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(client.unassignPermission(1L, 2L, 3L)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(client.unassignPermission(1L, 2L, 4L)).
                thenReturn(Response.status(303).build());

        assertDoesNotThrow(() -> when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).
                thenReturn(client));

        Boolean result = assertDoesNotThrow(() -> target.unassignPermission(1L, 2L, 3L));
        assertNotNull(result);
        assertTrue(result);

        result = assertDoesNotThrow(() -> target.unassignPermission(1L, 2L, 4L));
        assertNotNull(result);
        assertFalse(result);
    }

    @Test
    public void testUnAssignPermissionTokenExpiration() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unassignPermission(1L, 2L, 3L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.unassignPermission(1L, 2L, 3L));
    }

    @Test
    public void testUnAssignPermissionException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.unassignPermission(1L, 2L, 3L)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.unassignPermission(1L, 2L, 3L));
    }

    @Test
    public void testTenantRoleList() throws Exception {
        String jsonArrayAsString = "[{\"id\": 1, \"tenantId\": 2, \"roleId\": 3}, " +
                "{\"id\": 2, \"tenantId\": 5, \"roleId\": 6}]";

        InputStream i = new ByteArrayInputStream(jsonArrayAsString.getBytes());
        Response response = Response.ok().entity(i).build();

        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(1L, 2L, true)).
                thenReturn(response);

        List<? extends SystemTenantRole> tenantRolesList =
                assertDoesNotThrow(() -> target.getTenantRoles(1L, 2L, true));
        assertNotNull(tenantRolesList);
        assertTrue(tenantRolesList.size() == 2);
    }


    @Test
    public void testTenantRoleListWithTokenException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(1L, 2L, true)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getTenantRoles(1L, 2L, true));
    }

    @Test
    public void testTenantRoleListWithException() throws Exception {
        TenantRoleResourceClient client = Mockito.mock(TenantRoleResourceClient.class);

        when(roleServiceUtil.getTenantResourceClient(getRoleManagementUrl())).thenReturn(client);
        when(client.getSpecific(1L, 2L, true)).thenThrow(new RuntimeException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getTenantRoles(1L, 2L, true));
    }
}
