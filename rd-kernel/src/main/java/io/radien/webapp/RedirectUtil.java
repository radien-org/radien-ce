package io.radien.webapp;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

/**
 * @author Marco Weiland
 */
public @Model @RequestScoped class RedirectUtil implements RedirectHandler {
	protected static final Logger log = LoggerFactory.getLogger(RedirectUtil.class);
	private static final long serialVersionUID = 162788969745175865L;

	@Inject
	private OAFAccess oaf;
	private String publicIndex;
	
	@PostConstruct
	private void init() {
		this.publicIndex = getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
	}

	public static String combineURLSegment(String url, String segment) {
		if (url == null || url.isEmpty()) {
			return "/" + segment;
		}
		if (url.charAt(url.length() - 1) == '/' || segment.charAt(0) == '/' ) {
			return url + segment;
		}
		return url + "/" + segment;
	}

	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	@Override
	public void redirectTo(String url) {
		ExternalContext ec = JSFUtil.getExternalContext();
		String uri = ec.getRequestContextPath() + url;
		try {
			ec.redirect(uri);
			return;
		} catch (IOException e) {
			log.error("Problem with redirect to " + uri, e);
		}
		FacesContext fc = JSFUtil.getFacesContext();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "pretty:"+ url +"?faces-redirect=true");
	}

	@Override
	public void redirectToIndex(boolean openDefaultApp) {
		String url = openDefaultApp ? publicIndex+"?openApp=true" : publicIndex;
		redirectTo(url);
	}

	@Override
	public void redirectToPublicIndex() {
		redirectTo(publicIndex);
	}

	@Override
	public void redirectToLogout() {
		redirectTo("/logout");
	}

	@Override
	public void redirectToErrorPage(Exception exp) {
		Map<String, Object> infos = JSFUtil.getExternalContext().getSessionMap();
//		infos.put(OafExceptionHandler.ERROR_EXCEPTION, exp);
//		infos.put(OafExceptionHandler.ERROR_MESSAGE, exp.getMessage());
//		infos.put(OafExceptionHandler.ERROR_REQUEST_URI, JSFUtil.getRequest(JSFUtil.getFacesContext()).getRequestURI());
		redirectTo("/public/error");
	}
}
