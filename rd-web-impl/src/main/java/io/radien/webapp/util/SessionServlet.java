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

package io.radien.webapp.util;

import java.io.IOException;
import javax.inject.Named;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cn.apiclub.captcha.Captcha;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet
@Named("/session")
public class SessionServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(SessionServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("CAPTCHA VALIDATION");
        if (req.getSession(false).getAttribute(Captcha.NAME) != null) {
            log.info(req.getSession(false).getAttribute(Captcha.NAME).toString());
        } else {
            log.info("NULL");
        }
        Captcha captcha = (Captcha) req.getSession(false).getAttribute(Captcha.NAME);
        log.info("CAPTCHA ANSWER");
        String captchaAnswer = req.getParameter("captchaAnswer");
        log.info(captchaAnswer);
        if(captcha == null) {
            try {
                resp.sendError(500, "Could not get captcha attribute in order to validate input.");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return;
        }

        boolean result = captcha.isCorrect(captchaAnswer);

        if(!result) {
            resp.setStatus(Response.Status.NOT_ACCEPTABLE.getStatusCode());
        }
        resp.setStatus(Response.Status.ACCEPTED.getStatusCode());
    }
}
