<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("emailVerifyTitle", idpDisplayName)}
    <#elseif section = "form">
        <div id="loginbox">
            <div align="center">
                <img src="${url.resourcesPath}/img/ProTecSports_Logo_Wortmarke.png" class="login-logo-form"/>
            </div>
            <div style="display: flex; flex-direction: column; align-items: center">
                <div id="instruction1" class="instruction generic-paragraph">
                    ${kcSanitize(msg("emailVerifyInstruction1"))?no_esc}
                </div>
                <br>
                <div id="instruction2" class="instruction generic-paragraph">
                    ${msg("emailVerifyInstruction2")?no_esc}
                    <#--                <a href="${url.loginAction}" class="btn btn-outline-primary text-decoration-none" role="button">${msg("emailVerifyInstruction3")}</a>-->
                </div>
                <div id="instruction2" class="instruction generic-paragraph">
                    <a href="${url.loginAction}" style="text-decoration: underline">${msg("doClickHereVerif")}</a> ${msg("emailVerifyInstruction3")}
                    <#--                <a href="${url.loginAction}" class="btn btn-outline-primary text-decoration-none" role="button">${msg("emailVerifyInstruction3")}</a>-->
                </div>
                <br>
                <br>
            </div>
        </div>
        <div id="kc-form-options" class="${properties.kcFormOptionsClass!} login-registration-link up-division-line divisionLogin">
            <div class="${properties.kcFormOptionsWrapperClass!}">
                <span class="instruction">${kcSanitize(msg("backToLogin"))?no_esc} <a href="${url.loginUrl}">${kcSanitize(msg("doLogIn"))?no_esc}.</a></span>
            </div>
        </div>

    </#if>
</@layout.registrationLayout>