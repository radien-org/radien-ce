package io.radien.ms.usermanagement.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import io.radien.api.Appframeable;
import io.radien.api.OAFAccess;

@Stateless
public class ConfigService implements Appframeable {

    private static final long serialVersionUID = 224814422013232692L;
    
    @Inject
    private OAFAccess oaf;

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

    
}
