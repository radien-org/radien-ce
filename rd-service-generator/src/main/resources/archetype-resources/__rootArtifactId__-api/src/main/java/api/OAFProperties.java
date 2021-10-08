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
package ${package}.api;

/**
 * Enum class that contains the keys of the properties used in OAF
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public enum OAFProperties implements SystemProperties{

	SYS_APP_NAME("application.name"),
    SYS_RUNTIME_MODE("system.runtime.mode"),

	SYS_DEFAULT_LOCALE("system.default.locale"),
	SYS_SUPPORTED_LOCALES("system.supported.locales"),

    SYS_MF_APP_VERSION("app-version"),
    SYS_MF_WEBAPP_VERSION("webapp-version"),
    SYS_MF_BUILD_NUMBER("build-number"),

    SYS_CFG_DEFAULT_LANDING_PAGE("system.cfg.default.landing.page"),
    SYS_CFG_TERM_PAGE("system.cfg.term.page"),
    SYS_PRETTY_FACES_ENABLED("system.cfg.pretty.faces.enabled"),
    SYS_CFG_JSF_MAPPING("system.cfg.jsf.mapping"),
    SYS_DYNAMIC_APPMENU_DISPLAY_ENABLED("system.dynamic.appmenu.display.enabled"),

    SYS_DB_TABLES_AUTOCREATE("system.database.tables.autocreate"),
    SYS_DB_DATA_AUTOLOAD("system.database.data.autoload"),
    SYS_PERSISTENCE_UNIT("system.persistence.unit"),

    SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT("system.ms.endpoint.${entityResourceName.toLowerCase()}management");

    private String propKey;

    OAFProperties(String propKey) {
        this.propKey = propKey;
    }

    @Override
    public String propKey() {
        return propKey;
    }
}
