<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password',
'user.attributes.mobile_number','password-confirm', 'terms'); section>
    <link href="${url.resourcesPath}/css/intlTelInput.css" rel="stylesheet" />
    <script src="${url.resourcesPath}/js/intlTelInput.js" type="text/javascript"></script>
    <script src="${url.resourcesPath}/js/utils.js" type="text/javascript"></script>
    <script src="${url.resourcesPath}/js/data.js" type="text/javascript"></script>

    <#if section = "header">
        ${msg("registerTitle")}
    <#elseif section = "form">

        <script>
            window.onload = function() {
                let uuid = crypto.randomUUID();
                let captchaURL = "${properties.radCaptchaServlet}" + "?uuid=" + uuid;

                document.getElementById("captcha_uuid_value").setAttribute('value', uuid);
                document.getElementById("radCaptcha").setAttribute("src", captchaURL);
            }
        </script>

        <div id="loginbox" align="center" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
            <div align="center">
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
            </div>
            <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <#if messagesPerField.existsError('firstName')>
                            <span id="input-error-firstname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('firstName'))?no_esc}
                            </span>
                        </#if>
                        <input
                                type="text"
                                id="firstName"
                                class="${properties.kcInputClass!}"
                                name="firstName"
                                value="${(register.formData.firstName!'')}"
                                aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>"
                                placeholder="${msg("firstName")}"
                        />
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <#if messagesPerField.existsError('lastName')>
                            <span id="input-error-lastname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('lastName'))?no_esc}
                            </span>
                        </#if>
                        <input
                                type="text"
                                id="lastName"
                                class="${properties.kcInputClass!}"
                                name="lastName"
                                value="${(register.formData.lastName!'')}"
                                aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>"
                                placeholder="${msg("lastName")}"
                        />
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <#if messagesPerField.existsError('email')>
                            <span id="input-error-email" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('email'))?no_esc}
                            </span>
                        </#if>
                        <input
                                type="text"
                                id="email"
                                class="${properties.kcInputClass!}"
                                name="email"
                                value="${(register.formData.email!'')}"
                                autocomplete="email"
                                aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"
                                placeholder="${msg("email")}"
                        />
                    </div>
                </div>

                <#if !realm.registrationEmailAsUsername>
                    <div class="${properties.kcFormGroupClass!}">
                        <div class="${properties.kcInputWrapperClass!}">
                            <#if messagesPerField.existsError('username')>
                                <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('username'))?no_esc}
                                </span>
                            </#if>
                            <input
                                    type="text"
                                    id="username"
                                    class="${properties.kcInputClass!}"
                                    name="username"
                                    value="${(register.formData.username!'')}"
                                    autocomplete="username"
                                    aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"
                                    placeholder="${msg("username")}"
                            />
                        </div>
                    </div>
                </#if>
                <#if passwordRequired??>
                    <div class="${properties.kcFormGroupClass!}">
                        <div class="${properties.kcInputWrapperClass!}">
                            <#if messagesPerField.existsError('password')>
                                <span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    <#--${kcSanitize(messagesPerField.get('password'))?no_esc}-->
                                    ${msg("invalidPasswordGenericProctecPolicies")}
                                </span>
                            </#if>
                            <input
                                    type="password"
                                    id="password"
                                    class="${properties.kcInputClass!}"
                                    name="password"
                                    autocomplete="new-password"
                                    aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"
                                    placeholder="${msg("password")}"
                            />
                        </div>
                    </div>

                    <div class="${properties.kcFormGroupClass!}">
                        <div class="${properties.kcInputWrapperClass!}">
                            <#if messagesPerField.existsError('password-confirm')>
                                <span id="input-error-password-confirm" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                                </span>
                            </#if>
                            <input
                                    type="password"
                                    id="password-confirm"
                                    class="${properties.kcInputClass!}"
                                    name="password-confirm"
                                    aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"
                                    placeholder="${msg("passwordConfirm")}"
                            />
                        </div>
                    </div>
                </#if>

                <div class="${properties.kcFormGroupClass!}">
                    <#if messagesPerField.existsError('captcha_value')>
                        <span id="input-error-captcha" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('captcha_value'))?no_esc}
                            </span>
                    </#if>
                    <div class="${properties.kcInputWrapperClass!} captcha-container">
                        <div class="captcha-img-wrapper">
                            <img src="" alt="captcha" id="radCaptcha" class="captcha_img"/>
                        </div>
                        <input
                                type="text"
                                id="captcha_val"
                                name="captcha_value"
                                value="${(register.formData['captcha_value']!'')}"
                                aria-invalid="<#if messagesPerField.existsError('captcha_value')>true</#if>"
                                placeholder="${msg("captcha_value")}"
                        />


                        <input type="hidden"
                               id="captcha_uuid_value"
                               name="captcha_uuid_value"
                               value="${(register.formData['captcha_uuid_value']!'')}"/>
                    </div>
                </div>

                <#if recaptchaRequired??>
                    <div class="form-group">
                        <div class="${properties.kcInputWrapperClass!}">
                            <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                        </div>
                    </div>
                </#if>

                <div class="${properties.kcFormGroupClass!} register-terms-link-content">
                    <#if messagesPerField.existsError('terms')>
                        <span id="input-error-terms" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('missingTermsAndConditions'))?no_esc}
                                </span>
                    </#if>
                        <div class="form-group-checkmark">
                            <input type="checkbox"
                                   id="terms"
                                   class="${properties.kcInputClass!}"
                                   name="terms"
                                   aria-invalid="<#if messagesPerField.existsError('terms')>true</#if>"/>
                            <label class="form-label-checkmark" for="terms">
                                <div class="checkmark-container">
                                    <div class="checkmark-container--inner">
                                    </div>
                                    <div class="checkmark-sign-container"> <img class="checkmark-img" src="${url.resourcesPath}/img/ProTec_Software_Icon_check-2B4645.svg"> </div>
                                </div>
                                <span>${msg("termsAgreement")} <a href='${properties.AGB_URL}' target="_blank">${msg("AGB")}</a> ${msg("termsAgreementFinal")}</span>
                            </label>
                        </div>

                    <#if messagesPerField.existsError('Dataprivacy')>
                        <span id="input-error-terms" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('missingDataprivacy'))?no_esc}
                                </span>
                    </#if>
                        <div class="form-group-checkmark">
                            <input
                                    type="checkbox"
                                    id="terms_2"
                                    class="${properties.kcInputClass!}"
                                    name="terms_2"
                                    aria-invalid="<#if messagesPerField.existsError('Dataprivacy')>true</#if>"/>
                            <label class="form-label-checkmark" for="terms_2">
                                <div class="checkmark-container">
                                    <div class="checkmark-container--inner">
                                    </div>
                                    <div class="checkmark-sign-container"> <img class="checkmark-img" src="${url.resourcesPath}/img/ProTec_Software_Icon_check-2B4645.svg"> </div>
                                </div>
                                <span>${msg("termsAgreement")} <a href='${properties.LEGAL_DATA_URL}' target="_blank">${msg("Datenschutzrichtlinie")}</a> ${msg("termsAgreementFinal")}</span>
                            </label>
                        </div>

                        <#if messagesPerField.existsError('terms')>
                            <span id="input-error-terms" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('terms'))?no_esc}
                                </span>
                        </#if>

                    <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                        <input
                                class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                                type="submit"
                                value="${msg("doRegister")}"
                        />
                    </div>

                </div>

            </form>
        </div>

        <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-registration-link up-division-line">
            <div class="${properties.kcFormOptionsWrapperClass!} reg-footer-msg">
                <span class="form-msg">${kcSanitize(msg("backToLogin"))?no_esc} <a href="${url.loginUrl}">${kcSanitize(msg("doLogIn"))?no_esc}</a></span>
            </div>
        </div>

    </#if>
</@layout.registrationLayout>
