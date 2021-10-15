package io.radien.webapp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
/**
 * Class that sets UTF-8 encoding in every request/response
 *
 * @author Marco Weiland
 * @author jrodrigues
 */
public class CharacterEncodingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
		// No need to implement this
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		servletRequest.setCharacterEncoding("UTF-8");
		servletResponse.setContentType("text/html; charset=UTF-8");
		servletResponse.setCharacterEncoding("UTF-8");

		chain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
		// No need to implement this
	}

}