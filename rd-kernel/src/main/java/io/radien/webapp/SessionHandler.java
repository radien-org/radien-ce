package io.radien.webapp;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.radien.api.model.identity.SystemUser;
import io.radien.exception.AccountNotValidException;
import io.radien.exception.UserNotFoundException;
import io.radien.util.OAFHttpRequest;

/**
 * Main handler of all application session
 *
 * @author Rafael Fernandes
 */
public interface SessionHandler extends Serializable {
	public static final String ATTR_USER = "_USER";

    HttpSession getSession(boolean create);

    HttpSession getSession(boolean create, HttpServletRequest request, HttpServletResponse response);

    HttpServletRequest getRequest();

    SystemUser getUser();

    SystemUser getUser(HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException;

    void register(SystemUser user) throws AccountNotValidException;

    void register(OAFHttpRequest req, SystemUser user) throws AccountNotValidException;

    void update(SystemUser user) throws AccountNotValidException;

    void logout();

    void logout(HttpServletRequest request, HttpServletResponse response);

    void register(HttpServletRequest request, HttpServletResponse response, SystemUser user);

    SystemUser getUser(HttpServletRequest request);

    void eraseCookies(ExternalContext externalContext);

    void eraseCookies(HttpServletRequest req, HttpServletResponse resp);

}
