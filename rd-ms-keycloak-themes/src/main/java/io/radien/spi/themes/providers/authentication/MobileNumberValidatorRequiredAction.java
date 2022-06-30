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

package io.radien.spi.themes.providers.authentication;

import io.radien.spi.themes.gateway.sms.SmsServiceFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.theme.Theme;
import org.keycloak.utils.StringUtil;

public class MobileNumberValidatorRequiredAction implements RequiredActionProvider {
    private static final Logger LOG = Logger.getLogger(SmsServiceFactory.class);
    public static final String PROVIDER_ID = "mobile_otp_validator";

    private static final String TPL_FILE = "mobile_two_step_verification.ftl";
    private static final String USER_ATTRIBUTE = "mobile_number";
    private static final String MOBILE_NUMBER_ATTRIBUTE = "user.attributes.mobile_number";
    private static final String CODE_PARAMETER = "code";
    private static final String ENV_SIMULATION_KEY = "AWS_SNS_SIMULATION";
    private static final String ENV_SENDER_ID_KEY = "AWS_SNS_SENDER_ID";
    private static final String ENV_SMS_LENGTH_KEY = "AWS_SNS_MESSAGE_LENGTH";
    private static final String ENV_SMS_TTL_KEY = "AWS_SNS_TTL";


    @Override
    public void evaluateTriggers(RequiredActionContext requiredActionContext) {

    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        Response challenge = context.form().createForm(TPL_FILE);
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        if(context.getHttpRequest().getDecodedFormParameters().containsKey("sendSMS")) {
            KeycloakSession session = context.getSession();
            String mobileNumber = context.getHttpRequest().getDecodedFormParameters().getFirst(MOBILE_NUMBER_ATTRIBUTE);

            int length = Integer.parseInt(System.getenv(ENV_SMS_LENGTH_KEY));
            int ttl = Integer.parseInt(System.getenv(ENV_SMS_TTL_KEY));
            Map<String, String> config = new HashMap<>();
            config.put("simulation", System.getenv(ENV_SIMULATION_KEY));
            config.put("senderId", StringUtil.isBlank(System.getenv(ENV_SENDER_ID_KEY)) ? System.getenv(ENV_SENDER_ID_KEY) : "RADIEN");

            String code = SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
            AuthenticationSessionModel authSession = context.getAuthenticationSession();
            authSession.setAuthNote(CODE_PARAMETER, code);
            authSession.setAuthNote(SmsAuthenticatorFactory.TTL_CONFIG, Long.toString(System.currentTimeMillis() + (ttl * 1000L)));

            try {
                Theme theme = session.theme().getTheme(Theme.Type.LOGIN);
                Locale locale = session.getContext().resolveLocale(context.getUser());
                String smsAuthText = theme.getMessages(locale).getProperty("smsAuthText");
                String smsText = String.format(smsAuthText, code, Math.floorDiv(ttl, 60));
                SmsServiceFactory.get(config).send(mobileNumber, smsText);
                context.challenge(
                        context
                                .form()
                                .setAttribute("field_mobile_number", mobileNumber)
                                .createForm(TPL_FILE)
                );
            } catch (Exception e) {
                LOG.warn("Error during SMS send process", e);
                context.failure();
            }
        } else if(context.getHttpRequest().getDecodedFormParameters().containsKey("submit")) {
            String mobileNumber = context.getHttpRequest().getDecodedFormParameters().getFirst(MOBILE_NUMBER_ATTRIBUTE);
            String enteredCode = context.getHttpRequest().getDecodedFormParameters().getFirst(CODE_PARAMETER);

            AuthenticationSessionModel authSession = context.getAuthenticationSession();
            String code = authSession.getAuthNote(CODE_PARAMETER);
            String ttl = authSession.getAuthNote(SmsAuthenticatorFactory.TTL_CONFIG);

            if (code == null || ttl == null) {
                context.failure();
                return;
            }

            boolean isValid = enteredCode.equals(code);
            if (isValid) {
                if (Long.parseLong(ttl) < System.currentTimeMillis()) {
                    context.challenge(
                            context.form()
                                .setAttribute("field_mobile_number", mobileNumber)
                                .addError(new FormMessage(MOBILE_NUMBER_ATTRIBUTE, "Invalid code. Please request a new code."))
                                .createForm(TPL_FILE)
                    );
                } else {
                    context.getUser()
                            .setAttribute(USER_ATTRIBUTE, Collections.singletonList(mobileNumber));
                    context.success();
                }
            } else {
                context.challenge(
                        context.form()
                                .setAttribute("field_mobile_number", mobileNumber)
                                .addError(new FormMessage(MOBILE_NUMBER_ATTRIBUTE, "Invalid code. Please request a new code."))
                                .createForm(TPL_FILE)
                );
            }
        }
    }

    @Override
    public void close() {

    }
}
