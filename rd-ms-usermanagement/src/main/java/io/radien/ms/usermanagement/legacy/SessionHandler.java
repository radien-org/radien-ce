package io.radien.ms.usermanagement.legacy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * Main handler of all application session
 *
 * @author Bruno Gama
 */
public interface SessionHandler extends Serializable {
	public static final String ATTR_USER = "_USER";

    HttpSession getSession(boolean create);

    HttpSession getSession(boolean create, HttpServletRequest request, HttpServletResponse response);

    HttpServletRequest getRequest();

    SystemUser getUser();

    SystemUser getUser(HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException;

    void logout();

    void logout(HttpServletRequest request, HttpServletResponse response);

    void invalidate(HttpServletRequest request, HttpServletResponse response);

    void register(HttpServletRequest request, HttpServletResponse response, SystemUser user);

    SystemUser getUser(HttpServletRequest request);

}
