/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package org.openappframe.webapp;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.webapp.PluginBridgingManager;

/**
 *
 *
 * @author Marco Weiland
 * @author Rafael Fernandes
 */
@Named("themeManager") // !!! IMPORTANT !!! @Named MUST be annotated for
						// programatically EL Evaluation of the bean
public @Model @RequestScoped class OafThemeManager extends PluginBridgingManager implements Serializable {
	public static final String DEFAULT_THEME = "oaf";

	protected static final Logger log = LoggerFactory.getLogger(OafThemeManager.class);
	private static final long serialVersionUID = 1L;
	@Inject
	private OAFAccess oaf;

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	public String getThemeCssClass() {
		return DEFAULT_THEME;

	}

	public String getLogoResourcePath() {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().evaluateExpressionGet(context,
				"#{resource['gfx/logos/" + getThemeCssClass() + ".png']}", String.class);

	}

	public String getPreloaderSvgName() {
		return getThemeCssClass();
	}

	public String getPreloaderResourcePath() {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().evaluateExpressionGet(context,
				"preloader-" + getPreloaderSvgName() + ".svg", String.class);
	}
}
