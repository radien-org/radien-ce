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

package io.radien.spi.themes.providers.validation;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import kong.unirest.Unirest;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;

public class CaptchaValidator implements FormAction, FormActionFactory {
    private static final Logger log = Logger.getLogger(CaptchaValidator.class);

    private static final String PROVIDER_ID = "captcha-field-validation-action";
    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = { AuthenticationExecutionModel.Requirement.REQUIRED, AuthenticationExecutionModel.Requirement.DISABLED };

    public static final String CAPTCHA = "captcha_valuer";
    public static final String MISSING_CAPTCHA = "missing_captcha";
    public static final String INVALID_CAPTCHA = "invalid_captcha";

    @Override
    public void close() {
    }

    @Override
    public FormAction create(KeycloakSession keycloakSession) {
        return this;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public String getDisplayType() {
        return "Captcha Validator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return new ArrayList<>();
    }

    @Override
    public String getHelpText() {
        return "Captcha.";
    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void success(FormContext context) {
    }

    @Override
    public void validate(ValidationContext validationContext) {
        MultivaluedMap<String, String> formData = validationContext.getHttpRequest().getDecodedFormParameters();
        List<FormMessage> errors = new ArrayList<>();
        log.info("CAPTCHA VALUE " + formData.getFirst("captcha_value"));
        String targetURL = System.getenv("RAD_SESSION_SERVLET") == null ? "https://int.radien.io/web/public/session" : System.getenv("RAD_SESSION_SERVLET");
        try {
            URL url = new URL(targetURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String cookiesHeader = connection.getHeaderField("Set-Cookie");
            List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
            cookies.forEach(cookie -> log.info(cookie.getName() + "  " + cookie.getValue()));
            log.info("RESPONSE FROM HTTP URL " + connection.getResponseCode());
        } catch (IOException e) {
            log.info("ERROR", e);
        }

        int responseCode = Unirest
                .get(targetURL)
                .queryString("captchaAnswer", formData.getFirst("captcha_value"))
                .asString().getStatus();
        log.info(responseCode);

        boolean success = responseCode == Response.Status.ACCEPTED.getStatusCode();
        if(success) {
            validationContext.success();
        } else {
            errors.add(new FormMessage("captcha_value", "Invalid Captcha"));
            validationContext.validationError(formData, errors);
        }
    }

}
