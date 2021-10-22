/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.kernel.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFProperties;
import io.radien.kernel.OpenAppframe;

/**
 * Phase Listener for User Agent headers in Render response JSF phase
 *
 * @author Marco Weiland
 */
public class UACompatibleHeaderPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(UACompatibleHeaderPhaseListener.class);

    private static final String X_UA_COMPATIBLE_HEADER = "X-UA-Compatible";

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
     */
    public void afterPhase(PhaseEvent event) {
        //NO need to implement afterPhase logic
    }

    /*
     * (non-Javadoc)s
     *
     * @see
     * javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
     */
    public void beforePhase(PhaseEvent event) {
        try {
            final FacesContext facesContext = event.getFacesContext();
            final HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            OpenAppframe app = facesContext.getApplication().evaluateExpressionGet(facesContext,
                    OpenAppframe.OAF_EL_RESOLVE_NAME, OpenAppframe.class);
            if (app != null) {
                String headerValue = app.getProperty(OAFProperties.SYS_HTTP_HEADER_X_UA_COMPATIBLE);
                response.addHeader(X_UA_COMPATIBLE_HEADER, headerValue);
            }
        } catch (Exception e) {
            log.error("Error in method beforePhase", e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.event.PhaseListener#getPhaseId()
     */
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}
