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
package io.radien.api;

/**
 * Enum class that contains the keys of the properties used in OAF
 *
 * @author Marco Weiland
 */
public enum OAFProperties implements SystemProperties{

	SYS_APP_NAME("application.name"),
	SYS_HOSTNAME("system.hostname"),
	SYS_APPLICATION_CONTEXT("system.application.context"),
	SYS_RUNTIME_MODE("system.runtime.mode"),
	SYS_SERVER_RUNTIME("system.server.runtime"),
	SYS_DEFAULT_LOCALE("system.default.locale"),
	SYS_SUPPORTED_LOCALES("system.supported.locales"),

    SYS_MF_APP_VERSION("app-version"),
    SYS_MF_WEBAPP_VERSION("webapp-version"),
    SYS_MF_BUILD_NUMBER("build-number"),

    SYS_CFG_DEFAULT_LANDING_PAGE("system.cfg.default.landing.page"),
    SYS_CFG_LOGIN_PAGE("system.cfg.login.page"),
    SYS_CFG_TERM_PAGE("system.cfg.term.page"),
    SYS_CFG_CONTEXT_PAGE("system.cfg.context.page"),
    SYS_CFG_JSF_MAPPING("system.cfg.jsf.mapping"),
    SYSTEM_CFG_SCIM_ENABLED ("scim.api.enabled"),
    SYSTEM_CFG_SCIM_USER_ENDPOINT ("scim.api.users.url"),
    SYS_PRETTY_FACES_ENABLED("system.cfg.pretty.faces.enabled"),

    SYS_AUTO_LOGIN_ENABLED("system.cfg.auto.login.enabled"),
    SYS_USERS_DEFAULT_PWD("system.users.default.pwd"),

    SYS_MAIL_HOST("system.mail.host"),
    SYS_MAIL_USER("system.mail.user"),
    SYS_MAIL_PASSWORD("system.mail.password"),
    SYS_MAIL_FROM_SYSTEM_ADMIN("system.mail.from.system.admin"),
    SYS_MAIL_TO_SYSTEM_ADMIN("system.mail.to.system.admin"),
    SYS_MAIL_SUBJECT("system.mail.subject"),
    SYS_MAIL_BODY("system.mail.body"),
    SYS_MAIL_SMTP_AUTH("system.mail.smtp.auth"),
	SYS_MAIL_STARTTLS_ENABLE("system.mail.smtp.starttls.enable"),
	SYS_MAIL_TRANSPORT_PROTOCOL("system.mail.transport.protocol"),
	SYS_MAIL_SMTP_PORT("system.mail.smtp.port"),

    SYS_DB_TABLES_AUTOCREATE("system.database.tables.autocreate"),
    SYS_DB_DATA_AUTOLOAD("system.database.data.autoload"),

    SYS_PERSISTENCE_UNIT("system.persistence.unit"),
    SYS_DATASOURCE("system.sql.datasource"),
    SYS_PERSISTENCE_UNIT_CUSTOM1("system.persistence.unit.custom1"),
    SYS_DATASOURCE_CUSTOM1("system.sql.datasource.custom1"),
    SYS_PERSISTENCE_UNIT_CUSTOM2("system.persistence.unit.custom2"),
    SYS_DATASOURCE_CUSTOM2("system.sql.datasource.custom2"),

    SYS_AUTHENTICATION_ENABLED("authentication.enabled"),
    SYS_AUTHENTICATION_CHECK_TERMDATE("authentication.check.termdate.enabled"),

    SYS_AUTHENTICATION_OIDC_ENABLED("authentication.oidc.enabled"),
    SYS_AUTHENTICATION_OIDC_PUBLIC_REQUIRED("authentication.oidc.public.required"),

    SYS_AUTHENTICATION_LOGOUT_REDIRECT_URL_ENABLED("authentication.logout.redirect.url.enabled"),
    SYS_AUTHENTICATION_LOGOUT_REDIRECT_URL("authentication.logout.redirect.url"),

    SYS_USER_CONTEXT_REQUIRED("oaf.user.context.mandatory"),
    SYS_USER_CONTEXT_DEFAULT_PUBLIC_CONTEXT("oaf.user.context.default-public-context"),
    SYSTEM_CONFIGURATION_CAPTCHA_ENABLED("system.configuration.captcha.enabled"),
    SYSTEM_CONFIGURATION_CAPTCHA("system.configuration.captcha"),

