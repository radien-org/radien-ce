package io.radien.ms.email.lib;

import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.model.SystemMailTemplate;

import java.util.HashMap;
import java.util.Map;

public class MailTemplate implements SystemMailTemplate {
	private static final long serialVersionUID = 7603047380189017644L;
	private EnterpriseContent content;
	private Map<String, String> args;

	public MailTemplate(EnterpriseContent content, Map<String, String> args) {
		this.content = content;
		this.args = args;
	}

	public EnterpriseContent getContent() {
		return content;
	}

	public void setContent(EnterpriseContent content) {
		this.content = content;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(HashMap<String, String> args) {
		this.args = args;
	}

}
