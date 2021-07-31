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
package io.radien.ms.rolemanagement.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that offer common features to be shared
 * among different resources
 */
public abstract class LoggableResource {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Utility method to log messages (in a cleaner approach)
     * @param msg message to be logged
     * @param params message params
     */
    protected void log(String msg, Object... params) {
        if (msg != null) {
            String formattedMsg = params != null ? String.format(msg, params) : msg;
            log.error(formattedMsg);
        }
    }
}
