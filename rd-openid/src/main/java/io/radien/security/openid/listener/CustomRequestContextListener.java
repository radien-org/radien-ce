package io.radien.security.openid.listener;

import javax.servlet.annotation.WebListener;

import org.springframework.web.context.request.RequestContextListener;

@WebListener
public class CustomRequestContextListener extends RequestContextListener {
}