    SYS_DOCUMENT_SHARE("document.share.url"),
    SYS_HTTP_HEADER_X_UA_COMPATIBLE("http.header.xua.compatible"),

    SYS_DYNAMIC_APPMENU_DISPLAY_ENABLED("system.dynamic.appmenu.display.enabled"),

    MODULE_CMS_ENABLED("system.module.cms.enabled"),
    MODULE_CMS_DYNAMIC_CONTENT_MENU_ENABLED("system.module.cms.dynamic.content.menu.enabled"),

    MODULE_MONGODB_ENABLED("system.module.mongodb.enabled"),
    MODULE_SAML_ENABLED("system.module.saml.enabled"),

    SYSTEM_CMS_REPO_CONF_DIR("org.apache.jackrabbit.repository.conf"),
    SYSTEM_CMS_REPO_HOME_DIR("system.jcr.home"),
    CMS_MS_URL("cms.ms.url"),

    SCHEDULER_MS_URL("scheduler.ms.url"),
    SCHEDULER_MS_URL_REGISTER("scheduler.ms.url.register"),
    SCHEDULER_MS_URL_DELETE("scheduler.ms.url.delete"),
    SCHEDULER_MS_CONN_TIMEOUT("scheduler.ms.connection.timeout"),
    SCHEDULER_MS_READ_TIMEOUT("scheduler.ms.read.timeout"),

    INITIALIZER_DYNAMODB_REGION("initializer.dynamodb.region"),
    INITIALIZER_DYNAMODB_TABLE_NAME("initializer.dynamodb.table.name"),
    INITIALIZER_DYNAMODB_LEASE_DURATION("initializer.dynamodb.lease.duration"),
    INITIALIZER_DYNAMODB_HEARTBEAT_PERIOD("initializer.dynamodb.heartbeat.period"),
    INITIALIZER_DYNAMODB_LOCAL("initializer.dynamodb.local.execution"),

	PLUGIN_INITIALIZER_URL("plugin.initializer.url"),
    PLUGIN_INITIALIZER_CONNECTION_TIMEOUT("plugin.initializer.connection.timeout"),
    PLUGIN_INITIALIZER_READ_TIMEOUT("plugin.initializer.read.timeout"),

    SYSTEM_MS_ENDPOINT_USERMANAGEMENT("system.ms.endpoint.usermanagement"),

    SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT("system.ms.endpoint.tenantmanagement"),

    SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT("system.ms.endpoint.permissionmanagement"),

    SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT("system.ms.endpoint.rolemanagement"),
    SYSTEM_MS_ENDPOINT_ECM("system.ms.endpoint.ecm"),

	SYSTEM_MS_SECRET_ECM("system.ms.secret.ecm"),
	SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM("system.supported.languages"),
    SYSTEM_MS_CONFIG_DEFAULT_LANG_ECM("system.default.language"),

    AUTH_LOGOUT_URI("auth.logoutUri"),

    OAF_NODE_TYPES("jcr/oafnodetypes.cnd"),
    SYSTEM_CMS_CFG_NODE_ROOT("system.jcr.node.root"),
    SYSTEM_CMS_CFG_NODE_HTML("system.jcr.node.html"),
    SYSTEM_CMS_CFG_NODE_NEWS_FEED("system.jcr.node.newsfeed"),
    SYSTEM_CMS_CFG_NODE_NOTIFICATION("system.jcr.node.notifications"),
    SYSTEM_CMS_CFG_NODE_DOCS("system.jcr.node.documents"),
    SYSTEM_CMS_CFG_NODE_IMAGE("system.jcr.node.images"),
    SYSTEM_CMS_CFG_NODE_IFRAME("system.jcr.node.iframe"),
    SYSTEM_DMS_CFG_AUTO_CREATE_FOLDERS("system.jcr.document.autocreate.folder.names"),
    SYSTEM_CMS_CFG_NODE_APP_INFO("system.jcr.node.appinfo"),
    SYSTEM_CMS_CFG_NODE_STATIC_CONTENT("system.jcr.node.staticcontent"),
    SYSTEM_CMS_CFG_NODE_TAG("system.jcr.node.tag"),
    LOGIN_HOOK_ACTIVE("login_hook_active");

    private String propKey;

    /**
     * OAF Properties constructor
     * @param propKey endpoint to be constructed
     */
    OAFProperties(String propKey) {
        this.propKey = propKey;
    }

    /**
     * Gets the correct requested property key value
     * @return the property key value as a string
     */
    @Override
    public String propKey() {
        return propKey;
    }
}
