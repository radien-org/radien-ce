package io.radien.security.openid.filter;

import io.radien.security.openid.context.SecurityContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * The aims for this filter:
 * 1 - Intercepting requests
 * 2 - Check if the request refers a private URI
 * 3 - If is about a private URI and there is no Authentication information (user did not authenticate himself)
 * 4 - Redirects to the URI that triggers the authentication process.
 */
public class OpenIdURLChecker implements Filter {

    @Inject
    private SecurityContext securityContext;

    @Inject
    @ConfigProperty(name="auth.privateContexts", defaultValue = "/module")
    private String privateContexts;

    @Inject
    @ConfigProperty(name="auth.redirectUri")
    private String authenticationTriggerURI;

    /**
     * Where the interception takes place. The request will be checked,
     * if refers a private URI, and user is not authenticated yet, redirects
     * to an URI that triggers/starts the authentication process
     * @param servletRequest servlet request parameter (See {@link ServletRequest}
     * @param servletResponse servlet response parameter (See {@link ServletResponse}
     * @param chain filter chain parameter (See {@link FilterChain}
     * @throws IOException in case of any issue doing I/O processing
     * @throws ServletException in case of any issue processing servlet stuff
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (isPrivateURI(request) && securityContext.getUserDetails() == null) {
            response.sendRedirect(this.authenticationTriggerURI);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * Check if the current requests refers a private URI, in other words, if is a URI that can only
     * be accessed by an authenticated user.
     * @param request servlet request parameter from which the URI will be extracted
     * @return true if is a private URI, otherwise false
     */
    protected boolean isPrivateURI(HttpServletRequest request) {
        Collection<String> contexts = getPrivateContextList();
        for (String ctx:contexts) {
            if (request.getServletPath().startsWith(ctx)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits the privateContexts parameter (String) into a list of possible
     * private contexts to be checked against the URI
     * @return list containing String
     */
    protected List<String> getPrivateContextList() {
        List<String> privateContextList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String ctx:privateContexts.split(",")) {
            if (ctx.trim().length() > 0) {
                if (!ctx.startsWith("/")) {
                    sb.append("/");
                }
                sb.append(ctx);
                privateContextList.add(sb.toString());
            }
            sb.setLength(0);
        }
        return privateContextList;
    }
}
