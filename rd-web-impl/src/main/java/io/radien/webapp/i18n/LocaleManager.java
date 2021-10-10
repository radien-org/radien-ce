/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.webapp.i18n;

import java.util.Locale;
import java.util.TimeZone;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import io.radien.api.OAFAccess;
import io.radien.api.webapp.i18n.LocaleManagerAccess;
import io.radien.webapp.AbstractLocaleManager;
import io.radien.webapp.JSFUtil;

/**
 * Class responsible for managing the i8n on openappframe resource bundle
 * messages
 *
 * @author Marco Weiland
 */
@Default
@Model
@Named("localeManager") // !!! IMPORTANT !!! @Named MUST be annotated for
						// programatically EL Evaluation of the bean
@SessionScoped
public class LocaleManager extends AbstractLocaleManager implements LocaleManagerAccess {
	private static final long serialVersionUID = 1L;

	@Inject
	private OAFAccess oaf;

	public void languageChanged(ValueChangeEvent e) {
		String newLocaleValue = e.getNewValue().toString();
		for (String language : super.getSupportedLanguages()) {
			if (language.equals(newLocaleValue)) {
				FacesContext.getCurrentInstance().getViewRoot().setLocale(oaf.findLocale(language));
			}
		}
	}

	public void timezoneChangedListener(AjaxBehaviorEvent event) {
		Locale requestLocale = JSFUtil.getExternalContext().getRequestLocale();
		FacesContext.getCurrentInstance().getViewRoot().setLocale(requestLocale);
		String tzOffset = getClientTzOffset();
		log.info(tzOffset);
		TimeZone tz = TimeZone.getTimeZone(tzOffset);
		log.info(tz.getDisplayName());

	}

	public OAFAccess getOAF() {
		return oaf;
	}

	@Override
	public String getUserLanguage() {
		return super.getActiveLanguage();
	}

}
