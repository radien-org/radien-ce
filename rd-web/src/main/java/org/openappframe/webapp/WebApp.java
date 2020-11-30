package org.openappframe.webapp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.webapp.AbstractWebapp;

/**
 * @author Marco Weiland
 */
public @ApplicationScoped @Model class WebApp extends AbstractWebapp {

	private static final long serialVersionUID = 162788969745175865L;
	@Inject
	private OAFAccess oaf;

	@Override
	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

}
