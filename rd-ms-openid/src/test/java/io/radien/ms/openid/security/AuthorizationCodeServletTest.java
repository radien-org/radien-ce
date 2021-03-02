package io.radien.ms.openid.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizationCodeServletTest {

    @InjectMocks
    AuthorizationCodeServlet target;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void doGet() throws ServletException, IOException {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);
        target.doGet(req,resp);
        verify(resp).sendRedirect(any());
    }
}