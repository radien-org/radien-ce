<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=!messagesPerField.existsError('username'); section>
    <#if section = "header">
        ${msg("emailForgotTitle")}
    <#elseif section = "form">
        <div id="loginbox" align="center" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
            <div align="center">
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
            </div>
            <form id="kc-reset-password-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                <div class="instruction">
                    ${kcSanitize(msg('forgotPasswordInstruction1'))?no_esc}
                </div>
                <br/>
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcInputWrapperClass!}">
                        <#if messagesPerField.existsError('username')>
                            <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(msg('forgotPasswordError'))?no_esc}
                        </span>
                        </#if>
                        <input
                                type="text"
                                id="username"
                                class="${properties.kcInputClass!}"
                                name="username"
                                autofocus
                                value="${(auth.attemptedUsername!'')}"
                                aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"
                                placeholder="${msg("forgotPasswordLabel")}"
                        />
                    </div>
                </div>
                <br>
                <div id="kc-form-buttons" style="margin-top:10px" class="${properties.kcFormButtonsClass!}">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <input tabindex="4" class="${properties.kcButtonClass!}" id="kc-login" type="submit" value="${msg("forgotPasswordSubmit")}"/>
                    </div>
                </div>
            </form>
        </div>
        <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-registration-link up-division-line divisionLogin" style="margin-top: 3rem">
            <div class="${properties.kcFormOptionsWrapperClass!} reg-footer-msg">
                <span class="instruction">${kcSanitize(msg("forgotPasswordBackToLogin"))?no_esc} &nbsp;<a href="${url.loginUrl}">${kcSanitize(msg("doLogInReset"))?no_esc}.</a></span>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>
