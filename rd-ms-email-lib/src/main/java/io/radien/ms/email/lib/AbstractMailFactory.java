/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.email.lib;

import com.healthmarketscience.rmiio.RemoteInputStream;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract Factory class responsible for creating system emails
 *
 * @author Marco Weiland
 */
public abstract class AbstractMailFactory {

	private static final Logger log = LoggerFactory.getLogger(AbstractMailFactory.class);
	private static final String MAILFROM_MUST_NOT_BE_EMPTY = "MAILFROM MUST NOT BE EMPTY!";

	public Mail create(String tO) {
		return create(getOAF().getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), tO,
				getOAF().getProperty(OAFProperties.SYS_MAIL_SUBJECT),
				getOAF().getProperty(OAFProperties.SYS_MAIL_BODY), MailContentType.HTML);
	}

	public Mail create(String from, String tO, String subject, String body, MailContentType contentType) {

		List<String> tOList = new ArrayList<>();
		if (tO != null && !tO.isEmpty()) {
			tOList.add(tO);
		}

		return create(from, tOList, subject, body, contentType);
	}

	public Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType) {

		return create(from, tO, subject, body, contentType, null, null, null);
	}

	public Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType,
			Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC) {
		return handleParameters(from, tO, subject, body, contentType, attachments, cC, bCC);
	}

	private Mail handleParameters(String from, List<String> tO, String subject, String body,
			MailContentType contentType, Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC) {

		if (from == null) {
			throw new IllegalArgumentException(MAILFROM_MUST_NOT_BE_EMPTY);
		}

		if (subject == null || subject.equalsIgnoreCase("")) {
			throw new IllegalArgumentException(MAILFROM_MUST_NOT_BE_EMPTY);
		}

		if (body == null || body.equalsIgnoreCase("")) {
			throw new IllegalArgumentException(MAILFROM_MUST_NOT_BE_EMPTY);
		}

		if (contentType == null) {
			contentType = MailContentType.HTML;
		}

		if (contentType == MailContentType.HTML) {
			body = getBodyMessage(subject, body);
		}

		if (tO == null || tO.isEmpty()) {
			throw new IllegalArgumentException("MAILTO MUST NOT BE EMPTY!");
		}
		if (cC == null || cC.isEmpty()) {
			cC = new ArrayList<>();
		}
		if (bCC == null || bCC.isEmpty()) {
			bCC = new ArrayList<>();
		}

		log.info("Email parameters: body[{}], cc[{}], bcc[{}]", body, cC, bCC);
		return new MailMessage(from, tO, subject, body, contentType, attachments, cC, bCC);
	}

	private String getLogoBase64() {
		return "<img src='data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwIDAgMTA1LjQzIDg0LjM0Ij48ZGVmcz48c3R5bGU+LmNscy0xe2ZpbGw6dXJsKCNVbmJlbmFubnRlcl9WZXJsYXVmXzE4NjMpO30uY2xzLTJ7ZmlsbDojMWQxZDFiO308L3N0eWxlPjxyYWRpYWxHcmFkaWVudCBpZD0iVW5iZW5hbm50ZXJfVmVybGF1Zl8xODYzIiBjeD0iNDIuMTciIGN5PSI0Mi4xNyIgcj0iNDIuMTciIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIj48c3RvcCBvZmZzZXQ9IjAiIHN0b3AtY29sb3I9IiNlNjMzMmEiIHN0b3Atb3BhY2l0eT0iMC40Ii8+PHN0b3Agb2Zmc2V0PSIwLjIyIiBzdG9wLWNvbG9yPSIjZTYzNDI5IiBzdG9wLW9wYWNpdHk9IjAuNDEiLz48c3RvcCBvZmZzZXQ9IjAuMzkiIHN0b3AtY29sb3I9IiNlNjM2MjciIHN0b3Atb3BhY2l0eT0iMC40NSIvPjxzdG9wIG9mZnNldD0iMC41NCIgc3RvcC1jb2xvcj0iI2U3M2EyNCIgc3RvcC1vcGFjaXR5PSIwLjUyIi8+PHN0b3Agb2Zmc2V0PSIwLjY3IiBzdG9wLWNvbG9yPSIjZTc0MDIwIiBzdG9wLW9wYWNpdHk9IjAuNjIiLz48c3RvcCBvZmZzZXQ9IjAuOCIgc3RvcC1jb2xvcj0iI2U4NDcxYSIgc3RvcC1vcGFjaXR5PSIwLjc0Ii8+PHN0b3Agb2Zmc2V0PSIwLjkyIiBzdG9wLWNvbG9yPSIjZTk1MDEyIiBzdG9wLW9wYWNpdHk9IjAuODkiLz48c3RvcCBvZmZzZXQ9IjEiIHN0b3AtY29sb3I9IiNlYTU2MGQiLz48L3JhZGlhbEdyYWRpZW50PjwvZGVmcz48ZyBpZD0iRWJlbmVfMiIgZGF0YS1uYW1lPSJFYmVuZSAyIj48ZyBpZD0iRWJlbmVfMS0yIiBkYXRhLW5hbWU9IkViZW5lIDEiPjxwYXRoIGNsYXNzPSJjbHMtMSIgZD0iTTg0LjE5LDguNTRsMC0uMzNMMTYuNDgsMTYuMzRoLS4xM2wtOC42LDY3LjguMzMsMCw4LjU3LTY3LjU0Wm0wLDMuNTEtLjA2LS4zMkwyMy40NywyMy4zNGwtLjExLDBMMTEuMDcsODQuMTRsLjMyLjA2TDIzLjY0LDIzLjY0Wm0wLTEuMTctLjA2LS4zM0wyMS4xNCwyMSwyMSwyMSwxMCw4NC4xNGwuMzMuMDYsMTEtNjIuODlabTAtMS4xNywwLS4zM0wxOC44MSwxOC42N2wtLjEyLDBMOC44NSw4NC4xNWwuMzMsMEwxOSwxOVptMCwzLjUxLS4wNy0uMzJMMjUuOCwyNS42N2wtLjEsMEwxMi4xOCw4NC4xM2wuMzIuMDhMMjYsMjZabTAsMS4xNy0uMDgtLjMyTDI4LjEzLDI4LDI4LDI4LDEzLjI4LDg0LjEzbC4zMi4wOCwxNC43LTU1LjlabTAsMi4zNS0uMS0uMzJMMzIuNzgsMzIuNjhsLS4wOCwwTDE1LjUsODQuMTJsLjMxLjFMMzMsMzNabTAsMS4xNy0uMTEtLjMyTDM1LjExLDM1LDM1LDM1LDE2LjYxLDg0LjExbC4zMS4xMkwzNS4zLDM1LjNabTAtMi4zNS0uMDktLjMyTDMwLjQ2LDMwLjM0bC0uMDksMC0xNiw1My43NS4zMi4xTDMwLjYzLDMwLjY0Wm0wLTguMiwwLS4zMy03MCw3SDE0TDYuNjQsODQuMTVsLjMzLDAsNy4zNS02OS44N1ptMC00LjY4VjIuMzRMNC42Nyw0LjY3LDIuMjEsODQuMTdoLjM0TDUsNVptMC0yLjY4SDBWODQuMTdILjMzVi4zM0g4NC4xN1ptMCwxLjVWMS4xN0wyLjUsMi4zM0gyLjM0TDEuMTEsODQuMTdoLjMzTDIuNjYsMi42NlptMCwyLjM1LDAtLjMzTDcuMTYsN0g3TDMuMzIsODQuMTZsLjMzLDBMNy4zMyw3LjMzWm0wLDIuMzQsMC0uMzNMMTEuODIsMTEuNjdoLS4xNEw1LjUzLDg0LjE2bC4zMywwTDEyLDEyWm0wLTEuMTcsMC0uMzNMOS4zNCw5LjM0LDQuNDMsODQuMTZsLjMzLDBMOS42Niw5LjY2Wm0uMDksMTkuOS0uMTktLjI3TDQ5LjA3LDQ5bC0yNS44LDM1LC4yNy4yLDI1Ljc1LTM1Wm0wLTUuODQtLjEyLS4zMUwzNy40NCwzNy4zNWwtLjA2LDBMMTcuNzIsODQuMTFsLjMuMTNMMzcuNjMsMzcuNjNaTTYzLjI5LDYzLjI5bDIxLTMxLjM4TDg0LDMxLjcybC0yMSwzMS4zM0wzMCw4NGwuMTguMjgsMzMuMTMtMjFabTIxLTMzLjcxLS4yNS0uMjItMjUuNjUsMjlMMjcuNzIsODRsLjIxLjI2LDMwLjctMjUuNjlaTTY1LjYyLDY1LjYzbDE4LjctMzIuNTZMODQsMzIuOSw2NS4zOCw2NS4zOCwzMS4wNyw4NGwuMTYuM0w2NS41OCw2NS42NVpNODQuMzMsMzUuMzksODQsMzUuMjcsNzAsNzAsMzMuMyw4NGwuMTIuMzEsMzYuODgtMTRabTAtNy0uMjQtLjI0LTI4LDI3Ljg3LTI5LjQ0LDI4LC4yMy4yNCwyOS40NS0yOFpNNjgsNjgsODQuMzIsMzQuMjNsLS4zLS4xNEw2Ny43MSw2Ny43MSwzMi4xOCw4NGwuMTQuM0w2Ny45MSw2OFpNODQuMywzMC43NSw4NCwzMC41NCw2MC43Miw2MC43MiwyOC44NCw4NGwuMTkuMjdMNjAuOTMsNjFabTAtOS4zNC0uMTUtLjI5TDQyLjA5LDQybDAsMEwxOS45Myw4NC4wOWwuMy4xNiwyMi4wNi00MlptMC0xLjE2LS4xNC0uMzFMMzkuNzcsMzkuNjhsLS4wNiwwTDE4LjgzLDg0LjFsLjMuMTRMNDAsNDBabTAsMi4zMy0uMTYtLjI5TDQ0LjQyLDQ0LjM2bDAsMEwyMSw4NC4wOWwuMjkuMTdMNDQuNjIsNDQuNjNabTAsMS4xNy0uMTgtLjI4TDQ2Ljc1LDQ2LjY5bDAsMEwyMi4xNiw4NC4wOGwuMjcuMThMNDcsNDdabTAsMy41TDg0LjA2LDI3LDUzLjcxLDUzLjcyLDI1LjQ5LDg0LjA2bC4yNS4yMkw1NCw1NFptMC0xLjE2LS4yLS4yN0w1MS4zNyw1MS40bC0yNywzMi42Ny4yNi4yMSwyNy0zMi42NVptLjA3LDE1LjExSDg0LjJMODQuMzQsNDBsLS4yMSwwLC4yLTEuMTQtLjI4LS4wNS4yOC0xLjExTDg0LDM3LjY0LDc0LjcsNzQuNywzNS42NSw4NGwzNy0xMS4zNEw4NC4zMywzNi41NWwtLjMyLS4xTDcyLjM3LDcyLjM3LDM0LjQyLDg0bC4wOS4zMiwxLS4zMS4wNy4zMSwxLS4yNS4wNS4yNSwxLjA3LS4xOCwwLC4xOSwxLjA5LS4xM3YuMTNsMS4xLS4wNnYuMDZIODQuMzR2LTQyaC0uMDdaTTM3Ljg2LDgzLjhsMzctOC44TDc1LDc1bDguOTEtMzUuNDZMNzcsNzdaTTc3LjIsNzcuMzNsLjExLDAsNi40Ny0zNS4zOUw3OS4zNSw3OS4zNWwtMzkuMiw0LjM5Wm0yLjMyLDIuMzQuMTMsMCw0LjE3LTM1LjI0TDgxLjY4LDgxLjY4LDQyLjU0LDgzLjgxWk04NCw4NEg0NWwzNi44My0ySDgybDItMzVaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMjUuMjgsNTZ2NS4xNWEuNjcuNjcsMCwwLDEtMS4zMywwVjU1LjU1YS43NS43NSwwLDAsMSwuMjEtLjY2LjcyLjcyLDAsMCwxLC41OS0uMThoMy43YS42Ni42NiwwLDEsMSwwLDEuMzJaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNMzYuMTQsNTZjLS4zNywwLS44Ny0uMDgtLjg3LS42NHMuNDUtLjYzLjg3LS42M2gxLjc1YTIsMiwwLDAsMSwxLjY0LjU2LDEuODcsMS44NywwLDAsMSwuNTcsMS41MWwuMTMsMy44N2MwLC42OS0uMjgsMS4xOS0xLjE0LDEuMTloLTNhMiwyLDAsMCwxLTEuNDQtLjQ1QTEuNzcsMS43NywwLDAsMSwzNCw1OS44M3YtLjU2YTEuNjMsMS42MywwLDAsMSwuNjItMS40MSwyLjA3LDIuMDcsMCwwLDEsMS40OC0uNDhoMi43M3YtLjI5YzAtMS0uMjEtMS4xMS0xLjEzLTEuMTFabTAsMi42N2MtLjM1LDAtLjg1LjA5LS44NS41NnYuODRjMCwuNDMuNTEuNTIuODIuNTJoMi4xN2MuNDcsMCwuNjQtLjE0LjY0LS41M3YtLjQ4YzAtLjctLjItLjkxLS45LS45MVoiLz48cGF0aCBjbGFzcz0iY2xzLTIiIGQ9Ik00Nyw1NS43NVY1MS4wOGgxLjMzdjQuNjdaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNNTkuNDMsNTQuODVBMSwxLDAsMCwxLDYwLDU1VjUzYzAtLjQ0LjA5LS44OC42My0uODhzLjY0LjQ0LjY0Ljg4djcuNjFjMCwuNzMtLjMzLDEuMjctMS4xNywxLjI3SDU3LjI4YTEuOTMsMS45MywwLDAsMS0xLjA4LS40NUw1NS43Niw2MWExLjY0LDEuNjQsMCwwLDEtLjctMS40N1Y1Ny4xNGExLjY1LDEuNjUsMCwwLDEsLjctMS40N2wuNDQtLjM3YTEuNTgsMS41OCwwLDAsMSwxLjIxLS40NVptLTMuMSw0LjJjMCwuMzUsMCwuOC4yLDFsLjM4LjMxYS44Ni44NiwwLDAsMCwuNDguMjVoMi4wN0EuNTMuNTMsMCwwLDAsNjAsNjBWNTYuNzJhLjU2LjU2LDAsMCwwLS41NS0uNkg1Ny4zOWEuODYuODYsMCwwLDAtLjQ4LjI1bC0uMzguMzFjLS4yLjE2LS4yLjYxLS4yLDFaIi8+PHBhdGggY2xhc3M9ImNscy0yIiBkPSJNNzAsNjAuNTdINzFjLjQyLDAsLjg3LjA5Ljg3LjYzcy0uNS42NC0uODcuNjRINjcuNzZjLS4zNywwLS44Ny0uMDgtLjg3LS42NHMuNDUtLjYzLjg3LS42M2gxVjU2aC0xYy0uMzcsMC0uODctLjA4LS44Ny0uNjRzLjQ1LS42My44Ny0uNjNINjkuMWMuNzMsMCwuOTQuMTMuOTQuODhabS0xLjkyLTguNDNhLjUzLjUzLDAsMCwxLC41OS0uNmguNzNhLjUzLjUzLDAsMCwxLC42LjZ2LjYxYzAsLjQ1LS4yNS41OS0uNi41OWgtLjczYS41Mi41MiwwLDAsMS0uNTktLjU5WiIvPjxwYXRoIGNsYXNzPSJjbHMtMiIgZD0iTTc5LjI0LDU1Ljc1VjUxLjA4aDEuMzJ2NC42N1oiLz48cGF0aCBjbGFzcz0iY2xzLTIiIGQ9Ik04OC41LDYxLjg0YTEuMTgsMS4xOCwwLDAsMS0xLjE4LTEuMTFWNTYuMDlhMS4yOCwxLjI4LDAsMCwxLDEuMi0xLjM4aDMuNzVhMS4yLDEuMiwwLDAsMSwxLjI3LDEuMzd2Mi4xN2MwLC43My0uMTIuOTQtLjg4Ljk0SDg4LjU5VjYwYzAsLjQ0LjI0LjU0LjY2LjU0aDMuNDFjLjQzLDAsLjg4LjA5Ljg4LjYzcy0uNTEuNjQtLjg4LjY0Wm0uMDktMy45MmgzLjY4VjU2Ljc1YzAtLjQ2LS4xOC0uNzctLjY4LS43N0g4OS4zM2MtLjQ3LDAtLjc0LjMyLS43NC44N1oiLz48cGF0aCBjbGFzcz0iY2xzLTIiIGQ9Ik0xMDUuNDMsNjAuODlsLS4xMy00YzAtLjY5LDAtMS4yNy0uNTMtMS43NmExLjY3LDEuNjcsMCwwLDAtMS4wNy0uNDJIMTAwYS43LjcsMCwwLDAtLjU4LjE4Ljc1Ljc1LDAsMCwwLS4yMS42NnY1LjYzYS42Ni42NiwwLDEsMCwxLjMyLDBWNTZoMy4wN2MuMzcuMTEuNDIuNDMuNDQsMWwuMTMsMy45MmMwLC40Mi4xMi44Ni42NC44NlMxMDUuNDQsNjEuMzQsMTA1LjQzLDYwLjg5WiIvPjwvZz48L2c+PC9zdmc+'/>";
	}

	private String getBodyMessage(String subject, String htmlMessage) {

		// GENERIC
		return ("<html><head><style type=\"text/css\">" + getAnchorStyle() + getHTMLBodyStyle() + getImgStyle() + getHTMLContentWrapperStyle()
				+ getContentStyle() + getHTMLFooterStyle() + "</style></head><div class=\"content-wrapper\">" + getLogoBase64() +
				"<br /><span id=\"subject\">" + subject + "</span><br />" +
				"<span id=\"body\">" + htmlMessage +"</span><center>" +
				"<div class=\"footer\"> &copy;&nbsp;radien.io <br></div></center></div></body></html>");
	}

    protected abstract OAFAccess getOAF();

	protected abstract String getAnchorStyle();

	protected abstract String getImgStyle();

	protected abstract String getContentStyle();

	protected abstract String getHTMLBodyStyle();

	protected abstract String getHTMLContentWrapperStyle();

	protected abstract String getHTMLFooterStyle();

	public abstract Mail create(SystemUser user, SystemMailTemplate template);

	public abstract Mail create(String targetEmail, SystemMailTemplate template);

	public abstract Mail create(List<SystemUser> user, SystemMailTemplate template);

	public abstract Mail create(SystemMailTemplate template, List<String> receiverEmails);
}
