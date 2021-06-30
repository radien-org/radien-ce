package io.radien.security.oidc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//@WebFilter(filterName = "jwtFilter")
public class JwtFilter implements Filter {
    

    @Override
    public void init(FilterConfig filterConfig) {

        	String issuer = "";
        	String clientId = "";
        	String clientSecret = "";
        	
//			AccessTokenVerifier accessTokenVerifier = JwtVerifiers.accessTokenVerifierBuilder().setIssuer(issuer).build();
//			
//			JwtVerifiers.idTokenVerifierBuilder().setIssuer(issuer).setClientId(clientId).build();
        	
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        System.out.println("In JwtFilter, path: " + request.getRequestURI());

        // Get access token from authorization header
        String authHeader = request.getHeader("authorization");
        if (authHeader == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied.");
            return;
        } else {
           
//        	String accessToken = authHeader.substring(authHeader.indexOf("Bearer ") + 7);
//            try {
//                Jwt jwt = jwtVerifier.decodeAccessToken(accessToken);
//                System.out.println("Hello, " + jwt.getClaims().get("sub"));
//            } catch (JoseException e) {
//                e.printStackTrace();
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied.");
//                return;
//            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // empty
    }
} 