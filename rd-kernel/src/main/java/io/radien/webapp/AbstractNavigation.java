/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.webapp;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

/**
 * Class responsible for page navigation inside th openappframe web application
 *
 * @author Marco Weiland
 */
public abstract class AbstractNavigation implements Serializable {
	protected static final long serialVersionUID = 5486816910779729901L;
	protected static final Logger log = LoggerFactory.getLogger(AbstractNavigation.class);
	private static final String TRUE_STRING = "true";
	private static final String PREFIX_PRETTY = "pretty:";
	private static final String BLANK = "";
	private static final String WEB_ACTION = "webAction";

	private String paramNavigationNode;
	private String defaultLandingPage;

	private String activeNavigationNode;

	@PostConstruct
	protected void init() {

		HttpServletRequest request = (HttpServletRequest) JSFUtil.getExternalContext()
				.getRequest();
		request.getRequestURI();

		defaultLandingPage = getOAF().getProperty(OAFProperties.SYS_CFG_DEFAULT_LANDING_PAGE);
		//getOAF().getProperty(OAFProperties.SYS_CFG_JSF_MAPPING);
		activeNavigationNode = defaultLandingPage;
		paramNavigationNode = "navigationNode";

	}

	private String getPrettyPrefix() {
		return (getOAF().getProperty(OAFProperties.SYS_PRETTY_FACES_ENABLED).equalsIgnoreCase(TRUE_STRING)
				? PREFIX_PRETTY
				: BLANK);
	}

	public String navigate() {
		return getPrettyPrefix() + activeNavigationNode;
	}

	public String navigate(String navigationNode) {
		setActiveNavigationNode(navigationNode);
		return navigate();
	}

	public void navigationAction(ActionEvent event) {
		String navigationNode = JSFUtil.getString(event, paramNavigationNode, defaultLandingPage);
		setActiveNavigationNode(navigationNode);
	}

	public void navigationAction(AjaxBehaviorEvent event) {
		String navigationNode = JSFUtil.getString(event, paramNavigationNode, defaultLandingPage);
		setActiveNavigationNode(navigationNode);
	}

//	public void processCMSAction(AjaxBehaviorEvent event) {
//		navigationAction(event);
//		String webAction = JSFUtil.getString(event, WEB_ACTION, "");
//		processCMSAction(webAction);
//
//	}
//
//	public void processCMSAction(ActionEvent event) {
//		navigationAction(event);
//		String webAction = JSFUtil.getString(event, WEB_ACTION, "");
//		processCMSAction(webAction);
//	}

//	private void processCMSAction(String webAction) {
//		switch (webAction) {
//		case "save":
//			getCMS().save();
//			break;
//		case "close":
//			getCMS().close();
//			break;
//		case "delete":
//			getCMS().delete();
//			break;
//		case "add":
//			getCMS().load();
//			break;
//		default:
//			log.info("no webaction");
//			break;
//		}
//
//	}
//
//	protected void processDMSAction(String webAction) {
//		switch (webAction) {
//		case "save":
//			getDMS().save();
//			break;
//		case "close":
//			getDMS().close();
//			break;
//		case "delete":
//			getDMS().delete();
//			break;
//		default:
//			getDMS().load();
//			break;
//		}
//
//	}

	public void updateNavigationNode(String path) {
		this.activeNavigationNode = path;
	}

	public String getActiveNavigationNode() {
		return activeNavigationNode;
	}

	protected void setActiveNavigationNode(String navigationNode) {
		this.activeNavigationNode = navigationNode;
	}

	protected abstract OAFAccess getOAF();

//	protected abstract ContentManager getCMS();
//
//	protected abstract DocumentManager getDMS();

}
