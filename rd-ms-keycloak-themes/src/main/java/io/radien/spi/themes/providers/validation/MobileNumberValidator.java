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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.ws.rs.core.MultivaluedMap;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileNumberValidator implements FormAction, FormActionFactory {
    private static final Logger log = LoggerFactory.getLogger(MobileNumberValidator.class);

    private static final String PROVIDER_ID = "organization-field-validation-action";
    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = { AuthenticationExecutionModel.Requirement.REQUIRED, AuthenticationExecutionModel.Requirement.DISABLED };

    public static final String FIELD_PHONE_NUMBER = "user.attributes.mobile_number";
    public static final String MISSING_PHONE_NUMBER = "missing_mobile_number";
    public static final String INVALID_PHONE_NUMBER = "invalid_mobile_number";

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
        return "Mobile Number Profile Validation";
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
        return Collections.singletonList(
                new ProviderConfigProperty("regex", "Regular Expression", "Regular Expression to Match a Valid Mobile Number.", ProviderConfigProperty.STRING_TYPE, "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$")
        );
    }

    @Override
    public String getHelpText() {
        return "Validates mobile number field.";
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
        AuthenticatorConfigModel config = validationContext.getAuthenticatorConfig();
        String regex = config.getConfig().getOrDefault("regex", "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");
        MultivaluedMap<String, String> formData = validationContext.getHttpRequest().getDecodedFormParameters();
        List<FormMessage> errors = new ArrayList<>();

        String phoneNumber = formData.getFirst(FIELD_PHONE_NUMBER);
        boolean success = true;
        if (Validation.isBlank(phoneNumber)) {
            errors.add(new FormMessage(FIELD_PHONE_NUMBER, MISSING_PHONE_NUMBER));
            success = false;
        }
        if(!Pattern.compile(regex).matcher(phoneNumber).matches()) {
            errors.add(new FormMessage(FIELD_PHONE_NUMBER, INVALID_PHONE_NUMBER));
            success = false;
        }
        if(success) {
            validationContext.success();
        } else {
            validationContext.validationError(formData, errors);
        }
    }

}
