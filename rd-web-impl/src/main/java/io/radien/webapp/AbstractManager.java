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
package io.radien.webapp;

import java.util.HashMap;
import java.util.Map;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Newton Carvalho
 * Base class for all JSF Manager Beans
 */
public abstract class AbstractManager implements Serializable {

    private static final long serialVersionUID = 1660384525695042610L;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String JSON_OPEN_BRACKET = "{";
    private static final String JSON_CLOSE_BRACKET = "}";

    protected void handleError(Exception e, String pattern, Object ... params) {
        String msg = MessageFormat.format(pattern, params);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Flash flash = facesContext.getExternalContext().getFlash();
        flash.setKeepMessages(true);
        flash.setRedirect(true);
        facesContext.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, msg, extractErrorMessage(e)));
        log.error(msg, e);
    }

    protected void handleMessage(FacesMessage.Severity severity, String pattern, Object ... params) {
        String msg = MessageFormat.format(pattern, params);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Flash flash = facesContext.getExternalContext().getFlash();
        flash.setKeepMessages(true);
        flash.setRedirect(true);
        facesContext.addMessage(null, new FacesMessage(severity, msg, null));
        log.info(msg);  
    }

    protected String extractErrorMessage(Exception exception) {
        if(exception.getMessage() != null) {
            String errorMsg = (exception instanceof EJBException) ?
                    exception.getCause().getMessage() : exception.getMessage();
            // Bootsfaces growl has issues to handle special characters
            return errorMsg.replace("\n\t", "");
        }
        return "";
    }

    public Logger getLog() {
        return log;
    }


    /**
     * Redirects the user to the Radien home page can be appended with a message or not
     * @throws IOException in case of redirection error
     */
    protected void redirectToHomePage() throws IOException {
        redirectToPage(DataModelEnum.PUBLIC_INDEX_PATH.getValue());
    }


    /**
     * Redirect the navigation to a specified page
     * @param pagePath uri or (sub) path that designates the page (ex: /module/users, or /public/index)
     * @throws IOException in case of any i/o issue during the dispatch process
     */
    protected void redirectToPage(String pagePath) throws IOException {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + pagePath);
    }

    /**
     * Given a {@link Exception}, extracts a {@link HashMap} that corresponds to
     * an error descriptor (that eventually exists as a json encapsulated message).
     * @param exception {@link Exception} instance
     * @return HashMap containing attributes regarding error obtained from Exception message,
     * or null if the encapsulated messaged does not contain a Json descriptor.
     */
    public Map<?,?> getWrappedErrorDescriptor(Exception exception) {
        String originalMessage = exception.getMessage();
        int index1 = originalMessage.indexOf(JSON_OPEN_BRACKET);
        int index2 = originalMessage.lastIndexOf(JSON_CLOSE_BRACKET);
        if (index1 !=-1 && index2 != -1) {
            String message = originalMessage.substring(index1, index2+1);
            try (Jsonb jsonb = JsonbBuilder.create()) {
                return jsonb.fromJson(message, HashMap.class);
            }
            catch (Exception e) {
                log.error("error closing resource", e);
            }
        }
        return null;
    }
}
