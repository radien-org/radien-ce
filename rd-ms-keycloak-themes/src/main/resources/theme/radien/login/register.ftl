<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password',
                                            'user.attributes.mobile_number','password-confirm'); section>
    <link href="${url.resourcesPath}/css/intlTelInput.css" rel="stylesheet" />
    <script src="${url.resourcesPath}/js/intlTelInput.js" type="text/javascript"></script>
    <script src="${url.resourcesPath}/js/utils.js" type="text/javascript"></script>
    <script src="${url.resourcesPath}/js/data.js" type="text/javascript"></script>

    <#if section = "header">
        ${msg("registerTitle")}
    <#elseif section = "form">
        <div id="loginbox" align="center" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
            <div align="center">
                <img src="${url.resourcesPath}/img/bg.png" class="login-logo-form"/>
            </div>
            <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <input 
                            type="text" 
                            id="firstName" 
                            class="${properties.kcInputClass!}" 
                            name="firstName"
                            value="${(register.formData.firstName!'')}"
                            aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>" 
                            placeholder="${msg("firstName")}"
                        />

                        <#if messagesPerField.existsError('firstName')>
                            <span id="input-error-firstname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('firstName'))?no_esc}
                            </span>
                        </#if>
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <input 
                            type="text" 
                            id="lastName" 
                            class="${properties.kcInputClass!}" 
                            name="lastName"
                            value="${(register.formData.lastName!'')}"
                            aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>" 
                            placeholder="${msg("lasttName")}"
                        />

                        <#if messagesPerField.existsError('lastName')>
                            <span id="input-error-lastname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('lastName'))?no_esc}
                            </span>
                        </#if>
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
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

                        <#if messagesPerField.existsError('email')>
                            <span id="input-error-email" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('email'))?no_esc}
                            </span>
                        </#if>
                    </div>
                </div>

                <#if !realm.registrationEmailAsUsername>
                    <div class="${properties.kcFormGroupClass!}">
                        <div class="${properties.kcInputWrapperClass!}">
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

                            <#if messagesPerField.existsError('username')>
                                <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('username'))?no_esc}
                                </span>
                            </#if>
                        </div>
                    </div>
                </#if>
                <br/>
                <#if passwordRequired??>
                    <div class="${properties.kcFormGroupClass!}">
                        <div class="${properties.kcInputWrapperClass!}">
                            <input 
                                type="password" 
                                id="password" 
                                class="${properties.kcInputClass!}" 
                                name="password"
                                autocomplete="new-password"
                                aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>" 
                                placeholder="${msg("password")}"
                            />

                            <#if messagesPerField.existsError('password')>
                                <span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('password'))?no_esc}
                                </span>
                            </#if>
                        </div>
                    </div>

                    <div class="${properties.kcFormGroupClass!}">
                        <div class="${properties.kcInputWrapperClass!}">
                            <input 
                                type="password" 
                                id="password-confirm" 
                                class="${properties.kcInputClass!}"
                                name="password-confirm"
                                aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>" 
                                placeholder="${msg("passwordConfirm")}"
                            />

                            <#if messagesPerField.existsError('password-confirm')>
                                <span id="input-error-password-confirm" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                                </span>
                            </#if>
                        </div>
                    </div>
                </#if>

                <#if recaptchaRequired??>
                    <div class="form-group">
                        <div class="${properties.kcInputWrapperClass!}">
                            <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                        </div>
                    </div>
                </#if>

                <div class="${properties.kcFormGroupClass!}">
                    <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                        <div class="${properties.kcFormOptionsWrapperClass!}">
                            <span><a href="${url.loginUrl}">< ${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
                        </div>
                    </div>

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
    </#if>
</@layout.registrationLayout>
