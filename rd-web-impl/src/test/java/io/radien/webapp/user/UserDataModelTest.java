package io.radien.webapp.user;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.authz.WebAuthorizationChecker;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.*;

public class UserDataModelTest {

    @InjectMocks
    private UserDataModel userDataModel;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Check if the tenant association process is allowed to be started from
     * the following perspective:
     * <ul>
     *     <li>Current logged user has the right roles (the one who will start/trigger the process)</li>
     *     <li>There is a user available to be associated with a Tenant (i.e a newly created user
     *     or a previously selected one from the data grid)</li>
     * </ul>
     *
     * @return true if the process can be handled/started, false otherwise
     * Success Case
     */
    @Test
    public void testIsTenantAssociationProcessAllowedSuccessCase() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");
        SystemUser selectedUser = new User(); selectedUser.setId(3L); selectedUser.setLogon("a.b");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();
        doReturn(Optional.of(user)).when(userRESTServiceAccess).getUserByLogon(any());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertTrue(expected);

        // Set selectedUser
        this.userDataModel.setSelectedUser(selectedUser);

        expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertTrue(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No role available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoRoleAvailable() throws SystemException {
        doReturn(Boolean.FALSE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();

        // Call on load to check permission
        this.userDataModel.onload();

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No user available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoUserAvailable() throws SystemException {
        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();

        // Call on load to check permission
        this.userDataModel.onload();

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No found user available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoFoundUserAvailable() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();
        doReturn(Optional.empty()).when(userRESTServiceAccess).getUserByLogon(user.getLogon());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No found user available (Raise exception during user retrieval process)
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailException() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();
        doThrow(new SystemException("test")).when(userRESTServiceAccess).getUserByLogon(user.getLogon());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test getter and setter methods regarding hasTenantAdministratorRoles
     */
    @Test
    public void testGetterSetterForHasTenantAdministratorRoles() {
        this.userDataModel.setHasTenantAdministratorRoleAccess(true);
        assertTrue(this.userDataModel.isHasTenantAdministratorRoleAccess());
    }


    /**
     * Test getter and setter methods regarding userForTenantAssociation
     */
    @Test
    public void testGetterSetterForUserForTenantAssociation() {
        SystemUser user = new User(); user.setId(8L);
        this.userDataModel.setUserForTenantAssociation(user);
        assertEquals(this.userDataModel.getUserForTenantAssociation(), user);
    }


//
//    /**
//     * Check if the tenant association process is allowed to be started from
//     * the following perspective:
//     * <ul>
//     *     <li>Current logged user has the right roles (the one who will start/trigger the process)</li>
//     *     <li>There is a user available to be associated with a Tenant (i.e a newly created user
//     *     or a previously selected one from the data grid)</li>
//     * </ul>
//     * @return true if the process can be handled/started, false otherwise
//     */
//    public boolean isTenantAssociationProcessAllowed() {
//        if (!isHasTenantAdministratorRoleAccess()) {
//            return false;
//        }
//        this.userForTenantAssociation = findUserToAssociate();
//        return this.userForTenantAssociation != null;
//    }
//
//    /**
//     * Find the user that may participate on the tenant association process
//     * @return SystemUser instance, or null in case of not found any user
//     */
//    protected SystemUser findUserToAssociate() {
//        // Corresponds to the user picked from the data grid (user selected to be update, etc)
//        if (this.selectedUser != null && this.selectedUser.getId() != null) {
//            return this.selectedUser;
//        }
//        // Corresponds to the user recently created
//        if (this.user != null && !StringUtils.isEmpty(this.user.getLogon())) {
//            return getSystemUserFromLogon(this.user.getLogon());
//        }
//        return null;
//    }
//
//    /**
//     * Retrieve a SystemUser using logon as parameter
//     * @param logon parameter that will guide the search process
//     * @return User instance (if there is one available for the informed logon)
//     */
//    protected SystemUser getSystemUserFromLogon(String logon) {
//        try {
//            return service.getUserByLogon(logon).orElse(null);
//        } catch(Exception e) {
//            this.log.error("Error retrieving user from logon", e);
//            return null;
//        }
//    }
//
//    /**
//     * This method do some preparing process (Pre-Processing) before redirecting
//     * to the Tenant association screen
//     * @return url mapping that refers Tenant association screen
//     */
//    public String prepareTenantAssociation() {
//        return "pretty:userTenantAssociation";
//    }
//
//    /**
//     * Validates if current user has System Administrator or User Administrator roles
//     * @return true in case of so
//     */
//    public boolean getHasUserAdministratorRoleAccess() {
//        return hasUserAdministratorRoleAccess;
//    }
//
//    /**
//     * Sets if current user has System Administrator or User Administrator roles
//     */
//    public void setHasUserAdministratorRoleAccess(boolean hasUserAdministratorRoleAccess) {
//        this.hasUserAdministratorRoleAccess = hasUserAdministratorRoleAccess;
//    }
//
//    /**
//     * Flag indicating if current user has System Administrator or any other Tenant Administration role
//     * @return true if such condition is affirmative, false otherwise
//     */
//    public boolean isHasTenantAdministratorRoleAccess() {
//        return hasTenantAdministratorRoleAccess;
//    }
//
//    /**
//     * Sets a flag indicating if the current user has System Administrator or
//     * any other Tenant Administration role
//     * @param hasTenantAdministratorRoleAccess boolean value to be set
//     */
//    public void setHasTenantAdministratorRoleAccess(boolean hasTenantAdministratorRoleAccess) {
//        this.hasTenantAdministratorRoleAccess = hasTenantAdministratorRoleAccess;
//    }
//
//    /**
//     * Getter that refers tne User for whom will be applied the association with a tenant and a role
//     * @return user for whom will done the associaton
//     */
//    public SystemUser getUserForTenantAssociation() {
//        return userForTenantAssociation;
//    }
//
//    /**
//     * Setter that refers tne User for whom will be applied the association with a tenant and a role
//     * @param userForTenantAssociation  user for whom will done the associaton
//     */
//    public void setUserForTenantAssociation(SystemUser userForTenantAssociation) {
//        this.userForTenantAssociation = userForTenantAssociation;
//    }
}
