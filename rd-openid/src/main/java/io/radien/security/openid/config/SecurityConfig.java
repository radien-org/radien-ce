package io.radien.security.openid.config;

import io.radien.exception.GenericErrorCodeMessage;
import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ResourceHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.radien.security.openid.filter.OpenIdConnectFilter;

@EnableWebSecurity
@EnableOAuth2Client
public @RequestScoped class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
	private static final String LOGIN_PAGE = "/login";

	@Value("${auth.clientId}")
	private String clientId;

	@Value("${auth.clientSecret}")
	private String clientSecret;

	@Value("${auth.accessTokenUri}")
	private String accessTokenUri;

	@Value("${auth.userAuthorizationUri}")
	private String userAuthorizationUri;

	@Value("${auth.redirectUri}")
	private String redirectUri;

	@Value("${auth.logoutRedirectUri}")
	private String logoutRedirectUri;

	@Value("${api}")
	private String apiPrefix;

	@Value("${auth.csrfEnabled}")
	private boolean csrfEnabled;

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@Bean
	public OAuth2ProtectedResourceDetails openId() {
		final AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setClientId(clientId);
		details.setClientSecret(clientSecret);
		details.setAccessTokenUri(accessTokenUri);
		details.setUserAuthorizationUri(userAuthorizationUri);
		details.setScope(Arrays.asList("openid", "email", "profile"));
		details.setPreEstablishedRedirectUri(redirectUri);
		details.setUseCurrentUri(Boolean.FALSE);
		return details;
	}

	@Bean
	public OAuth2RestTemplate openIdTemplate(OAuth2ClientContext clientContext) {
		return new OAuth2RestTemplate(openId(), clientContext);
	}

	@Override
	public void configure(WebSecurity web) {
		RequestMatcher requestMatcher = request -> {
			String reqURI = request.getRequestURI();
			if(log.isDebugEnabled()){
				log.debug(GenericErrorCodeMessage.RESOURCE_URL_PATH.toString(), request.getRequestURL());
			}
			try {
				if (reqURI.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
					if(log.isInfoEnabled()){
						log.info(GenericErrorCodeMessage.RESOURCE_URL_PATH.toString(reqURI));
					}
					return true;
				}
			} catch (Exception e) {
				log.error(GenericErrorCodeMessage.ERROR_IN_CONFIGURE.toString(), e);
			}
			return false;
		};
		web.ignoring().requestMatchers(requestMatcher);

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// disable scrf protection with jsf
		if(!csrfEnabled) {
			http.csrf().disable();
		}

		http.headers().frameOptions().sameOrigin();

		RequestMatcher requestMatcher = request -> {
			String reqURI = request.getRequestURI();
			try {
				if (reqURI.contains(request.getContextPath() + "/module")) {
					return true;
				}
			} catch (Exception e) {
				log.error(GenericErrorCodeMessage.ERROR_IN_CONFIGURE.toString(), e);
			}
			return false;
		};

		RequestMatcher httpsRequestMatcher = request -> true;

		http.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
				.addFilterAfter(openIdConnectFilter(), OAuth2ClientContextFilter.class).httpBasic()
				.authenticationEntryPoint(this.authenticationEntryPoint())
				.and().authorizeRequests().requestMatchers(requestMatcher).authenticated()
				.and().exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint())
				.and().requiresChannel().requestMatchers(httpsRequestMatcher).requiresSecure();
	}

	@Bean
	public OpenIdConnectFilter openIdConnectFilter() {
		final OpenIdConnectFilter filter = new OpenIdConnectFilter(LOGIN_PAGE);
		filter.setRestTemplate(restTemplate);
		filter.setAuthenticationManager(new AuthenticationManagerSwitchOff());
		return filter;
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new AjaxAwareAuthenticationEntryPoint(LOGIN_PAGE);
	}

}