package io.radien.kernel.service;

import io.radien.api.Configurable;
import io.radien.api.SystemProperties;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.ejb.Stateless;
import java.io.Serializable;


@Stateless
public class ConfigService implements Configurable, Serializable {

	private static final long serialVersionUID = 224814422013232692L;

	@Override
	public String getProperty(SystemProperties cfg) {
		Config config = ConfigProvider.getConfig();
		return config.getValue(cfg.propKey(),String.class);
	}

}
