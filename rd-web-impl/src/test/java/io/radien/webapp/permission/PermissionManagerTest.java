package io.radien.webapp.permission;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.webapp.JSFUtil;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

//import static org.junit.Assert.fail;
//import static org.powermock.api.mockito.PowerMockito.mock;
//import static org.powermock.api.mockito.PowerMockito.when;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({Logger.class, JSFUtil.class})
public class PermissionManagerTest {
//
//    @InjectMocks
//    private PermissionManager permissionManager;
//
//    @Mock
//    private PermissionRESTServiceAccess permissionRESTServiceAccess;
//
//    @Mock
//    private ActionRESTServiceAccess actionRESTServiceAccess;
//
//    @Mock
//    private ResourceRESTServiceAccess resourceRESTServiceAccess;
//
//    @Before
//    public void before(){
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testSavePermission() {
//
//    }
//
//    @Test
//    public void testSavePermissionWithFailure() {
//
//    }
//
//    @Test
//    public void testSaveAction() {
//        // Mocking FacesContext
//        PowerMockito.mockStatic(FacesContext.class);
//
//        FacesContext facesContext = mock(FacesContext.class);
//        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
//
//        SystemAction systemAction = new Action();
//        systemAction.setName("test");
//        try {
//            actionRESTServiceAccess.create(systemAction);
//        } catch (SystemException systemException) {
//            fail("");
//        }
//    }
//
//    @Test
//    public void testSaveActionWithFailure() {
//
//    }
//
//    @Test
//    public void testSaveResource() {
//
//    }
//
//    @Test
//    public void testSaveResourceWithFailure() {
//
//    }
}
